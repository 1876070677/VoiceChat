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

public class ClientRename extends JFrame {

    private JFrame frame;
    private JTextField textField;

    Client client;

    BufferedImage img = null;
    public void initGUI() {
        Container C;
        C = getContentPane();
        try{
            img = ImageIO.read(new File("img/Rename.png"));
        } catch (IOException e){
            System.out.println("Fail to load the Image");
            System.exit(0);
        }
        Renamepanel panel = new Renamepanel();
        getLayeredPane().add(panel);
        panel.setBounds(0,0,384,211);

        setBounds(100, 100, 400, 250);
        //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                //X버튼 누르면 프로그램을 전체 종료가 아니라 창닫기로 연결시켜버림
                closeWindow();
            }
        });
        C.setLayout(null);

        /*JLabel lblNewLabel = new JLabel("RENAME");
        lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel.setFont(new Font("굴림", Font.BOLD, 16));
        lblNewLabel.setBounds(130, 25, 111, 29);
        getContentPane().add(lblNewLabel);
         */

        textField = new JTextField();
        textField.setBounds(37, 86, 204, 40);
        textField.setColumns(10);
        C.add(textField);

        JButton btnNewButton = new JButton("Change");
        btnNewButton.setBounds(253, 94, 97, 23);
        btnNewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindowAndRename();
            }
        });
        C.add(btnNewButton);

        C.add(panel);
        setResizable(false);
        setVisible(true);
    }

    class Renamepanel extends JPanel{
        public void paint(Graphics g){

            g.drawImage(img,0,0,null);
        }
    }

    public void closeWindow() {
        setVisible(false);
        this.dispose();
    }

    public void closeWindowAndRename() {
        client.userRename(textField.getText().toString());
        setVisible(false);
        this.dispose();
    }

    public ClientRename(Client client) {
        this.client = client;
        initGUI();
    }
}
