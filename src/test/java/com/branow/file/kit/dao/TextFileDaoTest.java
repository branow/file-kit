package com.branow.file.kit.dao;

import com.branow.file.kit.io.TextFile;

import java.nio.file.Path;

public class TextFileDaoTest extends AbstractTextFileDaoTest {


    @Override
    protected AbstractTextFileDao<ObjectDTO, Integer> instant(Path path) {
        return new TextFileDao<>(new TextFile(path),  new StringCollectionConverter<>(converter(), "\n"), ObjectDTO::getKey);
    }
}
