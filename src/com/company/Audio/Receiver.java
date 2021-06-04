package com.company.Audio;

import javax.media.Manager;
import javax.media.Player;
import javax.media.protocol.DataSource;
import javax.media.rtp.*;
import javax.media.rtp.event.NewReceiveStreamEvent;
import javax.media.rtp.event.ReceiveStreamEvent;
import java.io.IOException;
import java.util.Vector;

public class Receiver implements ReceiveStreamListener {

    Player player;
    RTPManager rtpManager;
    DataSource ds;
    Vector<Player> PlayerList;

    boolean audioStop = true;

    public Receiver(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
        PlayerList = new Vector<Player>();
        rtpManager.addReceiveStreamListener(this::update);
    }

    public void setRtpManager(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
    }

    @Override
    public void update(ReceiveStreamEvent e) {
        if(e instanceof NewReceiveStreamEvent) {
            ReceiveStream rs = ((NewReceiveStreamEvent)e).getReceiveStream();
            rs.getSenderReport();
            DataSource ds = rs.getDataSource();
            try {
                player = Manager.createRealizedPlayer(ds);
            } catch (Exception e1) {
                System.out.println("잘못된 데이터 소스");
                //e1.printStackTrace();
            }
            PlayerList.add(player);
            if (!audioStop) {
                player.start();
            }
        }
    }


    public void startReceive() {
        for(int i = 0; i < PlayerList.size() ; i++) {
            if(PlayerList.get(i) != null) {
                PlayerList.get(i).start();
            }
        }
        audioStop = false;
        //rtpManager.addReceiveStreamListener(this);
    }

    public void stopReceive() {
        for(int i = 0; i < PlayerList.size() ; i++) {
            if(PlayerList.get(i) != null) {
                PlayerList.get(i).stop();
            }
        }
        audioStop = true;
        //rtpManager.removeReceiveStreamListener(this);
    }

    public void listReset() {
        rtpManager.removeReceiveStreamListener(this::update);
        PlayerList.removeAllElements();
    }

    public void setAudioOn() {
        //audioStop = false;
    }

    public void setAudioOff() {
        //audioStop = true;
    }
}
