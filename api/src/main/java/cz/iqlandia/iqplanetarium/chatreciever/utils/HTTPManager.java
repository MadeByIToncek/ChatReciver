package cz.iqlandia.iqplanetarium.chatreciever.utils;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.StringJoiner;

public class HTTPManager {
    public static String postHTTPString(String address, String body) throws IOException {
        URL url = new URL(address);
        URLConnection con = url.openConnection();
        HttpURLConnection http = (HttpURLConnection)con;
        http.setRequestMethod("POST"); // PUT is another valid option
        http.setDoOutput(true);
        http.setDoInput(true);

        byte[] out = body.getBytes(StandardCharsets.UTF_8);
        int length = out.length;

        http.setFixedLengthStreamingMode(length);
        http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        http.connect();
        try(OutputStream os = http.getOutputStream()) {
            os.write(out);
        }

        StringJoiner sj = new StringJoiner("\n");
        try (Scanner sc = new Scanner(http.getInputStream()))
        {
            while (sc.hasNextLine()) sj.add(sc.nextLine());
        }
        return sj.toString();
    }
    public static JSONObject postHTTPJSONObject(String address, JSONObject body) throws IOException {
        return new JSONObject(postHTTPString(address, body.toString(4)));
    }

    public static String getHTTPString(String address) throws IOException {
        URL url = new URL(address);

        StringJoiner sj = new StringJoiner("\n");
        try (Scanner sc = new Scanner(url.openConnection().getInputStream()))
        {
            while (sc.hasNextLine()) sj.add(sc.nextLine());
        }
        return sj.toString();
    }
}
