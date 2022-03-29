package ScopeTests;

import framework.context.ContextBuilder;
import framework.context.Context;
import org.junit.Test;

import java.util.concurrent.CountDownLatch;

public class UserTests {

    @Test
    public void onlySingletonTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.scope.music1");

        cases.scope.music1.Computer computer1 = context.getType(cases.scope.music1.Computer.class);
        cases.scope.music1.Computer computer2 = context.getType(cases.scope.music1.Computer.class);

        cases.scope.music1.MusicPlayer musicPlayer1 = context.getType(cases.scope.music1.MusicPlayer.class);
        cases.scope.music1.MusicPlayer musicPlayer2 = context.getType(cases.scope.music1.MusicPlayer.class);

        cases.scope.music1.Music music1 = context.getType(cases.scope.music1.ClassicalMusic.class);
        cases.scope.music1.Music music2 = context.getType(cases.scope.music1.ClassicalMusic.class);

        cases.scope.music1.Music music3 = context.getType(cases.scope.music1.RockMusic.class);
        cases.scope.music1.Music music4 = context.getType(cases.scope.music1.RockMusic.class);

        assert (computer1.equals(computer2));
        assert (musicPlayer1.equals(musicPlayer2));
        assert (music1.equals(music2));
        assert (music3.equals(music4));
    }

    @Test
    public void onlyPrototypeTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.scope.music2");

        cases.scope.music2.Computer computer1 = context.getType(cases.scope.music2.Computer.class);
        cases.scope.music2.Computer computer2 = context.getType(cases.scope.music2.Computer.class);

        cases.scope.music2.MusicPlayer musicPlayer1 = context.getType(cases.scope.music2.MusicPlayer.class);
        cases.scope.music2.MusicPlayer musicPlayer2 = context.getType(cases.scope.music2.MusicPlayer.class);

        cases.scope.music2.Music music1 = context.getType(cases.scope.music2.ClassicalMusic.class);
        cases.scope.music2.Music music2 = context.getType(cases.scope.music2.ClassicalMusic.class);

        cases.scope.music2.Music music3 = context.getType(cases.scope.music2.RockMusic.class);
        cases.scope.music2.Music music4 = context.getType(cases.scope.music2.RockMusic.class);

        assert (!computer1.equals(computer2));
        assert (!musicPlayer1.equals(musicPlayer2));
        assert (!music1.equals(music2));
        assert (!music3.equals(music4));
    }

    @Test
    public void SingletonPrototypeTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.scope.music3");

        cases.scope.music3.Computer computer1 = context.getType(cases.scope.music3.Computer.class);
        cases.scope.music3.Computer computer2 = context.getType(cases.scope.music3.Computer.class);

        cases.scope.music3.MusicPlayer musicPlayer1 = context.getType(cases.scope.music3.MusicPlayer.class);
        cases.scope.music3.MusicPlayer musicPlayer2 = context.getType(cases.scope.music3.MusicPlayer.class);

        cases.scope.music3.Music music1 = context.getType(cases.scope.music3.ClassicalMusic.class);
        cases.scope.music3.Music music2 = context.getType(cases.scope.music3.ClassicalMusic.class);

        cases.scope.music3.Music music3 = context.getType(cases.scope.music3.RockMusic.class);
        cases.scope.music3.Music music4 = context.getType(cases.scope.music3.RockMusic.class);

        assert (computer1.equals(computer2));
        assert (!musicPlayer1.equals(musicPlayer2));
        assert (music1.equals(music2));
        assert (!music3.equals(music4));
    }

    static Context context;

    @Test
    public void ThreadPrototypeTest() {
        var builder = new ContextBuilder();
        context = builder.Build();

        context.run("cases.scope.music4");

        new ThreadTest();
        new ThreadTest();

        while (START.getCount() > 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        assert(thread1_cm1 != thread1_cm2 && thread1_cm1 != thread2_cm2 && thread1_cm1 != thread2_cm1);
        assert(thread1_rm1 == thread1_rm2 && thread2_rm1 == thread2_rm2 && thread1_rm1 != thread2_rm1);
        assert(thread1_c1 == thread1_c2 && thread1_c1 == thread2_c2 && thread1_c1 == thread2_c1);
    }

    static cases.scope.music4.RockMusic thread1_rm1;
    static cases.scope.music4.RockMusic thread1_rm2;
    static cases.scope.music4.RockMusic thread2_rm1;
    static cases.scope.music4.RockMusic thread2_rm2;
    static cases.scope.music4.ClassicalMusic thread1_cm1;
    static cases.scope.music4.ClassicalMusic thread1_cm2;
    static cases.scope.music4.ClassicalMusic thread2_cm1;
    static cases.scope.music4.ClassicalMusic thread2_cm2;
    static cases.scope.music4.Computer thread1_c1;
    static cases.scope.music4.Computer thread1_c2;
    static cases.scope.music4.Computer thread2_c1;
    static cases.scope.music4.Computer thread2_c2;

    private static final CountDownLatch START = new CountDownLatch(2);
    static final Object obj = new Object();
    static int i = 0;

    static class ThreadTest implements Runnable {
        ThreadTest() {
            new Thread(this).start();
        }

        @Override
        public void run() {
            var rm1 = context.getType(cases.scope.music4.RockMusic.class);
            var rm2 = context.getType(cases.scope.music4.RockMusic.class);
            var cm1 = context.getType(cases.scope.music4.ClassicalMusic.class);
            var cm2 = context.getType(cases.scope.music4.ClassicalMusic.class);
            var c1 = context.getType(cases.scope.music4.Computer.class);
            var c2 = context.getType(cases.scope.music4.Computer.class);

            synchronized (obj) {
                if (i == 0) {
                    thread1_rm1 = rm1;
                    thread1_rm2 = rm2;
                    thread1_cm1 = cm1;
                    thread1_cm2 = cm2;
                    thread1_c1 = c1;
                    thread1_c2 = c2;
                    i++;
                } else {
                    thread2_rm1 = rm1;
                    thread2_rm2 = rm2;
                    thread2_cm1 = cm1;
                    thread2_cm2 = cm2;
                    thread2_c1 = c1;
                    thread2_c2 = c2;
                }
                START.countDown();
            }
        }
    }
}