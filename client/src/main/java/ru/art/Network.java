package ru.art;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.scene.control.ProgressBar;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Network {
    private static Network network = new Network();
    private ProgressBar operationProgress;

    public void setOperationProgress(ProgressBar operationProgress) {
        this.operationProgress = operationProgress;
    }

    public static Network getInstance(){
        return network;
    }
    private Socket socket;
    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;
    OutputStream os;

    public boolean isConnected(){
        return socket != null && !socket.isClosed();
    }
    public void connect() throws IOException {
        socket = new Socket("localhost", 8189);
        os = socket.getOutputStream();
        in = new ObjectDecoderInputStream(socket.getInputStream(), 1024 * 1024 * 100 * 10);
        out = new ObjectEncoderOutputStream(os);
    }

    public void sendData(Object data) throws IOException{
        new Thread(() -> {
            if (data instanceof MyFile) {
                operationProgress.setManaged(true);
                operationProgress.setVisible(true);
            }
            try {
                out.writeObject(data);
            } catch (IOException e) {
                e.printStackTrace();
            }
            operationProgress.setManaged(false);
            operationProgress.setVisible(false);
        }).start();
    }
    public Object readData() throws IOException, ClassNotFoundException {
        return in.readObject();
    }
    public void close(){
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
