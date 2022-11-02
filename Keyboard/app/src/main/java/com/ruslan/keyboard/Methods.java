package com.ruslan.keyboard;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Methods {
    public static String getContent(String path) throws IOException {
        BufferedReader reader = null;
        InputStream stream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(10000);
            connection.connect();
            stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuilder buf = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buf.append(line);
            }
            return buf.toString();
        }
        finally {
            if (reader != null)
                reader.close();
            if (stream != null)
                stream.close();
            if (connection != null)
                connection.disconnect();
        }
    }

    public static String postContent(String path, String body) throws IOException {
        BufferedWriter writer = null;
        OutputStream stream = null;
        HttpURLConnection connection = null;
        try {
            Map<String, String> map = new HashMap<>();
            map.put("id", "62");
            map.put("userId", "3");
            map.put("word", "конфета");
            map.put("count", "1");
            byte[] out = map.toString().getBytes();
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true);
            connection.setDoInput(true);

            connection.setConnectTimeout(10000);
            connection.connect();

            stream = connection.getOutputStream();
            writer = new BufferedWriter(new OutputStreamWriter(stream));
            stream.write(out);
//            writer.write(out);
//            writer.write("body=" + body);
//            writer.flush();
            if (connection.getResponseCode() == 200 || connection.getResponseCode() == 201) {
                System.out.println(connection.getResponseCode());
                System.out.println(connection.getResponseMessage());
                return connection.getResponseMessage();
            }
            return "";
        }
        finally {
            if (writer != null)
                writer.close();
            if (stream != null)
                stream.close();
            if (connection != null)
                connection.disconnect();
        }
    }
}
