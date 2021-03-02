package com.hxg.sofa.jraft.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;


public abstract class FileOutputSignalHandler implements JRaftSignalHandler {

    protected File getOutputFile(final String path, final String baseFileName) throws IOException {
        makeDir(path);
        final String now = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
        final String fileName = baseFileName + "." + now;
        final File file = Paths.get(path, fileName).toFile();
        if (!file.exists() && !file.createNewFile()) {
            throw new IOException("Fail to create file: " + file);
        }
        return file;
    }

    private static void makeDir(final String path) throws IOException {
        final File dir = Paths.get(path).toFile().getAbsoluteFile();
        if (dir.exists()) {
            Requires.requireTrue(dir.isDirectory(), String.format("[%s] is not directory.", path));
        } else {
            FileUtils.forceMkdir(dir);
        }
    }
}
