package cases.cycles.music3;

import framework.context.ContextBuilder;
import framework.context.Context;

public class RunMusic {
    static Context context;

    public static void main(String[] args) {
        var builder = new ContextBuilder();
        context = builder.setConfiguration("src/main/resources/valuesConfig.json").Build();

        context.run("cases.cycles.music3");

        RockMusic rockMusic = context.getType(RockMusic.class);
        JazzMusic jazzMusic = context.getType(JazzMusic.class);
        ClassicalMusic classicalMusic = context.getType(ClassicalMusic.class);
        System.out.println(rockMusic.getSong());
        System.out.println(jazzMusic.getSong());
        System.out.println(classicalMusic.getSong());

        assert (jazzMusic == classicalMusic.getJazzMusic() && rockMusic == jazzMusic.getRockMusic()
                && classicalMusic == rockMusic.getClassicalMusic());
    }
}
