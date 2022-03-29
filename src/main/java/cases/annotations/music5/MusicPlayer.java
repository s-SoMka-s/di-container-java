package cases.annotations.music5;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("MP")
public class MusicPlayer {

    @Inject
    private Music classicalMusic;

    @Inject
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }
}
