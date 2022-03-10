package cases.music1;

import javax.inject.Inject;
import javax.inject.Named;

@Named
public class MusicPlayer {
    @Inject
    @Named("rockMusic")
    private Music music;

    //private Music music = BeanFactory.getInstance().getBean(Music.class);

    /*private Music music;

    //IoC
    public MusicPlayer(Music music) {
        this.music = music;
    }*/

    public void playMusic() {
        System.out.println("Playing: " + music.getSong());
    }
}
