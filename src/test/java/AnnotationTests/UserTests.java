package AnnotationTests;

import framework.context.ContextBuilder;
import org.junit.jupiter.api.Test;

public class UserTests {

    @Test
    public void namedIdTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.annotations.music1");

        cases.annotations.music1.MusicPlayer musicPlayer1 = context.getType(cases.annotations.music1.MusicPlayer.class);
        cases.annotations.music1.MusicPlayer musicPlayer2 = context.getType(cases.annotations.music1.MusicPlayer.class);

        cases.annotations.music1.Computer computer1 = context.getType(cases.annotations.music1.Computer.class);
        cases.annotations.music1.Computer computer2 = context.getType(cases.annotations.music1.Computer.class);

        cases.annotations.music1.Music music1 = context.getType(cases.annotations.music1.ClassicalMusic.class);
        cases.annotations.music1.Music music2 = context.getType(cases.annotations.music1.ClassicalMusic.class);

        cases.annotations.music1.Music music3 = context.getType(cases.annotations.music1.RockMusic.class);
        cases.annotations.music1.Music music4 = context.getType(cases.annotations.music1.RockMusic.class);

        assert (computer1.equals(computer2));
        assert (musicPlayer1.equals(musicPlayer2));
        assert (music1.equals(music2));
        assert (music3.equals(music4));
    }

    @Test
    public void PartiallyIdComponentsTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.annotations.music2");

        cases.annotations.music2.MusicPlayer musicPlayer1 = context.getType(cases.annotations.music2.MusicPlayer.class);
        cases.annotations.music2.MusicPlayer musicPlayer2 = context.getType(cases.annotations.music2.MusicPlayer.class);

        cases.annotations.music2.Computer computer1 = context.getType(cases.annotations.music2.Computer.class);
        cases.annotations.music2.Computer computer2 = context.getType(cases.annotations.music2.Computer.class);

        cases.annotations.music2.Music music1 = context.getType(cases.annotations.music2.ClassicalMusic.class);
        cases.annotations.music2.Music music2 = context.getType(cases.annotations.music2.ClassicalMusic.class);

        cases.annotations.music2.Music music3 = context.getType(cases.annotations.music2.RockMusic.class);
        cases.annotations.music2.Music music4 = context.getType(cases.annotations.music2.RockMusic.class);

        assert (computer1.equals(computer2));
        assert (musicPlayer1.equals(musicPlayer2));
        assert (music1.equals(music2));
        assert (music3.equals(music4));
    }

    @org.junit.Test(expected = RuntimeException.class)
    public void NoSuchIdComponentsTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();


        try {
            context.run("cases.annotations.music3");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        context.run("cases.annotations.music3");
    }

    @org.junit.Test(expected = RuntimeException.class)
    public void SameIdComponentsTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        try {
            context.run("cases.annotations.music4");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        context.run("cases.annotations.music4");
    }

    @org.junit.Test(expected = RuntimeException.class)
    public void AmbiguousComponentsTest() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        try {
            context.run("cases.annotations.music5");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }

        context.run("cases.annotations.music5");
    }
}