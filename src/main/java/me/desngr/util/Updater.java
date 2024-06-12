package me.desngr.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

public class Updater {

    public static CompletableFuture<Boolean> isLatestVersion(String current) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://api.github.com/repos/desngr/ShiroEconomy/releases/latest")
                        .openConnection();
                InputStream inputStream = connection.getInputStream();
                JSONObject response = (JSONObject) new JSONParser().parse(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8)
                );

                return response.get("tag_name").equals(current);
            } catch (Exception e) {
                Logger.error("Error while getting latest version.");

                return true;
            }
        });
    }
}
