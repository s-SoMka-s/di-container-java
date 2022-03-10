package cases.music2;

import implementation.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Named("PIPA")
public class Computer {
    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
