package tests.fields.simple;

import framework.annotations.Component;

@Component
public class DbContext {
    public String getConnectionString() {
        return "simple-db.db";
    }
}
