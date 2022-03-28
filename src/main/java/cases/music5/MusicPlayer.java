package cases.music5;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Scope;
import framework.annotations.Value;

import static framework.enums.Scope.PROTOTYPE;
import static framework.enums.Scope.SINGLETON;

@Component
//@Scope(PROTOTYPE)
public class MusicPlayer {
    //@Value("$volume")
    @Value("43")
    private int volume;

    //@Value("[\"a\", \"bc\", \"def\"]")
    @Value("$list")
    private String[] array;

    @Inject("classicalMusic")
    private Music classicalMusic;

    @Inject("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }

    public int getVolume() {
        return volume;
    }
}
