package cases.scope.music2;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Scope;

import static framework.enums.Scope.PROTOTYPE;

@Component("PC")
@Scope(PROTOTYPE)
public class Computer {
    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
