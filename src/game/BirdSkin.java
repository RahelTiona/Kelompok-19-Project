package game;

public enum BirdSkin {
    YELLOW("yellowbird.png"),
    BLUE("bluebird.png"),
    RED("redbird.png");

    private final String filename;

    BirdSkin(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}