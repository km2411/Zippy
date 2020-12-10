package enums;

public enum ZipFormatType {

    ZIP(".zip");

    private final String extension;

    ZipFormatType(String extension) {
        this.extension = extension;
    }

    public String getExtension() {
        return extension;
    }
}
