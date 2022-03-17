package cases.music6;

import implementation.annotation.Scope;
import org.springframework.beans.factory.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

@Scope("prototype")
@Named("PC")
public class Computer {
    @Value("Macbook Air 13")
    private String name;

    @Inject
    private MusicPlayer musicPlayer;

    public String result(){
        return "Computer: " + " " + musicPlayer.playMusic();
    }
}
