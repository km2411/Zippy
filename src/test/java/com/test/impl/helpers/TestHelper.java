package com.test.impl.helpers;

import com.test.utils.ZippyUtils;
import org.testng.collections.Sets;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class TestHelper {

    public static void writeDummyData(String testFileName, int size) {
        try (FileOutputStream fos = new FileOutputStream(testFileName)) {
            long remaining = size * 1024 * 1024;
            byte buffer[] = new byte[ZippyUtils.BUFFER_SIZE];
            new Random().nextBytes(buffer);

            while (remaining > 0) {
                fos.write(buffer, 0, ZippyUtils.BUFFER_SIZE);
                remaining -= ZippyUtils.BUFFER_SIZE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Set<String> getFilesWithSizeGreaterThanMax(List<Path> files, long maxSize) {
        Set<String> biggerFiles = Sets.newHashSet();
        for (Path file : files) {
            try {
                if (Files.size(file) > maxSize) {
                    biggerFiles.add(file.toString());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return biggerFiles;
    }

    public static boolean compareFiles(Path inputFile, Path outputFile) throws IOException {
        int bufferSize = 1024;
        int data1, data2;

        try (BufferedInputStream bis1 = new BufferedInputStream(new FileInputStream(inputFile.toString()), bufferSize);
             BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream(outputFile.toString()), bufferSize)) {
            data1 = bis1.read();
            data2 = bis2.read();

            while (data1 != -1 && data2 != -1) {
                if (data1 != data2) {
                    return false;
                }
                data1 = bis1.read();
                data2 = bis2.read();
            }

            if (data1 > 0 || data2 > 0)
                return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public static void deleteDir(Path dir) {
        try {
            ZippyUtils.getFilesInDir(dir, false).forEach(file -> {
                try {
                    Files.deleteIfExists(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            Files.deleteIfExists(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
