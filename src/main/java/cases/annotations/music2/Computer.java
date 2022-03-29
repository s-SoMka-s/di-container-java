package cases.annotations.music2;

import framework.annotations.Component;

@Component
public class Computer {

    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
