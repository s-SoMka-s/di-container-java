package cases.annotations.music4;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component
public class MusicPlayer {

    @Inject("classicalMusic")
    private Music classicalMusic;

    @Inject("classicalMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }
}
