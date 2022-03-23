package cases.music2;

import framework.annotations.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope(framework.enums.Scope.PROTOTYPE)
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
