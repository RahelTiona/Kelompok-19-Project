package game;

public class BirdSkin {
    public static final BirdSkin YELLOW = new BirdSkin("yellowbird.png");
    public static final BirdSkin BLUE = new BirdSkin("bluebird.png");
    public static final BirdSkin RED = new BirdSkin("redbird.png");

    private final String filename;

    private BirdSkin(String filename) {
        this.filename = filename;
    }

    public String getFilename() {
        return filename;
    }
}