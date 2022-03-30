package framework.context;

public class ContextBuilder {
    private Context context;

    public ContextBuilder() {
        this.context = new Context();
    }

    public ContextBuilder setConfiguration(String path) {
        context.setConfiguration(path);

        return this;
    }

    public Context Build() {
        return context;
    }
}
