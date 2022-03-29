package cases.music7;

//import framework.context.Context;
//import org.springframework.context.ApplicationContext;

import cases.music8.JazzMusic;
import framework.context.ContextBuilder;
import framework.context.NewContext;

public class RunMusic {
    static NewContext context;

    public static void main(String[] args) {
        var builder = new ContextBuilder();
        context = builder.setConfiguration("src/main/resources/valuesConfig.json").Build();

        context.run("cases.music7");

        RockMusic rockMusic = context.getType(RockMusic.class);
        ClassicalMusic classicalMusic = context.getType(ClassicalMusic.class);
        System.out.println(rockMusic.getSong());
        System.out.println(classicalMusic.getSong());
    }
}
