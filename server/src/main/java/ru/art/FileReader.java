package ru.art;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

public class FileReader {

    private Set filePathes;

    public Set readFileStructure() throws IOException {
        filePathes = new LinkedHashSet();
        Files.list(Paths.get("common/storage/")).forEach(p -> {
            filePathes.add(p.getFileName().toString());
        });
        return filePathes;
    }
}
