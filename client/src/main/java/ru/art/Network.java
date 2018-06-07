package ru.art;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.net.Socket;

public class Network {
    private static Network network = new Network();

    public static Network getInstance(){
        return network;
    }
    private Socket socket;
    private ObjectDecoderInputStream in;
    private ObjectEncoderOutputStream out;

    public boolean isConnected(){
        return socket != null && !socket.isClosed();
    }
    public void connect() throws IOException {
        socket = new Socket("localhost", 8189);
        in = new ObjectDecoderInputStream(socket.getInputStream());
        out = new ObjectEncoderOutputStream(socket.getOutputStream());
    }
    public void sendData(Object data) throws IOException{
        out.writeObject(data);
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
