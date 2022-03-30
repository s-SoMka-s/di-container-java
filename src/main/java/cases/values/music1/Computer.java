package cases.values.music1;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;

@Component("PC")
public class Computer {

    @Value("iMac")
    private String name;

    @Value("179999")
    private int cost;

    @Inject("iPod")
    private MusicPlayer musicPlayer;

    public String result() {
        return "Computer: " + " " + musicPlayer.playMusic();
    }

    public String getName() {
        return name;
    }

    public int getCost() {
        return cost;
    }
}
