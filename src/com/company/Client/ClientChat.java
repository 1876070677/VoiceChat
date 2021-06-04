package com.company.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import static java.awt.Color.DARK_GRAY;

public class ClientChat extends JFrame {

    JFrame frame;
    //JTextArea textArea;
    JTextArea textArea_1;
    JList list_user;
    JScrollPane scrollPane_alarm;

    Client client;
    BufferedImage img = null;
    public void initGUI() {
        Container C;
        C = getContentPane();
        try{
            img = ImageIO.read(new File("img/Chatting.png"));
        } catch (IOException e){
            System.out.println("Fail to load the Image");
            System.exit(0);
        }
        Chatpanel panel = new Chatpanel();
        panel.setBounds(0,0,384,461);


        setBounds(100, 100, 400, 500);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //X버튼 누르면 프로그램을 전체 종료가 아니라 창닫기로 연결시켜버림
                closeWindow();
            }
        });
        C.setLayout(null);

        // 참가자 목록은 JList로 변경
        list_user = new JList();
        //list_user.setCellRenderer(new SpeakerMarkRed());
        list_user.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        list_user.setBounds(12, 10, 180, 134);
        list_user.setFont(new Font("굴림", Font.BOLD, 18));
        C.add(list_user);

        //textArea = new JTextArea();
        //textArea.setBounds(12, 10, 360, 134);
        //textArea.setEditable(false);
        //getContentPane().add(textArea);

        textArea_1 = new JTextArea();
        textArea_1.setBounds(12, 154, 360, 214);
        textArea_1.setFont(new Font("굴림", Font.BOLD, 18));
        textArea_1.setEditable(false);
        C.add(textArea_1);

        scrollPane_alarm = new JScrollPane(textArea_1);
        scrollPane_alarm.setBounds(12, 154, 360, 214);
        C.add(scrollPane_alarm);

        JButton btnRename = new JButton("RENAME");
        btnRename.setBounds(205, 378, 85, 73);
        btnRename.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.userRenameWindow();
            }
        });
        C.add(btnRename);

        JButton btnQuit = new JButton("QUIT");
        btnQuit.setBounds(302, 378, 70, 73);
        btnQuit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindow();
            }
        });
        C.add(btnQuit);

        //토글 버튼
        JToggleButton tglbtnMic = new JToggleButton("<html>MIC<br>ON</html>");
        tglbtnMic.setBounds(12, 378, 70, 73);
        C.add(tglbtnMic);
        ItemListener micListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    System.out.println("mic off"); // show your message here
                    tglbtnMic.setText("<html>MIC<br>OFF</html>");
                    client.micOff();
                    client.stopSend();
                } else {
                    System.out.println("mic on"); // remove your message
                    tglbtnMic.setText("<html>MIC<br>ON</html>");
                    client.micOn();
                    client.startSend();
                }
            }
        };
        tglbtnMic.addItemListener(micListener);

        JToggleButton tglbtnSpeaker = new JToggleButton("<html><center>SPEAKER<br>ON</center></html>");
        tglbtnSpeaker.setBounds(94, 378, 99, 73);
        C.add(tglbtnSpeaker);
        ItemListener speakerListener = new ItemListener() {
            public void itemStateChanged(ItemEvent itemEvent) {
                int state = itemEvent.getStateChange();
                if (state == ItemEvent.SELECTED) {
                    System.out.println("speaker off"); // show your message here
                    tglbtnSpeaker.setText("<html><center>SPEAKER<br>OFF</center></html>");
                    //client.audioOff();
                    client.stopReceive();
                } else {
                    System.out.println("speaker on"); // remove your message
                    tglbtnSpeaker.setText("<html><center>SPEAKER<br>ON</center></html>");
                    //client.audioOn();
                    client.startReceive();
                }
            }
        };
        tglbtnSpeaker.addItemListener(speakerListener);
        C.add(panel);
        setResizable(false);
        setVisible(true);
    }

    class Chatpanel extends JPanel{
        public void paint(Graphics g){
            g.drawImage(img,0,0,null);
        }
    }

    public void setList_user(Vector e) {
        list_user.setListData(e);
    }

    public void addText(String str) {
        textArea_1.append(str + "\n");
        scrollPane_alarm.getVerticalScrollBar().setValue(scrollPane_alarm.getVerticalScrollBar().getMaximum());
    }

    public void closeWindow() {
        client.disconnect();
        setVisible(false);
        this.dispose();
        client.setVisible(true);
    }

    public ClientChat(Client client) {
        this.client = client;
        initGUI();
    }

    public ClientChat() {
        initGUI();
    }

    public void markSpeakerList(int index) {
        // 말하고 있는 녀석을 색깔 표시
        list_user.setSelectedIndex(index);
    }

    public void removeSpeakerMark(int index) {
        list_user.setSelectedIndex(index);
    }

    public static void main(String[] args) {
        new ClientChat();
    }

    /*
    private static class SpeakerMarkRed extends DefaultListCellRenderer {
        public Component getListCellRendererComponent( JList list, Object value, int index, boolean isSelected, boolean cellHasFocus ) {
            Component c = super.getListCellRendererComponent( list, value, index, isSelected, cellHasFocus );
            if ( index % 2 == 0 ) {
                c.setBackground( Color.yellow );
            }
            else {
                c.setBackground( Color.white );
            }
            return c;
        }
    }
     */
}
