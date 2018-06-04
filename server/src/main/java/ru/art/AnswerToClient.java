package ru.art;

import java.io.*;
import java.net.Socket;

public class AnswerToClient {

    void sendOk(){
        try (Socket socket = new Socket("localhost", 8189)) {
            OutputStream is = socket.getOutputStream();
            DataOutputStream dis = new DataOutputStream(is);
            dis.writeUTF("OK");
        }  catch (IOException e) {
            e.printStackTrace();
        }

    }
}
