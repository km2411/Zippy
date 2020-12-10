package impl;

import com.google.common.collect.Lists;
import com.google.common.collect.SortedSetMultimap;
import enums.ZipFormatType;
import interfaces.FileCompressorDecompressor;
import models.ChunkedUnzip;
import models.ChunkedZip;
import utils.ZippyUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FileCompressorDecompressorZipImpl implements FileCompressorDecompressor {

    private long maxFileSize;

    public FileCompressorDecompressorZipImpl(long maxFileSize) {
        this.maxFileSize = maxFileSize;
    }

    @Override
    public void zip(String sourceDir, String destinationDir) {
        Map<String, List<String>> pathToFileList = ZippyUtils.getAllFilesInDir(sourceDir);
        // submit tasks to executor later
        for (Map.Entry<String, List<String>> entry : pathToFileList.entrySet()) {
            String relativePath = entry.getKey();
            for (String file : entry.getValue()) {
                try {
                    ZippyUtils.createOutDirsIfMissing(destinationDir + relativePath);
                    ChunkedZip zipper = new ChunkedZip(file, sourceDir + relativePath,
                                        destinationDir + relativePath, maxFileSize);
                    zipper.createZip();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void unzip(String sourceDir, String destinationDir) {
        Map<String, SortedSetMultimap<String, String>> pathToFileAndPartFiles = ZippyUtils.getAllZippedFilesWithParts(sourceDir,
                                                                            ZipFormatType.ZIP.getExtension());
        // submit tasks to executor later
        for (Map.Entry<String, SortedSetMultimap<String, String>> entry : pathToFileAndPartFiles.entrySet()) {
            String relativePath = entry.getKey();
            for (String file : entry.getValue().keySet()) {
                try {
                    ZippyUtils.createOutDirsIfMissing(destinationDir + relativePath);
                    ChunkedUnzip unzipper = new ChunkedUnzip(file, sourceDir + relativePath,
                                                destinationDir + relativePath, Lists.newArrayList(entry.getValue().get(file)));
                    unzipper.unzipAll();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
