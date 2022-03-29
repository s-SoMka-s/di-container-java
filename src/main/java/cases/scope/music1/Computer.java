package cases.scope.music1;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;

@Component("PC")
public class Computer {
    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
