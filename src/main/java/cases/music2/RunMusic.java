package cases.music2;

import implementation.context.Context;
//import org.springframework.context.ApplicationContext;


public class RunMusic {
    public static void main(String[] args) {
        //ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        //ClassApplicationContextFromXML context = new ClassApplicationContextFromXML("src/main/resources/applicationContext.xml");
        //Music music1 = BeanFactory.getInstance().getBean(Music.class);
        //Music music2 = BeanFactory.getInstance().getBean(Music.class);

        //ApplicationContext ctx = Initialization.getApplicationContext();
        Context context = new Context();

        Computer computer1 = context.getBean(Computer.class);
        Computer computer2 = context.getBean(Computer.class);
        System.out.println(computer1.result());
        System.out.println(computer2.result());

        System.out.println(computer1);
        System.out.println(computer2);
    }
}
