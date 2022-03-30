package cases.annotations.music3;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component
public class MusicPlayer {

    @Inject("LOLOLOLOLO")
    private Music classicalMusic;

    @Inject("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }
}
