package models;

import utils.ZippyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ChunkedZip {

    private String filename;
    private String sourceDir;
    private String destDir;

    private long maxFileSize;

    private FileInputStream fis;
    private ZipOutputStream currZipOutStream;
    private int currPartIndex;

    public ChunkedZip(String filename, String sourceDir, String destDir, long maxFileSize) {
        this.filename = filename;
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.maxFileSize = maxFileSize;
    }

    public void createZip() throws IOException {
        // handle the case of ending or not ending with '/'
        fis = new FileInputStream(sourceDir + "/" + filename);

        int length, accumulator = 0;
        byte[] inBuffer = new byte[ZippyUtils.BUFFER_SIZE];

        while ((length = fis.read(inBuffer)) > 0) {
            if (currZipOutStream == null || (accumulator + ZippyUtils.BUFFER_SIZE >= maxFileSize)) {
                updateCurrentZipOutStream();
                accumulator = 0;
            }
            currZipOutStream.write(inBuffer, 0, length);
            accumulator += length;
            // log progress, based on size estimate of zip entry
        }

        fis.close();
        currZipOutStream.closeEntry();
        currZipOutStream.close();
    }

    private void updateCurrentZipOutStream() throws IOException {
        if (currZipOutStream != null) {
            currZipOutStream.closeEntry();
            currZipOutStream.close();
        }
        File targetFile = new File(destDir, getFilePartName());
        FileOutputStream fos = new FileOutputStream(targetFile);
        currZipOutStream = new ZipOutputStream(fos);
        currZipOutStream.putNextEntry(new ZipEntry(filename));
    }

    private String getFilePartName() {
        currPartIndex += 1;
        StringBuilder nameBuilder = new StringBuilder(filename).append(ZippyUtils.PART_POSTFIX)
                .append(currPartIndex).append(ZippyUtils.FILE_EXTENSION);
        return nameBuilder.toString();
    }
}
