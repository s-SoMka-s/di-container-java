package cases.scope.music4;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Scope;
import framework.annotations.Value;

import static framework.enums.Scope.THREAD;

@Component
//@Scope(PROTOTYPE)
@Scope(THREAD)
public class MusicPlayer {

    @Inject("classicalMusic")
    public Music classicalMusic;

    @Inject("rockMusic")
    public Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }

}
