package com.company.Client;

import com.company.Audio.Receiver;
import com.company.Audio.Sender;
import com.company.Audio.VoiceRecognition;

import javax.media.rtp.*;
import javax.media.rtp.event.SessionEvent;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Vector;
import javax.imageio.ImageIO;

public class Client extends JFrame {
    Socket socket;
    private final int SERVER_PORT = 8888;
    private String SERVER_IP = "";
    private String LOCAL_IP = "";
    private String ID = "";
    //DataInputStream in;
    //DataOutputStream output;
    ClientThread thread;
    SignUp signUp;
    ClientChat clientChat;
    Vector user_info;
    Vector user_ip;
    ClientRename clientRename;
    VoiceRecognition voiceRecognition;

    RTPManager rtpManager;
    Receiver receiver = null;
    Sender sender = null;

    //사용자 이름
    private String name = "";

    private JFrame frame;
    private JTextField textField;
    private JTextField textField_1;
    JButton btnLogin;
    JButton btnSingUp;
    BufferedImage img = null;

    public void initGUI() {
        Container C;
        C = getContentPane();
        try{
            img = ImageIO.read(new File("img/Client.png"));
        } catch (IOException e){
            System.out.println("Fail to load the Image");
            System.exit(0);
        }
        Clientpanel panel = new Clientpanel();
        panel.setBounds(0,0,334,461);



        setBounds(100, 100, 350, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        C.setLayout(null);

       /* JLabel lblNewLabel = new JLabel("MUYAHO_VoiceChat");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("굴림", Font.BOLD, 18));
        lblNewLabel.setBounds(76, 75, 202, 36);
        C.add(lblNewLabel); */

       /* JLabel lblNewLabel_1 = new JLabel("ID");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setFont(new Font("굴림", Font.BOLD, 14));
        lblNewLabel_1.setBounds(41, 171, 57, 32);
        C.add(lblNewLabel_1); */

        textField = new JTextField();
        textField.setColumns(10);
        textField.setBounds(110, 178, 145, 21);
        C.add(textField);

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(110, 219, 145, 21);
       C.add(textField_1);

      /*  JLabel lblNewLabel_1_1 = new JLabel("PW");
        lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1_1.setFont(new Font("굴림", Font.BOLD, 14));
        lblNewLabel_1_1.setBounds(41, 213, 57, 30);
        C.add(lblNewLabel_1_1); */

        btnLogin = new JButton("LOGIN");
        btnLogin.setFont(new Font("굴림", Font.BOLD, 13));
        btnLogin.setBounds(51, 284, 97, 23);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                thread.loginCheck(textField.getText().toString(), textField_1.getText().toString());
            }
        });
        C.add(btnLogin);

        btnSingUp = new JButton("SING UP");
        btnSingUp.setFont(new Font("굴림", Font.BOLD, 13));
        btnSingUp.setBounds(181, 284, 97, 23);
        btnSingUp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //sign up 버튼 눌럿을시
                signUpWindow();
            }
        });
        C.add(btnSingUp);
        C.add(panel);
        setResizable(false);
        setVisible(true);
    }
    class Clientpanel extends JPanel{
        public void paint(Graphics g){
            g.drawImage(img,0,0,null);
        }
    }

    public void signUpWindow() {
        signUp = new SignUp(this);
        setVisible(false);
    }

    public void idCheck(String id) {
        thread.idCheck(id);
    }

    public void nicknameCheck(String nickname) {
        thread.nicknameCheck(nickname);
    }

    public void signUp(String id, String pw, String name) {
        thread.signUp(id, pw, name);
    }

    public void dialogWindow() {
        JOptionPane.showMessageDialog(null, "로그인 실패");
    }

    public void failIdCheck(int num) {
        if (num == 1) {
            signUp.dialogWindow(num);
        }
        else if (num == 2) {
            signUp.dialogWindow(num);
        }
    }

    public void failNicknameCheck(int num) {
        if (num == 1) {
            signUp.nicknameCheckResult(num);
        }
        else if (num == 2) {
            signUp.nicknameCheckResult(num);
        }
    }

    public void disconnect() {
        clientChat = null;

        for (int i = 0 ; i < user_ip.size(); i++) {
            try {
                InetAddress tp = InetAddress.getByName(user_ip.get(i).toString());
                SessionAddress tmp = new SessionAddress(tp, 8000);
                rtpManager.removeTarget(tmp, "closed");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("IP Address Reset!!");

        //저장되있던 유저 네임 과 모든 유저 ip 초기화
        user_info.removeAllElements();
        user_ip.removeAllElements();
        thread.disconnect();

        //화자인식 종료
        //voiceRecognition.setChatting(false);
        //voiceRecognition.stop();
        //initRecognition();

        //음성통신관련 관련 메소드 전부 초기화
        sender.disconnectDatasource();
        rtpManager.dispose();
        initRtpmanager();
        setSender();
        setReceiver();

        //다시 준비완료
        System.out.println("Chat Program Ready!!");
    }

    public void userRenameWindow() {
        clientRename = new ClientRename(this);
    }

    public void userRename(String str) {
        thread.reName(str);
    }

    public void chat() {
        clientChat = new ClientChat(this);
        setVisible(false);
    }

    public void addUser(String username, String other_ip) {
        user_info.add(username);
        if (!getLOCAL_IP().equals(other_ip)) {
            user_ip.add(other_ip);
            try {
                InetAddress tg = InetAddress.getByName(other_ip);
                SessionAddress target = new SessionAddress(tg, 8000);
                rtpManager.addTarget(target);
            } catch (Exception e) {
                e.printStackTrace();
            }
            sender.startTrans();
            //receiver.startReceive();
        }
    }

    public void deleteUser(String username, String other_ip) {
        user_info.remove(username);
        user_ip.remove(other_ip);
        if (user_ip.size() < 1) {
            try {
                InetAddress tg = InetAddress.getByName(other_ip);
                SessionAddress target = new SessionAddress(tg, 8000);
                rtpManager.removeTarget(target, "this client is disconnect");
            } catch (Exception e) {
                System.out.println("rtp매니저에 타겟 주소 제거 실패");
            }
        }
    }

    public void updateUser() {
        clientChat.setList_user(user_info);
        //업데이트 할때마다 유저목록이랑 ip목록 출력을 해본다.
        System.out.println(user_info);
        System.out.println(user_ip);
    }

    public void listRename(String oldName, String newName) {
        for (int i = 0; i < user_info.size(); i++) {
            if (user_info.get(i).equals(oldName)) {
                user_info.setElementAt(newName, i);
                break;
            }
        }
        updateUser();
    }

    public void addText(String str) {
        clientChat.addText(str);
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getID() {
        return ID;
    }

    public void setID(String id) {
        this.ID = id;
    }

    public String getLOCAL_IP() {
        return LOCAL_IP;
    }

    public Client() {
        //System.load("C:/Windows/System32/jmdaudc.dll");
        SERVER_IP = "YOUR SERVER IP";
        try {
            LOCAL_IP = InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Client IP (MY) : " + LOCAL_IP);
        user_info = new Vector();
        user_ip = new Vector();

        initRtpmanager();

        setReceiver();
        setSender();
        System.out.println("Chat Program Ready!!");

        //음성 인식하기 위한 객체 생성
        //initRecognition();

        initGUI();

        try {
            socket = new Socket(SERVER_IP, SERVER_PORT);
            //in = new DataInputStream(socket.getInputStream());
            //output = new DataOutputStream(socket.getOutputStream());
            socket.setSoTimeout(0);
            thread = new ClientThread(this, socket);
            thread.start();
        } catch (UnknownHostException e) {
          System.out.println("잘못된 IP주소입니다.");
        } catch (IOException e) {
            System.out.println("접속에 실패했습니다.");
            //e.printStackTrace();
        }
    }


    //// 음성 통신 관련 함수
    public void initRtpmanager() {
        //rtp 세션 관리자 초기화
        rtpManager = RTPManager.newInstance();

        try {
            InetAddress lc = InetAddress.getByName(LOCAL_IP);
            //InetAddress tg = InetAddress.getByName(SERVER_IP);
            SessionAddress local = new SessionAddress(lc, 8000);
            SessionAddress target = new SessionAddress(lc, 9000);
            rtpManager.initialize(local);
            rtpManager.addTarget(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setReceiver() {
        receiver = new Receiver(rtpManager);
        //receiver.start();
    }

    public void setSender() {
        sender = new Sender(rtpManager);
        sender.start();
    }

    public void startReceive() {
        receiver.startReceive();
    }

    public void startSend() {
        //따로 스타트 하지 않아도 벡터에 상대방 ip가 있는경우 알아서 실행됨
        sender.startTrans();
    }

    public void stopReceive() {
        receiver.stopReceive();
    }

    public void stopSend() {
        sender.stopTrans();
    }

    public void micOn() {sender.setMicOn();}

    public void micOff() {sender.setMicOff();}

    //// 화자 인식 관련 함수
    public void initRecognition() {
        voiceRecognition = new VoiceRecognition(this);
    }

    public void imSpeaking() {
        if (!sender.getMicstop()) {
            // sender의 마이크가 오프가 되있는 상태이면 마이크 보낼 필요가 없음
            System.out.println("Im speaking....");
            thread.speaking();
        }
    }

    public void imnotSpeaking() {
        if (!sender.getMicstop()) {
            // sender의 마이크가 오프가 되있는 상태이면 마이크 보낼 필요가 없음
            thread.notSpeaking();
            System.out.println("Im not speaking....");
        }
    }

    public void markSpeaker(String name) {
        for (int i = 0; i < user_info.size(); i++) {
            if (user_info.get(i).equals(name)) {
                clientChat.markSpeakerList(i);
                break;
            }
        }
    }

    public void removeSpeaker(String name) {
        for (int i = 0; i < user_info.size(); i++) {
            if (user_info.get(i).equals(name)) {
                clientChat.markSpeakerList(i);
                break;
            }
        }
    }

    public static void main(String[] args) {
        new Client();
    }

}
