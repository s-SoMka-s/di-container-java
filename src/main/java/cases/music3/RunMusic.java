package cases.music3;

import implementation.context.Context;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
//import org.springframework.context.ApplicationContext;


public class RunMusic {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //ClassApplicationContextFromXML context = new ClassApplicationContextFromXML("src/main/resources/applicationContext.xml");
        //Music music1 = BeanFactory.getInstance().getBean(Music.class);
        //Music music2 = BeanFactory.getInstance().getBean(Music.class);

        //ApplicationContext ctx = Initialization.getApplicationContext();

        cases.music3.MusicPlayer musicPlayer1 = context.getBean(cases.music3.MusicPlayer.class);
        cases.music3.MusicPlayer musicPlayer2 = context.getBean(MusicPlayer.class);

        Computer computer1 = context.getBean(Computer.class);
        Computer computer2 = context.getBean(Computer.class);
        System.out.println(computer1.result());
        System.out.println(computer2.result());

        System.out.println(computer1);
        System.out.println(computer2);

        System.out.println(musicPlayer1);
        System.out.println(musicPlayer2);

        context.close();
    }
}
