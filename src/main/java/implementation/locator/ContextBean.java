package implementation.locator;

import implementation.context.Context;

public class ContextBean {
    static Context context;

    public void setContext(Context ctx) {
        context = ctx;
    }
}
