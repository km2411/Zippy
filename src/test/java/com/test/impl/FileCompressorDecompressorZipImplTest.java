package com.test.impl;

import com.test.utils.ZippyUtils;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.test.impl.helpers.TestHelper.*;

public class FileCompressorDecompressorZipImplTest {

    Path tempSourceDir;
    Path tempDestDir;
    Path tempOutDir;
    Path testFile;

    @BeforeClass
    public void setUp() throws IOException {
        tempSourceDir = Files.createTempDirectory(null);
        tempDestDir = Files.createTempDirectory(null);
        tempOutDir = Files.createTempDirectory(null);
        testFile = Files.createTempFile(tempSourceDir, null, ".txt");
    }

    @AfterClass
    public void tearDown() throws IOException {
        deleteDir(tempSourceDir);
        deleteDir(tempDestDir);
        deleteDir(tempOutDir);
    }

    @Test(priority = 1)
    public void testZip() {
        int size = 9; // MB
        int maxSize = 5; // MB

        int parts = size / maxSize;
        int mod = size % maxSize;
        parts += mod > 0 ? 1 : 0;

        writeDummyData(testFile.toString(), size);
        FileCompressorDecompressorZipImpl fcd = new FileCompressorDecompressorZipImpl(1);
        fcd.compress(tempSourceDir.toString(), tempDestDir.toString(), Integer.valueOf(maxSize));

        List<Path> filesInDestDir = ZippyUtils.getFilesInDir(tempDestDir, false);

        // check if file split in multi-zip files or not
        Assert.assertEquals(filesInDestDir.size(), parts);

        // check new file names
        Set<String> zipPrefix = filesInDestDir.stream()
                .map(f -> ZippyUtils.getPrefix(tempDestDir.relativize(f).toString()))
                .collect(Collectors.toSet());

        Assert.assertEquals(zipPrefix.size(), 1);
        Assert.assertEquals(zipPrefix.iterator().next(), tempSourceDir.relativize(testFile).toString());

        // check if file sizes < maxSize
        Set<String> biggerFiles = getFilesWithSizeGreaterThanMax(filesInDestDir, (long) maxSize * 1024 * 1024);
        Assert.assertEquals(biggerFiles.size(), 0);
    }

    @Test(priority = 2)
    public void testUnzip() {
        FileCompressorDecompressorZipImpl fcd = new FileCompressorDecompressorZipImpl(1);
        fcd.decompress(tempDestDir.toString(), tempOutDir.toString());

        List<Path> filesInDestDir = ZippyUtils.getFilesInDir(tempOutDir, false);

        Assert.assertEquals(filesInDestDir.size(), 1);
        Assert.assertEquals(tempOutDir.relativize(filesInDestDir.get(0)).toString(), tempSourceDir.relativize(testFile).toString());

        boolean isContentEqual = false;
        try {
            isContentEqual = compareFiles(testFile, filesInDestDir.get(0));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Assert.assertEquals(isContentEqual, true);
    }

}