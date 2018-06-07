package ru.art;

import java.io.Serializable;
import java.util.List;

public class UpdateMessage implements Message, Serializable {
    private String text;
    private List filePathes;

    @Override
    public String getText() {
        return text;
    }

    @Override
    public List getFilePathes() {
        return filePathes;
    }

    UpdateMessage(List filePathes, String text) {
        this.text = text;
        this.filePathes = filePathes;
    }
}
