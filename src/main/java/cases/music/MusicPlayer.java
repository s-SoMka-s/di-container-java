package cases.music;

import implementation.annotation.Inject;
import implementation.factory.BeanFactory;

public class MusicPlayer {
    @Inject
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
