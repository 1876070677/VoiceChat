package com.company.Client;

import java.io.*;
import java.net.Socket;

public class ClientThread extends Thread{
    Socket socket;
    Client cl;
    DataInputStream in;
    DataOutputStream output;

    public ClientThread(Client client, Socket socket) {
        this.cl = client;
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new DataInputStream(socket.getInputStream());
            output = new DataOutputStream(socket.getOutputStream());
            int cnt = 1;
            String list = "";
            String msg[];

            output.writeUTF("ip//" + cl.getLOCAL_IP());

            while (true) {
                if (in != null) {
                    msg = in.readUTF().split("//");
                    if (msg[0].equals("lgok")) {
                        //로그인 성공
                        cl.setName(msg[1]);
                        cl.setID(msg[2]);
                        cl.chat();
                        System.out.println("로그인 성공");
                        //cl.startReceive();
                        cl.startReceive();
                        //마이크 인식 시작
                        //cl.voiceRecognition.start();
                    }
                    else if(msg[0].equals("lgnok")) {
                        //로그인 실패
                        System.out.println("로그인 실패");
                        cl.dialogWindow();
                    }
                    else if(msg[0].equals("dcok")) {
                        //중복확인 성공
                        System.out.println("중복확인 성공");
                        cl.failIdCheck(1);
                    }
                    else if(msg[0].equals("dcnok")) {
                        //중복확인 실패
                        System.out.println("중복확인 실패");
                        cl.failIdCheck(2);
                    }
                    else if(msg[0].equals("ncok")) {
                        cl.failNicknameCheck(1);
                    }
                    else if(msg[0].equals("ncnok")) {
                        cl.failNicknameCheck(2);
                    }
                    else if(msg[0].equals("OldUser")) {
                        cl.addUser(msg[1], msg[2]);
                    }
                    else if(msg[0].equals("NewUser")) {
                        cl.addUser(msg[1], msg[2]);
                        cl.addText("[" + msg[1] + "]" + " 님이 입장했습니다.");
                    }
                    else if(msg[0].equals("update")) {
                        //유저 목록 업데이트
                        cl.updateUser();
                    }
                    else if(msg[0].equals("userout")) {
                        cl.deleteUser(msg[1], msg[2]);
                        cl.addText("[" + msg[1] + "]" + " 님이 퇴장하였습니다.");
                    }
                    else if(msg[0].equals("rn")) {
                        cl.listRename(msg[1], msg[2]);
                    }
                    else if(msg[0].equals("spk")) {
                        cl.markSpeaker(msg[1]);
                    }
                    else if(msg[0].equals("nspk")) {
                        cl.removeSpeaker(msg[1]);
                    }
                    else {
                        cl.addText(msg[0]);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loginCheck(String id, String pw) {
        try {
            output.writeUTF("lg//" + id + "//" + pw);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void idCheck(String id) {

        try {
            output.writeUTF("dc//" + id);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void nicknameCheck(String nickname) {
        try {
            output.writeUTF("nc//" + nickname);
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public void signUp(String id, String pw, String name) {
        try {
            output.writeUTF("rg//" + id + "//" + pw + "//" + name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reName(String str) {
        try {
            output.writeUTF("rn//" + str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void speaking() {
        try {
            output.writeUTF("spk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void notSpeaking() {
        try {
            output.writeUTF("nspk");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            output.writeUTF("bye");
            cl.setName("");
            cl.setID("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
