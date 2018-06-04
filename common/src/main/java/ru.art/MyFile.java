package ru.art;

import java.io.Serializable;

public class MyFile implements Serializable {
    private static final long serialVersionUID = 5193392663743561680L;

    private String name;
    private byte[] bytes;

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
