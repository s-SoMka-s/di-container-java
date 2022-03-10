package implementation.configuration;

import java.util.Map;

public class XMLConfiguration implements Configuration {
    @Override
    public String getPackageToScan() {
        return null;
    }

    @Override
    public Map<Class, Class> getInterfaceToImplementation() {
        return null;
    }
}
