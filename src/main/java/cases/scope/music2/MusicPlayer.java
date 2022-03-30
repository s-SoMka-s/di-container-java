package cases.scope.music2;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;

@Component
@Scope(PROTOTYPE)
public class MusicPlayer {

    @Inject("classicalMusic")
    private Music classicalMusic;

    @Inject("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }
}
