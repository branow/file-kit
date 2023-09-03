package com.branow.file.kit.dao;

import com.branow.file.kit.io.TextFile;

import java.nio.file.Path;

public class TextFileStreamDaoTest extends AbstractTextFileDaoTest {

    @Override
    protected AbstractTextFileDao<ObjectDTO, Integer> instant(Path path) {
        return new TextFileStreamDao<>(new TextFile(path), "\n", converter(), ObjectDTO::getKey);
    }
}
