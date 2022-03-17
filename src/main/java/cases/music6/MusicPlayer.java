package cases.music6;

import implementation.annotation.Scope;
import implementation.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope("prototype")
public class MusicPlayer {

    @Value("73")
    private int volume;

    @Inject
    @Named("classicalMusic")
    private Music classicalMusic;

    @Inject
    @Named("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }

    public int getVolume() {
        return volume;
    }
}
