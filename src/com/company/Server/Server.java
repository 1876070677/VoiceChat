package com.company.Server;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final int SERVER_PORT = 8888;
    private String SERVER_IP = "";

    MongoClient mongoClient;
    MongoDatabase database;
    MongoCollection<Document> collection;

    ServerSocket serverSocket = null;
    Socket socket;
    List<Thread> list;

    public Server() {

        //데이터베이스 커넥션
        ConnectionString connectionString = new ConnectionString(
                "Your MongoDB Connection URL"
        );
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .retryWrites(true)
                .build();
        mongoClient = MongoClients.create(settings);
        database = mongoClient.getDatabase("MongoDB name");
        collection = database.getCollection("MongoDB Collection Name");

        //쓰레드 리스트 선언
        list = new ArrayList<Thread>();

        try {
            SERVER_IP = InetAddress.getLocalHost().getHostAddress();
            serverSocket = new ServerSocket(SERVER_PORT);
            serverSocket.setReuseAddress(true); //서버소켓이 이 port를 바로 다시 사용하도록 설정해줌

            while(true) {
                socket = serverSocket.accept();
                ServerThread thread = new ServerThread(this, socket);
                //addClient(thread);
                thread.start(); //스레드 실행
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public synchronized void enteredUser(ServerThread s) {
        for (int i = 0 ; i < list.size(); i++) {
            ServerThread thread = (ServerThread)list.get(i);
            s.sendMessage("OldUser//" + thread.getUsername() + "//" + thread.getIp());
        }
    }

    // synchronized : 스레드들이 같이 쓰는 데이터를 동시에 사용하지 않게 하기 위해서,, 접근순서대로 읽기, 쓰기 가능
    public synchronized void addClient(ServerThread thread) {
        list.add(thread);
        System.out.println("클라이언트 한명 접속 후 " + list.size() + "명 접속중");
    }

    public synchronized void broadCasting(String str) {
        //로그인 성공 후 서버는 각각의 스레드를 이용해서 메시지를 각 클라이언트들에게 전달
        for (int i = 0; i < list.size() ; i++) {
            ServerThread thread = (ServerThread)list.get(i);
            thread.sendMessage(str);
        }
    }

    public synchronized void deleteClient(Thread thread) {
        list.remove(thread);
        System.out.println("클라이언트 1명이 퇴장 후 " + list.size() + "명 접속중");
    }

    public boolean doublecheck(String id) {
        Document info = null;
        info = collection.find(Filters.eq("id", id)).first();
        //info = collection.find(new Document("id", id)).first();
        if (info == null) {
            //데이터베이스에서 똑같은 id가 발견 안되었을 경우
            return true;
        } else {
            return false;
        }
    }

    public boolean nicknameCheck(String nickname) {
        Document info = null;
        info = collection.find(Filters.eq("name", nickname)).first();

        if (info == null) {
            return true;
        } else {
            return false;
        }
    }

    public void registerDatabase(String id, String pw, String name) {
        Document info = new Document("id", id)
                .append("name", name)
                .append("passwd", pw);
        collection.insertOne(info);
    }

    public String loginDatabase(String id, String pw) {
        Document info = null;
        //info = collection.find(Filters.and(Filters.eq("id", id), Filters.eq("pw", pw))).first();
        info = collection.find(new Document("id", id).append("passwd", pw)).first();
        if (info == null) {
            return null;
        } else {
            return info.get("name").toString();
        }
    }

    public void updateDatabase(String id, String newName) {
        collection.updateOne(Filters.eq("id", id), new Document("$set", new Document("name", newName)));
    }

    public static void main(String[] args) {
        new Server();
    }
}
