package cases.music6;

import implementation.annotation.Scope;
import implementation.annotation.Value;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Scope("prototype")
public class MusicPlayer {

    @Value("$volume")
    //@Value("43")
    private int volume;

    @Value("[\"a\", \"bc\", \"def\"]")
    private String[] array;

    @Inject
    @Named("classicalMusic")
    private Music classicalMusic;

    @Inject
    @Named("rockMusic")
    private Music rockMusic;

    public String playMusic() {
        return "Playing: " + classicalMusic.getSong() + ", " + rockMusic.getSong();
    }

    private HeadPhones headPhones;

    @Inject
    public MusicPlayer(HeadPhones headPhones) {
        this.headPhones = headPhones;
    }

    public void getModel() {
        this.headPhones.getModel();
    }

    public int getVolume() {
        return volume;
    }

    public String[] getArray() {return array;}
}
