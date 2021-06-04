package com.company.Server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread{
    Socket socket;
    Server sv;
    String name = null;
    String id = null;
    String ip = null;
    //String pw = null;
    DataInputStream input;
    DataOutputStream output;

    public ServerThread(Server server, Socket socket) {
        //생성자
        this.sv = server;
        this.socket = socket;
    }
    @Override
    public void run() {
        //오버라이딩을 통해 쓰레드 실행
        try {
            System.out.println("클라이언트와 연결 완료");
            input = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            String[] msg;
            boolean marker;

            //수정필요... 로그인 회원가입 분리
            //로그인 과정
            while(true) {
                //인풋 데이터를 '//' 단위로 쪼갠다. 배열 형태로 나눠져서 들어가게됨
                //msg[0] = id , msg[1] = pw,
                msg = input.readUTF().split("//");
                if (msg[0].equals("dc")) {
                    marker = sv.doublecheck(msg[1]);
                    if (marker) {
                        output.writeUTF("dcok");
                    } else {
                        output.writeUTF("dcnok");
                        continue;
                    }
                }
                else if (msg[0].equals("nc")) {
                    marker = sv.nicknameCheck(msg[1]);
                    if (marker) {
                        output.writeUTF("ncok");
                    } else {
                        output.writeUTF("ncnok");
                        continue;
                    }
                }
                else if (msg[0].equals("ip")) {
                    ip = msg[1];
                    System.out.println("연결된 Client Ip : " + ip);
                }
                else if (msg[0].equals("rg")) {
                    //회원가입 신호가 전달되었을 경우
                    sv.registerDatabase(msg[1], msg[2], msg[3]);
                }
                else if (msg[0].equals("lg")) {
                    name = sv.loginDatabase(msg[1], msg[2]);
                    if (name != null) {
                        id = msg[1];
                        //로그인 성공 후 입장완료 메세지를 위해서 닉네임을 보내준다.
                        output.writeUTF("lgok//" + name +"//" + id);

                        //현재 쓰레드의 클라인언트에게 이미 입장한 유저목록을 보내준다
                        sv.enteredUser(this);
                        sv.addClient(this);

                        //새로운 녀석이 입장했다고 기존 클라이언트들에게 전송
                        sv.broadCasting("NewUser//" + name + "//" + getIp());


                        //update를 부여해줌으로써 각 클라이언트들이 리스트를 업데이트 하게 해준다.
                        sv.broadCasting("update//");
                        //sv.broadCasting("[" + name + "]" + "님이 입장했습니다.");
                    }
                    else {
                        output.writeUTF("lgnok");
                    }
                }

                else if(msg[0].equals("bye")) {
                    id = null;
                    sv.deleteClient(this);
                    //유저가 나갔다고 각 클라이언트들에게 알려줌과 동시에 리스트 업데이트 지시
                    sv.broadCasting("userout//" + name + "//" + getIp());
                    sv.broadCasting("update//");

                    //sv.broadCasting("[" + name + "]" + " 님이 퇴장하였습니다.");
                }
                else if(msg[0].equals("rn")) {
                    sv.broadCasting("rn//" + name + "//" + msg[1]);
                    sv.updateDatabase(id, msg[1]);
                    name = msg[1];
                }
                else if (msg[0].equals("spk")) {
                    sv.broadCasting("spk//" + name);
                }
                else if (msg[0].equals("nspk")) {
                    sv.broadCasting("nspk//" + name);
                }
                else {
                    sv.broadCasting(msg[0]);
                }
            }

        } catch (Exception e) {
            System.out.println("disconnect!!");
        } finally {
            try {
                if (id != null) {
                    sv.deleteClient(this);
                    sv.broadCasting("userout//" + name + "//" + getIp());
                    sv.broadCasting("update//");
                }
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getUsername() {
        return this.name;
    }

    public String getIp() {
        return ip;
    }

    public void sendMessage(String msg) {
        try {
            output.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
