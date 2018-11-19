package io.beekeeper.bots.pizza;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.beekeeper.bots.pizza.shell.ProcessExecutor;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class OrderHelper {

    public interface Callback {
        void onSuccess();

        void onFailure();
    }

    public static void executeOrder(Collection<OrderItem> orderItems, Callback callback) {
        List<String> command = Arrays.asList("node", "pizza-ordering/app.js", toJSON(orderItems).toString());

        new Thread(() -> {
            try {
                System.out.println("command = " + command);
                ProcessExecutor.CommandResult result = ProcessExecutor.executeCommand(command);
                if (result.getExitCode() == 0) {
                    callback.onSuccess();
                } else {
                    callback.onFailure();
                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static JsonArray toJSON(Collection<OrderItem> orderItems) {
        JsonArray jsonArray = new JsonArray();
        orderItems.forEach(item -> jsonArray.add(toJSON(item)));
        return jsonArray;
    }

    private static JsonObject toJSON(OrderItem item) {
        String articleNumber = item.getMenuItem().getArticleNumber();
        if (item.getMenuItem().getParentArticleNumber() != null) {
            articleNumber = item.getMenuItem().getParentArticleNumber();
        }

        JsonObject json = new JsonObject();
        json.addProperty("articleId", item.getMenuItem().getArticleId());
        json.addProperty("articleNumber", articleNumber);
        json.addProperty("commodityGroupId", item.getMenuItem().getCommodityGroupId());
        return json;
    }
}