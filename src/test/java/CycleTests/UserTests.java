package CycleTests;

import framework.context.ContextBuilder;
import org.junit.jupiter.api.Test;

public class UserTests {

    @Test
    public void cycleLengthTwo() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.cycles.music1");

        cases.cycles.music1.RockMusic rockMusic = context.getType(cases.cycles.music1.RockMusic.class);
        cases.cycles.music1.ClassicalMusic classicalMusic = context.getType(cases.cycles.music1.ClassicalMusic.class);

        assert (rockMusic == classicalMusic.getRockMusic() && classicalMusic == rockMusic.getClassicalMusic());
    }

    @Test
    // Why when completing all tests classes mixes while completing only this test everythin is ok????
    public void cycleLengthThree() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context = builder.Build();

        context.run("cases.cycles.music3");

        cases.cycles.music3.RockMusic rockMusic = context.getType(cases.cycles.music3.RockMusic.class);
        cases.cycles.music3.JazzMusic jazzMusic = context.getType(cases.cycles.music3.JazzMusic.class);
        cases.cycles.music3.ClassicalMusic classicalMusic = context.getType(cases.cycles.music3.ClassicalMusic.class);

        assert (jazzMusic == classicalMusic.getJazzMusic() && rockMusic == jazzMusic.getRockMusic()
                && classicalMusic == rockMusic.getClassicalMusic());
    }

}