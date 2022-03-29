package cases.cycles.music1;

import framework.context.ContextBuilder;
import framework.context.NewContext;

public class RunMusic {
    static NewContext context;

    public static void main(String[] args) {
        var builder = new ContextBuilder();
        context = builder.Build();

        context.run("cases.cycles.music1");

        RockMusic rockMusic = context.getType(RockMusic.class);
        ClassicalMusic classicalMusic = context.getType(ClassicalMusic.class);
        System.out.println(rockMusic.getSong());
        System.out.println(classicalMusic.getSong());

        assert (rockMusic == classicalMusic.getRockMusic() && classicalMusic == rockMusic.getClassicalMusic());

    }
}
