package ru.art;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Controller {

    public TextField loginField;
    public PasswordField passField;
    public HBox actionPanel;
    public HBox authPanel;
    public ListView cloudList;

    public void tryToAuth(){
        openConnection();
        Message authMessage = new AuthMessage(loginField.getText(), passField.getText());
        try {
            Network.getInstance().sendData(authMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openConnection(){
        if (!Network.getInstance().isConnected()){
            try {
                Network.getInstance().connect();
                Thread thread = new Thread(() -> {
                    try {
                        while (true){
                            Object msg = Network.getInstance().readData();
                            if (msg instanceof Message){
                                Message am = (Message) msg;
                                switch (am.getText()){
                                    case "/authOk":
                                        authOk();
                                        initializeSimpleListView(am.getFilePathes());
                                        break;
                                    case "/update":
                                        initializeSimpleListView(am.getFilePathes());
                                        break;
                                }
                            }
                            if (msg instanceof MyFile){
                                saveFile((MyFile) msg);
                            }
                        }
                    } catch (ClassNotFoundException | IOException e){
                        e.printStackTrace();
                    } finally {
                        Network.getInstance().close();
                    }
                });
                thread.setDaemon(true);
                thread.start();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void sendFile(DragEvent event){
        try {
            Dragboard board = event.getDragboard();
            if (board.hasFiles()) {
                List<File> files = board.getFiles();
                if (files.size() != 1) throw new Exception("Передано больше 1 файла");
                File file = files.get(0);
                byte[] data = Files.readAllBytes(Paths.get(file.getAbsolutePath()));
                MyFile myFile = new MyFile();
                myFile.setName(file.getName());
                myFile.setBytes(data);
                Network.getInstance().sendData(myFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void draggingOver(DragEvent event) {
        Dragboard board = event.getDragboard();
        if (board.hasFiles()) {
            event.acceptTransferModes(TransferMode.ANY);
        }
    }

    public void authOk(){
        authPanel.setVisible(false);
        authPanel.setManaged(false);
        actionPanel.setVisible(true);
        actionPanel.setManaged(true);
        cloudList.setDisable(false);
    }

    public void initializeSimpleListView(List pathes) {
        Platform.runLater(() -> {
            ObservableList<String> listItems = FXCollections.observableArrayList();
            cloudList.setItems(listItems);
            cloudList.getItems().addAll(pathes.toArray());
        });
    }

    public void downloadFile() {
        String currentItemSelected = (String)cloudList.getSelectionModel().getSelectedItem();
        try {
            Network.getInstance().sendData(currentItemSelected);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveFile(MyFile myFile) throws IOException{
        byte[] bytes = myFile.getBytes();
        String fileName = myFile.getName();
        FileOutputStream fos = new FileOutputStream(new File("common/clientStorage/" + fileName));
        fos.write(bytes);
        fos.close();
    }
}
