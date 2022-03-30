package tests.constructors.config_value;

import framework.annotations.Autowired;
import framework.annotations.Component;
import framework.annotations.Value;

@Component
public class RemoteSource {
    private final String url;
    @Autowired
    public RemoteSource(@Value("$url") String url) {
        this.url = url;
    }

    public String getUrl() {
        return this.url;
    }
}
