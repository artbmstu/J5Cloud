package ru.art;

import java.io.Serializable;
import java.util.List;

public class AuthMessage implements Serializable, Message {

    private String text;
    private String login, password;
    private List filePathes;

    @Override
    public List getFilePathes() {
        return filePathes;
    }

    @Override
    public String getText() {
        return text;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    AuthMessage(String login, String password) {
        this.login = login;
        this.password = password;
    }
    AuthMessage (List filePathes, String text){
        this.text = text;
        this.filePathes = filePathes;
    }
}
