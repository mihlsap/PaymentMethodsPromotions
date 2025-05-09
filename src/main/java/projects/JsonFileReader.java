package projects;

import jakarta.json.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonFileReader {
    public ArrayList<Order> readOrders(String path) {

        ArrayList<Order> orders = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {

            JsonReader jsonReader = Json.createReader(fileInputStream);
            JsonArray jsonArray = jsonReader.readArray();

            for (JsonValue jsonValue : jsonArray) {

                JsonObject jsonObject = jsonValue.asJsonObject();

                String id = jsonObject.getString("id");
                double value = Double.parseDouble(jsonObject.getString("value"));

                JsonArray promotionsArray = jsonObject.getJsonArray("promotions");

                List<String> promotions = new ArrayList<>();

                if (promotionsArray != null) {
                    for (JsonValue x : promotionsArray) {
                        promotions.add(String.valueOf(x));
                    }
                }

                Order order = new Order(id, value, promotions);
                orders.add(order);
            }

        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        }
        return orders;
    }

    public ArrayList<PaymentMethod> readPaymentMethods(String path) {

        ArrayList<PaymentMethod> paymentMethods = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {

            JsonReader jsonReader = Json.createReader(fileInputStream);
            JsonArray jsonArray = jsonReader.readArray();

            for (JsonValue jsonValue : jsonArray) {

                JsonObject jsonObject = jsonValue.asJsonObject();

                String id = jsonObject.getString("id");

                double discount = Double.parseDouble(jsonObject.getString("discount"));

                double limit = Double.parseDouble(jsonObject.getString("limit"));

                PaymentMethod paymentMethod = new PaymentMethod(id, discount, limit);

                paymentMethods.add(paymentMethod);
            }

        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        }
        return paymentMethods;
    }

}
