package com.company.Audio;

import javax.media.*;
import javax.media.format.AudioFormat;
import javax.media.rtp.SendStreamListener;
import javax.media.rtp.event.SendStreamEvent;
import javax.sound.sampled.*;
import javax.media.format.UnsupportedFormatException;
import javax.media.protocol.ContentDescriptor;
import javax.media.protocol.DataSource;
import javax.media.protocol.PushBufferDataSource;
import javax.media.protocol.PushBufferStream;
import javax.media.rtp.RTPManager;
import javax.media.rtp.SendStream;
import javax.media.rtp.SessionAddress;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

public class Sender extends Thread {

    RTPManager rtpManager;
    Processor processor;
    MediaLocator mediaLocator;
    PushBufferDataSource pbds;
    PushBufferStream[] pbss;
    SendStream ss = null;
    DataSource source;
    DataSource ds;

    boolean micStop = false;

    private Format[] FORMATS = new Format[]{new AudioFormat(AudioFormat.ULAW_RTP)};
    private static final ContentDescriptor CONTENT_DESCRIPTOR = new ContentDescriptor(ContentDescriptor.RAW_RTP);

    public Sender(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
    }

    @Override
    public void run() {

        AudioFormat af = new AudioFormat(AudioFormat.LINEAR);
        System.out.println((float) af.getSampleRate());

        mediaLocator = new MediaLocator("javasound://0");

        try {
            source = Manager.createDataSource(mediaLocator);
            processor = Manager.createRealizedProcessor(new ProcessorModel(source, FORMATS, CONTENT_DESCRIPTOR));
            ds = processor.getDataOutput();

            pbds = (PushBufferDataSource) ds;
            pbss= pbds.getStreams();

            System.out.println("All Thread is Ready!!");
            for (int i = 0; i<pbss.length;i++) {
                ss = rtpManager.createSendStream(ds, i);
                ss.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        processor.start();
    }

    public void getFormat() {
    }

    public void setRtpManager(RTPManager rtpManager) {
        this.rtpManager = rtpManager;
    }

    public void startTrans() {
        if (!micStop) {
            processor.start();
        }
    }
    public void stopTrans() {
        processor.stop();
    }

    public void setMicOff() {
        micStop = true;
    }

    public void setMicOn() {
        micStop = false;
    }

    public boolean getMicstop() {
        return micStop;
    }

    public void disconnectDatasource() {
        try {
            ss.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
        processor.stop();
        pbds.disconnect();
        ds.disconnect();
        source.disconnect();
    }
}
