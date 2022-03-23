package cases.music4;

import framework.context.Context;

public class RunMusic {
    static Context context;

    public static void main(String[] args) {
        context = new Context("cases.music4");

        new ThreadTest();
        new ThreadTest();

        MusicPlayer musicPlayer1 = context.getBean(MusicPlayer.class);
        MusicPlayer musicPlayer2 = context.getBean(MusicPlayer.class);

        Computer computer1 = context.getBean("PC", Computer.class);
        Computer computer2 = context.getBean("PC", Computer.class);
        System.out.println(computer1.result());
        System.out.println(computer2.result());

        System.out.println(musicPlayer1);
        System.out.println(musicPlayer2);

        System.out.println(computer1);
        System.out.println(computer2);
    }

    static class ThreadTest implements Runnable {
        ThreadTest() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            var a = context.getBean("rockMusic", RockMusic.class);
            var b = context.getBean("rockMusic", RockMusic.class);
            System.out.println("Id: " + Thread.currentThread().getId() + "; 1: " + a);
            System.out.println("Id: " + Thread.currentThread().getId() + "; 2: " + b);
        }
    }
}
