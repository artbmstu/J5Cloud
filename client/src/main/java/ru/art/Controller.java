package ru.art;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.Socket;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

public class Controller implements Initializable {
    ListView<String> simpleListView;
    private boolean isAuthorized = true;
    ObjectEncoderOutputStream oeos = null;
    ObjectDecoderInputStream odis = null;
    Socket socket;

    public HBox actionPanel1;
    public TextField loginField;
    public HBox actionPanel2;
    public HBox authPanel;
    public ListView cloudList;

    final String IP_ADDRESS = "localhost";
    final int PORT = 8189;

    public void setAuthorized(boolean authorized) {
        isAuthorized = authorized;
        actionPanel1.setVisible(isAuthorized);
        actionPanel1.setManaged(isAuthorized);

        actionPanel2.setVisible(isAuthorized);
        actionPanel2.setManaged(isAuthorized);

        authPanel.setVisible(!isAuthorized);
        authPanel.setManaged(!isAuthorized);
    }

    public void tryToAuth(ActionEvent actionEvent) {
        setAuthorized(true);
    }

    public void sendFile(DragEvent event){
//        new Thread(() -> {
//            try {
//                while (true) {
//                    waitForResponse();
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });

        try {

            Dragboard board = event.getDragboard();
            List<File> phil = board.getFiles();
            oeos = new ObjectEncoderOutputStream(socket.getOutputStream());
            File file = phil.get(0);
            byte[] array = IOUtils.toByteArray(new FileInputStream(file));
            MyFile myFile = new MyFile();
            myFile.setName(FilenameUtils.getName(file.getAbsolutePath()));
            myFile.setBytes(array);
            oeos.writeObject(myFile);
            oeos.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
//            try {
//                oeos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
    }
    public void draggingOver(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void receiveMessage(){
        new Thread(()->{
            try {
                odis = new ObjectDecoderInputStream(socket.getInputStream());
                MyMessage reply;
                while (true){
                    reply = (MyMessage) odis.readObject();
                    System.out.println(reply.getText());
                }

            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void initializeSimpleListView() {
        ObservableList<String> listItems = FXCollections.observableArrayList();
        simpleListView.setItems(listItems);
        simpleListView.getItems().addAll("Java", "Core", "List", "View");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Socket socket = null;
        try {
            socket = new Socket(IP_ADDRESS, PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.socket = socket;
        System.out.println("init");
        receiveMessage();
//        initializeSimpleListView();
    }

//    void waitForResponse() throws IOException{
//        System.out.println("done");
//        InputStream is = socket.getInputStream();
//        DataInputStream dis = new DataInputStream(is);
//        System.out.println(dis.readUTF());
//    }
}
