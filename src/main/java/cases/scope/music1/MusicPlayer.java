package cases.scope.music1;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;

@Component
public class MusicPlayer {

    @Inject("classicalMusic")
    private Music classicalMusic;

    @Inject("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }
}