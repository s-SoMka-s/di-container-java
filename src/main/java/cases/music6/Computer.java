package cases.music6;

import framework.annotations.Scope;
import framework.annotations.Value;

import javax.inject.Inject;
import javax.inject.Named;

@Scope(framework.enums.Scope.PROTOTYPE)
@Named("PC")
public class Computer {
    //@Value("Macbook Air 13")
    @Value("$name")
    private String name;

    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }

    public String getName() {
        return name;
    }
}
