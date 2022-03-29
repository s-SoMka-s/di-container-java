package cases.values.music2;

import framework.annotations.Component;
import framework.annotations.Inject;
import framework.annotations.Value;

import java.util.ArrayList;

@Component("PC")
public class Computer {

    @Value("$name")
    private String name;

    @Value("$cost")
    private int cost;

    @Value("$list")
    private ArrayList<String> words;

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

    public ArrayList<String> getWords() {
        return words;
    }
}
