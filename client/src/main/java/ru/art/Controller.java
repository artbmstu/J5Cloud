package ru.art;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class Controller {

    public TextField loginField;
    public PasswordField passField;
    public HBox actionPanel;
    public HBox authPanel;
    public ListView cloudList;
    public Button download, delete, logout;
    public ProgressBar operationProgress;

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
        setImages();
        if (!Network.getInstance().isConnected()){
            try {
                Network.getInstance().connect();
                Network.getInstance().setOperationProgress(operationProgress);
                Thread thread = new Thread(() -> {
                    try {
                        while (true){
                            Object msg = Network.getInstance().readData();
                            if (msg instanceof Message){
                                Message am = (Message) msg;
                                switch (am.getCommand()){
                                    case "/authOk":
                                        auth(true);
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
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e){
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

    void auth(boolean authorized){
        authPanel.setVisible(!authorized);
        authPanel.setManaged(!authorized);
        actionPanel.setVisible(authorized);
        actionPanel.setManaged(authorized);
        cloudList.setDisable(!authorized);
    }


    public void initializeSimpleListView(Set pathes) {
        Platform.runLater(() -> {
            ObservableList<String> listItems = FXCollections.observableArrayList();
            cloudList.setItems(listItems);
            cloudList.getItems().addAll(pathes.toArray());
        });
    }

    public void downloadFile() throws IOException {
        String dCmd = (String)cloudList.getSelectionModel().getSelectedItem();
        if (dCmd != null) {
            Network.getInstance().sendData(new DoMessage("/download", dCmd));
        }
    }

    public void saveFile(MyFile myFile){
        Platform.runLater(() -> {
            final DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Выберите директорию");
            directoryChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File dir = directoryChooser.showDialog(Main.getPrimaryStage());
            if (dir != null) {
                byte[] bytes = myFile.getBytes();
                String fileName = myFile.getName();
                try {
                    FileOutputStream fos = new FileOutputStream(new File(dir.getPath() + "/" + fileName));
                    fos.write(bytes);
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void deleteFile(ActionEvent actionEvent) throws IOException {
        Network.getInstance().sendData(new DoMessage("/delete", (String)cloudList.getSelectionModel().getSelectedItem()));

    }

//    public void updateFiles(ActionEvent actionEvent) throws IOException {
//        Network.getInstance().sendData(new DoMessage("/update"));
//    }

    public void exitAuth(ActionEvent actionEvent){
        Network.getInstance().close();
        cloudList.setItems(null);
        loginField.clear();
        passField.clear();
        auth(false);
    }

    private void setImages(){
        Platform.runLater(() -> {
            List<Button> buttons = Arrays.asList(download, delete, logout);
            buttons.forEach((button)-> button.setGraphic(new ImageView(new Image(getClass().getClassLoader().getResourceAsStream(button.getId() + ".png"), 50, 50, true, false))));
        });
    }

}
