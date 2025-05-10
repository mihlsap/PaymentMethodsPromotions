package projects;

import jakarta.json.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// class to read data from files
public class JsonFileReader {

    ArrayList<Order> orders;
    ArrayList<PaymentMethod> paymentMethods;
    HashMap<String, ArrayList<Order>> ordersByPromotion;

    public JsonFileReader(String ordersPath, String paymentMethodsPath) {
        readOrders(ordersPath);
        readPaymentMethods(paymentMethodsPath);
    }

    // method checking whether a file is a .json file
    private void checkFileExtension(String path) {
        if (!path.endsWith(".json")) {
            throw new IllegalArgumentException("Wrong file type! File must have .json extension!");
        }
    }

    // method for reading orders and returning them as an ArrayList
    private void readOrders(String path) {

        // checking whether the file has the correct extension
        checkFileExtension(path);

        orders = new ArrayList<>();
        ordersByPromotion = new HashMap<>();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {

            JsonReader jsonReader = Json.createReader(fileInputStream);

            // read the content of the file as JsonArray
            JsonArray jsonArray = jsonReader.readArray();

            for (JsonValue jsonValue : jsonArray) {

                // store an element of the array contents as JsonObject
                JsonObject jsonObject = jsonValue.asJsonObject();

                // retrieve the content of the element and store it in variables, remove quotation marks for Strings
                String id = jsonObject.getString("id").replace("\"", "");

                double value = Double.parseDouble(jsonObject.getString("value"));

                JsonArray promotionsArray = jsonObject.getJsonArray("promotions");

                List<String> promotions = new ArrayList<>();

                // if a retrieved promotions array is not null, add it to the list
                if (promotionsArray != null) {
                    for (JsonValue x : promotionsArray) {
                        promotions.add(String.valueOf(x).replace("\"", ""));
                    }
                }

                // add "PUNKTY" to each list
                promotions.add("PUNKTY");

                // create an Order object with retrieved data and add it to the ArrayList
                Order order = new Order(id, value, promotions);
                orders.add(order);

                // for every promotion available for a given order
                for (String x : promotions) {

                    // add new ArrayList if there is no ArrayList corresponding to the order's promotion
                    ordersByPromotion.putIfAbsent(x, new ArrayList<>());

                    // add an Order object to the ArrayList in a HashMap corresponding to the order's promotion
                    ordersByPromotion.get(x).add(order);
                }
            }
            // exception handling for FileNotFoundException and IOException
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException("File not found: " + fileNotFoundException.getMessage(), fileNotFoundException);
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    // method for reading payment methods and returning them as a HashMap for easy searching by id
    private void readPaymentMethods(String path) {

        // checking whether the file has the correct extension
        checkFileExtension(path);

        paymentMethods = new ArrayList<>();

        try (FileInputStream fileInputStream = new FileInputStream(path)) {

            JsonReader jsonReader = Json.createReader(fileInputStream);

            // read the content of the file as JsonArray
            JsonArray jsonArray = jsonReader.readArray();

            for (JsonValue jsonValue : jsonArray) {

                // store an element of the array contents as JsonObject
                JsonObject jsonObject = jsonValue.asJsonObject();

                // retrieve the content of the element and store it in variables, remove quotation marks for Strings
                String id = jsonObject.getString("id").replace("\"", "");

                double discount = Double.parseDouble(jsonObject.getString("discount"));

                double limit = Double.parseDouble(jsonObject.getString("limit"));

                // create a PaymentMethod object with retrieved data
                PaymentMethod paymentMethod = new PaymentMethod(id, discount, limit);

                // add a created PaymentMethod object to the HashMap with the id as a key
                paymentMethods.add(paymentMethod);

            }

            // exception handling for FileNotFoundException and IOException
        } catch (FileNotFoundException fileNotFoundException) {
            throw new RuntimeException("File not found: " + fileNotFoundException.getMessage(), fileNotFoundException);
        } catch (IOException ioException) {
            System.err.println(ioException.getMessage());
            ioException.printStackTrace();
        }
    }

    // getter methods for the data read from the files
    public ArrayList<Order> getOrders() {
        return orders;
    }

    public ArrayList<PaymentMethod> getPaymentMethods() {
        return paymentMethods;
    }

    public HashMap<String, ArrayList<Order>> getOrdersByPromotion() {
        return ordersByPromotion;
    }
}
