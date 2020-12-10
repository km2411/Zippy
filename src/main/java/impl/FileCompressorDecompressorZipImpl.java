package impl;

import com.google.common.collect.Lists;
import interfaces.FileCompressorDecompressor;
import models.ChunkedUnzip;
import models.ChunkedZip;

import java.io.IOException;
import java.util.List;

public class FileCompressorDecompressorZipImpl implements FileCompressorDecompressor {

    private long maxFileSize;

    public FileCompressorDecompressorZipImpl(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void zip(String sourceDir, String destinationDir) {
        try {
            ChunkedZip zipper = new ChunkedZip("f2.mov", sourceDir, destinationDir, maxFileSize);
            zipper.createZip();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void unzip(String sourceDir, String destinationDir) {
        try {
            // need tp resolve directory structure and handle relative paths
            List<String> partFiles = Lists.newArrayList("f2.mov.part.1.zip", "f2.mov.part.2.zip", "f2.mov.part.3.zip");
            ChunkedUnzip unzipper = new ChunkedUnzip("tmp_f2.mov", sourceDir, destinationDir, partFiles);
            unzipper.unzipAll();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
