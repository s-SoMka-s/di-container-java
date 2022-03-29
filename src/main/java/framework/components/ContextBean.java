package framework.components;

//import framework.context.Context;
import framework.context.Context;

public class ContextBean {
    static Context context;

    public void setContext(Context ctx) {
        context = ctx;
    }
}
