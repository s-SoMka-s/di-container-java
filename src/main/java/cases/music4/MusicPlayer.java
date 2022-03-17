package cases.music4;

import implementation.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope("prototype")
public class MusicPlayer {
    @Inject
    @Named("classicalMusic")
    private Music classicalMusic;

    @Inject
    @Named("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }
}
