package cases.annotations.music1;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component("PC")
public class Computer {

    @Inject("iPod")
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
