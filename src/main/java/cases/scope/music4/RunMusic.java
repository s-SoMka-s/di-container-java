package cases.scope.music4;

//import framework.context.Context;
//import org.springframework.context.ApplicationContext;

import framework.context.ContextBuilder;
import framework.context.NewContext;

public class RunMusic {
    static NewContext context;

    public static void main(String[] args) {
        var builder = new ContextBuilder();
        context = builder.setConfiguration("src/main/resources/valuesConfig.json").Build();

        context.run("cases.scope.music4");

        new ThreadTest();
        new ThreadTest();

        MusicPlayer musicPlayer1 = context.getType(MusicPlayer.class);
        MusicPlayer musicPlayer2 = context.getType(MusicPlayer.class);

        Computer computer1 = context.getType(Computer.class);
        Computer computer2 = context.getType(Computer.class);
        System.out.println(computer1.result());
        System.out.println(computer2.result());

        System.out.println(musicPlayer1);
        System.out.println(musicPlayer2);

        System.out.println(computer1);
        System.out.println(computer2);
    }

    static final Object obj = new Object();

    static class ThreadTest implements Runnable {
        ThreadTest() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            var rm1 = context.getType(RockMusic.class);
            var rm2 = context.getType(RockMusic.class);
            var cm1 = context.getType(ClassicalMusic.class);
            var cm2 = context.getType(ClassicalMusic.class);
            var mp1 = context.getType(MusicPlayer.class);
            var mp2 = context.getType(MusicPlayer.class);

            synchronized (obj) {
                System.out.println("Id: " + Thread.currentThread().getId() + "; rm1: " + rm1);
                System.out.println("Id: " + Thread.currentThread().getId() + "; rm2: " + rm2);
                System.out.println("Id: " + Thread.currentThread().getId() + "; cm1: " + cm1);
                System.out.println("Id: " + Thread.currentThread().getId() + "; cm2: " + cm2);
                System.out.println("Id: " + Thread.currentThread().getId() + "; mp1: " + mp1);
                System.out.println("Id: " + Thread.currentThread().getId() + "; mp2: " + mp2);
                System.out.println("Id: " + Thread.currentThread().getId() + "; mp1 cm: " + mp1.classicalMusic);
                System.out.println("Id: " + Thread.currentThread().getId() + "; mp1 rm: " + mp1.rockMusic);
                System.out.println("Id: " + Thread.currentThread().getId() + "; mp2 cm: " + mp2.classicalMusic);
                System.out.println("Id: " + Thread.currentThread().getId() + "; mp2 rm: " + mp2.rockMusic);
                System.out.println("");
            }
        }
    }
}
