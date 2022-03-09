package implementation;

import implementation.context.Context;
import implementation.factory.BeanFactory;

public class AutumnDI {
    public static Context getApplicationContext() {
        Context context = new Context();
        BeanFactory beanFactory = new implementation.factory.BeanFactory(context);
        context.setBeanFactory(beanFactory);

        return context;
    }
}
