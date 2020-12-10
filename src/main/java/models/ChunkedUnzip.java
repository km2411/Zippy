package models;

import utils.ZippyUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ChunkedUnzip implements Runnable{

    private String filename;
    private String sourceDir;
    private String destDir;
    private List<String> partFiles;

    private FileOutputStream fos;

    public ChunkedUnzip(String filename, String sourceDir, String destDir, List<String> partFiles) {
        this.filename = filename;
        this.sourceDir = sourceDir;
        this.destDir = destDir;
        this.partFiles = partFiles;
    }

    @Override
    public void run() {
        try {
            unzipAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void unzipAll() throws IOException {
        try {
            fos = new FileOutputStream(destDir + ZippyUtils.DELIM + filename);
            for (String file : partFiles) {
                unzipSingleFile(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            fos.close();
        }
    }

    private void unzipSingleFile(String partFile) {
        try {
            ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceDir + ZippyUtils.DELIM + partFile));
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {
                int length;
                byte buffer[] = new byte[ZippyUtils.BUFFER_SIZE];
                while ((length = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, length);
                }
                zis.closeEntry();
                zipEntry = zis.getNextEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}