package com.company.Audio;

import com.company.Client.Client;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.Format;
import javax.media.protocol.CaptureDevice;
import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;

public class VoiceRecognition extends Thread{

    Client client;
    boolean speaking;
    boolean chatting;

    AudioFormat format;
    TargetDataLine microphone;

    public VoiceRecognition(Client client) {
        this.client = client;
        speaking = false;
        format = new AudioFormat(8000.0f, 16, 2, true, true);
        try {
            microphone = AudioSystem.getTargetDataLine(format);
            System.out.println(microphone);
            DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

            microphone = (TargetDataLine) AudioSystem.getLine(info);
            microphone.open();
            microphone.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
        format = null;
    }

    public int calculateRMSLevel(byte[] audioData)
    {
        long lSum = 0;
        for(int i=0; i < audioData.length; i++)
            lSum = lSum + audioData[i];

        double dAvg = lSum / audioData.length;
        double sumMeanSquare = 0d;

        for(int j=0; j < audioData.length; j++)
            sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

        double averageMeanSquare = sumMeanSquare / audioData.length;

        return (int)(Math.pow(averageMeanSquare,0.5d) + 0.5);
    }

    @Override
    public void run() {
        chatting = true;
        try {
            int level = 0;
            byte tempBuffer[] = new byte[6000];
            while (chatting) {
                if (microphone.read(tempBuffer, 0, tempBuffer.length) > 0) {
                    System.out.println("마이크 캡쳐중");
                    level = calculateRMSLevel(tempBuffer);
                    if (level > 45 && !speaking) {
                        //원래 말을하고 있지 않앗는데 마이크에 일정 레벨이상이 잡힐때,
                        //소켓을 통해서 이녀석이 말을 하고 있다고 전달해줌
                        client.imSpeaking();
                        speaking = true;
                    } else if (level <46 && speaking) {
                        //원래 말을 하고 있었는데 마이크에 일정 레벨이하가 잡힐때,
                        //소켓을 통해서 이 녀석이 말을 안하고 있다고 전달해줌
                        client.imnotSpeaking();
                        speaking = false;
                    }
                }
                Thread.sleep(1000);
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setChatting(boolean state) {
        this.chatting = false;
    }

    public void offVoiceRecognition() {
        chatting = false;
    }
}
