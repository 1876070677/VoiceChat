package com.company.Client;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class SignUp extends JFrame {

    boolean ID_CHECK = false;
    boolean NICKNAME_CHECK = false;
    String SIGN_ID = "";
    String SIGN_NICKNAME = "";


    private JTextField textField;
    private JTextField textField_1;
    private JTextField textField_2;

    Client client;

    BufferedImage img = null;
    public void initGUI(){
        Container C;
        C = getContentPane();
        try{
            img = ImageIO.read(new File("img/Signup.png"));
        } catch (IOException e){
            System.out.println("Fail to load the Image");
            System.exit(0);
        }
        SignUppanel panel = new SignUppanel();
        getLayeredPane().add(panel);
        panel.setBounds(0,0,334,461);

        setBounds(100, 100, 350, 500);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //X버튼 누르면 프로그램을 전체 종료가 아니라 창닫기로 연결시켜버림
                closeWindow();
            }
        });
        C.setLayout(null);

        /* JLabel lblNewLabel = new JLabel("SIGN UP");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("굴림", Font.BOLD, 15));
        lblNewLabel.setBounds(85, 33, 164, 45);
        getContentPane().add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("ID");
        lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1.setFont(new Font("굴림", Font.BOLD, 14));
        lblNewLabel_1.setBounds(35, 124, 57, 32);
        getContentPane().add(lblNewLabel_1); */

        textField = new JTextField();
        textField.setColumns(10);
        textField.setBounds(104, 131, 145, 21);
        C.add(textField);

       /* JLabel lblNewLabel_1_1 = new JLabel("PW");
        lblNewLabel_1_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1_1.setFont(new Font("굴림", Font.BOLD, 14));
        lblNewLabel_1_1.setBounds(35, 193, 57, 30);
        getContentPane().add(lblNewLabel_1_1); */

        textField_1 = new JTextField();
        textField_1.setColumns(10);
        textField_1.setBounds(104, 199, 145, 21);
       C.add(textField_1);

        JButton btnNewButton = new JButton("ID CHECK");
        btnNewButton.setFont(new Font("굴림", Font.BOLD, 13));
        btnNewButton.setBounds(114, 162, 125, 21);
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.idCheck(textField.getText().toString());
            }
        });
        C.add(btnNewButton);

       /* JLabel lblNewLabel_1_1_1 = new JLabel("NICKNAME");
        lblNewLabel_1_1_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_1_1_1.setFont(new Font("굴림", Font.BOLD, 14));
        lblNewLabel_1_1_1.setBounds(12, 252, 90, 30);
        getContentPane().add(lblNewLabel_1_1_1); */

        textField_2 = new JTextField();
        textField_2.setColumns(10);
        textField_2.setBounds(104, 258, 145, 21);
        C.add(textField_2);

        JButton btnNewButton_2 = new JButton("NAME CHECK");
        btnNewButton_2.setFont(new Font("굴림", Font.BOLD, 13));
        btnNewButton_2.setBounds(114, 289, 125, 21);
        btnNewButton_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                client.nicknameCheck(textField_2.getText().toString());
            }
        });
        C.add(btnNewButton_2);

        JButton btnNewButton_1 = new JButton("SIGN UP");
        btnNewButton_1.setFont(new Font("굴림", Font.BOLD, 15));
        btnNewButton_1.setBounds(114, 329, 109, 32);
        btnNewButton_1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!ID_CHECK && !NICKNAME_CHECK) {
                    JOptionPane.showMessageDialog(null, "ID 또는 닉네임 중복확인을 해주세요");
                }else {
                    if (SIGN_ID.equals(textField.getText().toString()) && SIGN_NICKNAME.equals(textField_2.getText().toString())) {
                        client.signUp(textField.getText().toString(),
                                textField_1.getText().toString(),
                                textField_2.getText().toString());
                        closeWindow();
                    } else {
                        JOptionPane.showMessageDialog(null, "ID 또는 닉네임 중복확인을 해주세요");
                    }
                }
            }
        });
        C.add(btnNewButton_1);

        C.add(panel);
        setResizable(false);
        setVisible(true);
    }

    class SignUppanel extends JPanel{
        public void paint(Graphics g){
            g.drawImage(img,0,0,null);
        }
    }

    public void closeWindow() {
        ID_CHECK = false;
        setVisible(false);
        dispose();
        client.setVisible(true);
    }

    public void dialogWindow(int num) {
        if (num == 1) {
            JOptionPane.showMessageDialog(null, "사용가능한 ID");
            ID_CHECK = true;
            SIGN_ID = textField.getText().toString();
        }
        else if (num == 2) {
            JOptionPane.showMessageDialog(null, "이미 사용중인 ID");
            textField.setText("");
        }

    }

    public void nicknameCheckResult(int num) {
        if (num == 1) {
            JOptionPane.showMessageDialog(null, "사용가능한 닉네임");
            NICKNAME_CHECK = true;
            SIGN_NICKNAME = textField_2.getText().toString();
        }
        else if(num == 2) {
            JOptionPane.showMessageDialog(null, "이미 사용중인 닉네임");
            textField_2.setText("");
        }
    }


    public SignUp(Client client) {
        this.client = client;
        initGUI();
    }
}
