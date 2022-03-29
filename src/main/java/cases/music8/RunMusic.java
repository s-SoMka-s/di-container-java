package cases.music8;

import framework.context.ContextBuilder;
import framework.context.NewContext;

public class RunMusic {
    static NewContext context;

    public static void main(String[] args) {
        var builder = new ContextBuilder();
        context = builder.setConfiguration("src/main/resources/valuesConfig.json").Build();

        context.run("cases.music8");

        RockMusic rockMusic = context.getType(RockMusic.class);
        JazzMusic jazzMusic = context.getType(JazzMusic.class);
        ClassicalMusic classicalMusic = context.getType(ClassicalMusic.class);
        System.out.println(rockMusic.getSong());
        System.out.println(jazzMusic.getSong());
        System.out.println(classicalMusic.getSong());
    }
}
