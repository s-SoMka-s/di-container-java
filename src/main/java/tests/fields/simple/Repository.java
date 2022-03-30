package tests.fields.simple;

import framework.annotations.Component;
import framework.annotations.Inject;

@Component
public class Repository {
    @Inject
    private DbContext dbContext;

    public String getConnectionString() {
        return dbContext.getConnectionString();
    }
}
