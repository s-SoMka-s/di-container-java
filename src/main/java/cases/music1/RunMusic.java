package cases.music1;

import framework.context.Context;
//import org.springframework.context.ApplicationContext;

public class RunMusic {
    public static void main(String[] args) {
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //ClassApplicationContextFromXML context = new ClassApplicationContextFromXML("src/main/resources/applicationContext.xml");
        //Music music1 = BeanFactory.getInstance().getBean(Music.class);
        //Music music2 = BeanFactory.getInstance().getBean(Music.class);

        //ApplicationContext ctx = Initialization.getApplicationContext();
        Context context = new Context("cases.music1");

        System.out.println(System.getProperty("java.home"));

        MusicPlayer musicPlayer1 = context.getBean(MusicPlayer.class);
        MusicPlayer musicPlayer2 = context.getBean(MusicPlayer.class);


        //MusicPlayer musicPlayer = BeanFactory.getInstance().getBean(MusicPlayer.class);
        //Music music = context.getBean("musicBean", Music.class);

        musicPlayer1.playMusic();
        musicPlayer2.playMusic();
        System.out.println(musicPlayer1);
        System.out.println(musicPlayer2);
        //System.out.println(music1.getSong());
        //System.out.println(music2.getSong());

        //context.close();
    }
}
