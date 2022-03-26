package framework.context;

import cases.server.Server;

public class ContextBuilder {
    private NewContext context;

    public ContextBuilder() {
        this.context = new NewContext();
    }

    public ContextBuilder setConfiguration(String path) {
        context.setConfiguration(path);

        return this;
    }

    public NewContext Build() {
        return context;
    }
}
