package ru.art;

import java.util.List;
import java.util.Set;

public interface Message {
    String getCommand();
    Set getFilePathes();
    String getFileName();
}
