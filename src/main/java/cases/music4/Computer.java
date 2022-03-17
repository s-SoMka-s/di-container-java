package cases.music4;

import implementation.annotation.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Scope("prototype")
@Named("PC")
public class Computer {
    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
