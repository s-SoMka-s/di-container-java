package implementation.configuration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class JavaConfiguration implements Configuration {
    @Override
    public String getPackageToScan() {
        return "cases.music2";
    }

    @Override
    public Map<Class, Class> getInterfaceToImplementation() {
        return new ConcurrentHashMap<Class, Class>();
        //return Map.of(Music.class, ClassicalMusic.class);
    }
}
