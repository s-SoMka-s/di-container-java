package implementation.bean;

import java.io.Serializable;

public class Bean implements Serializable {
    private Class aClass;
    private String id;
    private LifeCycle lifeCycle;
    private Object object;
    //private initParams;
    //private constParams;

    public Bean() {
    }

    // по дефолту тип жизненного цикла - Singleton.
    public Bean(Class aClass, String id, String lifeCycle) {
        this.aClass = aClass;
        this.id = id;
        if (lifeCycle != null) {
            this.lifeCycle = LifeCycle.valueOf(lifeCycle.toUpperCase());
        } else {
            this.lifeCycle = LifeCycle.SINGLETON;
        }
    }


    public Object getBean() {
        return object;
    }
}
