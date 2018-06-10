package ru.art;

import java.io.Serializable;
import java.util.Set;

public class AuthMessage implements Serializable, Message {

    private String command, fileName;
    private String login, password;
    private Set filePathes;

    @Override
    public Set getFilePathes() {
        return filePathes;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public String getCommand() {
        return command;
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
    AuthMessage (Set filePathes, String command){
        this.command = command;
        this.filePathes = filePathes;
    }
}
