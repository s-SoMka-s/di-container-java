package framework.components;

//import framework.context.Context;
import framework.context.NewContext;

public class ContextBean {
    static NewContext context;

    public void setContext(NewContext ctx) {
        context = ctx;
    }
}
