package utils;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.InputStream;

public class JsonReader {

    private static JSONObject data = null;

    private static void loadData() {
        if (data != null) return;

        try {
            InputStream is = JsonReader.class
                    .getClassLoader()
                    .getResourceAsStream("testdata.json");

            if (is == null) {
                throw new RuntimeException("testdata.json NOT FOUND in resources");
            }

            JSONTokener tokener = new JSONTokener(is);
            data = new JSONObject(tokener);

            System.out.println("JSON Loaded Successfully");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to load testdata.json", e);
        }
    }

    public static String getTestData(String parent, String key) {
        loadData();
        return data.getJSONObject(parent).getString(key);
    }
}