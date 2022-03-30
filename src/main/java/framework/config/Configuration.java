package framework.config;

import lombok.SneakyThrows;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Configuration {
    private Map<String, Object> variablesJSON = new ConcurrentHashMap<>();

    public Configuration(String path) {
        deserialize(path);
    }

    public Object getValue(String key) {
        if (variablesJSON.containsKey(key)) {
            return variablesJSON.get(key);
        }

        return null;
    }

    @SneakyThrows
    private void deserialize(String path) {
        var file = new File(path);
        if (!file.exists()) {
            throw new RuntimeException("File not found");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        TypeReference<ConcurrentHashMap<String, Object>> typeRef = new TypeReference<>() {
        };

        try {
            variablesJSON = objectMapper.readValue(file, typeRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
