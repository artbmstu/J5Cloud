package ru.art;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class DoMessage implements Message, Serializable {
    private String command, fileName;
    private Set filePathes;

    @Override
    public String getCommand() {
        return command;
    }

    @Override
    public Set getFilePathes() {
        return filePathes;
    }

    public String getFileName(){
        return fileName;
    }

    DoMessage(Set filePathes, String command) {
        this.command = command;
        this.filePathes = filePathes;
    }
    DoMessage(String command, String fileName){
        this.command = command;
        this.fileName = fileName;
    }
    DoMessage(String command){
        this.command = command;
    }
}
