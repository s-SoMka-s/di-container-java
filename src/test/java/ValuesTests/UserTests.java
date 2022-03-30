package ValuesTests;

import framework.context.ContextBuilder;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

public class UserTests {

    @Test
    public void internalValues() {
        var builder = new ContextBuilder();
        var context = builder.Build();

        context.run("cases.values.music1");

        cases.values.music1.Computer computer = context.getType(cases.values.music1.Computer.class);
        assert(computer.getCost() == 179999);
        assert(computer.getName().equals("iMac"));
    }

    @Test
    public void externalValues() {
        var builder = new ContextBuilder();
        var context = builder.setConfiguration("src/main/resources/valuesConfig.json").Build();

        context.run("cases.values.music2");

        cases.values.music2.Computer computer = context.getType(cases.values.music2.Computer.class);
        assert(computer.getCost() == 599999);
        assert(computer.getName().equals("Mac Pro"));

        var result = new ArrayList<>();
        result.add("a");
        result.add("bc");
        result.add("def");
        result.removeAll(computer.getWords());
        assert(result.size() == 0);
    }

}