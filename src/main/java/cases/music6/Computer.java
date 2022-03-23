package cases.music6;

import framework.annotation.Scope;
import framework.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

@Scope("prototype")
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
