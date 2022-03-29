package cases.scope.music3;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;
import static framework.enums.Scope.SINGLETON;

@Component("PC")
@Scope(SINGLETON)
public class Computer {
    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
