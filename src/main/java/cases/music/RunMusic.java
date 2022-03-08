package cases.music;

import implementation.bean.ClassApplicationContextFromJSON;
import implementation.bean.XML.ClassApplicationContextFromXML;
import implementation.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class RunMusic {
    public static void main(String[] args) {
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //ClassApplicationContextFromXML context = new ClassApplicationContextFromXML("src/main/resources/applicationContext.xml");
        Music music1 = BeanFactory.getInstance().getBean(Music.class);
        //Music music2 = BeanFactory.getInstance().getBean(Music.class);

        MusicPlayer musicPlayer = BeanFactory.getInstance().getBean(MusicPlayer.class);
        //Music music = context.getBean("musicBean", Music.class);

        musicPlayer.playMusic();
        //System.out.println(music1.getSong());
        //System.out.println(music2.getSong());

        //context.close();
    }
}
