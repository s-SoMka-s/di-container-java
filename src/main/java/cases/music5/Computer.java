package cases.music5;

import framework.annotations.Scope;

import javax.inject.Inject;
import javax.inject.Named;

@Scope(framework.enums.Scope.PROTOTYPE)
@Named("PC")
public class Computer {
    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
