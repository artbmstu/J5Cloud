//package ru.art;
//
//import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
//import org.apache.commons.io.FilenameUtils;
//import org.apache.commons.io.IOUtils;
//
//import javax.swing.*;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.Socket;
//
//public class Client {
//    public static void main(String[] args) {
//        ObjectEncoderOutputStream oeos = null;
//        try (Socket socket = new Socket("localhost", 8189)) {
//            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
//            JFileChooser fileopen = new JFileChooser();
//            int ret = fileopen.showDialog(null, "Выбрать файл");
//            if (ret == JFileChooser.APPROVE_OPTION) {
//                File file = fileopen.getSelectedFile();
//                byte[] array = IOUtils.toByteArray(new FileInputStream(file));
//                MyFile myFile = new MyFile();
//                myFile.setName(FilenameUtils.getName(file.getAbsolutePath()));
//                myFile.setBytes(array);
//                oeos.writeObject(myFile);
//                oeos.flush();
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                oeos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
