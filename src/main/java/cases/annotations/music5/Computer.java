package cases.annotations.music5;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component
public class Computer {

    @Inject("MP")
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
