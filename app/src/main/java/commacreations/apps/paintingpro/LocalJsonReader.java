package commacreations.apps.paintingpro;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class LocalJsonReader {

    // Method to get informations from a json file from assets directory.
    public ArrayList getDataFromJsonFile(InputStream is) {

        ArrayList<HashMap<String, String>> productsList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> productMap = null;

        try {
            // Get json objects form json file.
            JSONObject objects = new JSONObject(loadJSON(is));

            // Get array of objects (in this case there is one).
            JSONArray productsArray = objects.getJSONArray("products");

            // Get all values of products and store them in an HashMap as a key value.
            for (int i = 0; i < productsArray.length(); i++) {

                // Get product infos one by one.
                JSONObject object = productsArray.getJSONObject(i);
                String reference = object.getString("reference");
                String category = object.getString("category");
                String application = object.getString("application");
                String diluted = object.getString("diluted");
                String cov = object.getString("cov");
                String emmission = object.getString("emmission");

                // Add values in hashmap.
                productMap = new HashMap<String, String>();
                productMap.put("reference", reference);
                productMap.put("category", category);
                productMap.put("application", application);
                productMap.put("diluted", diluted);
                productMap.put("cov", cov);
                productMap.put("emission", emmission);

                productsList.add(productMap);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return productsList;
    }

    // Method to parse json file and return infos as a string.
    public String loadJSON(InputStream is) {

        // Read the file as input stream.
        String json = null;
        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
