package implementation.bean;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import implementation.Bean;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ClassApplicationContextFromJSON {
    String configLocation;

    public ClassApplicationContextFromJSON(String configLocation) {
        this.configLocation = configLocation;

        try {
            JsonReader jsonReader = new JsonReader(new FileReader(configLocation));
            Gson gson = new Gson();
            Type listOfMyClassObject = new TypeToken<ArrayList<Bean>>() {}.getType();

            List<Bean> outputList = gson.fromJson(jsonReader, listOfMyClassObject);
            FileInputStream fis = new FileInputStream(configLocation);
            ObjectInputStream ois = new ObjectInputStream(fis);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
