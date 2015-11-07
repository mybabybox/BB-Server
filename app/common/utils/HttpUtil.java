package common.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class HttpUtil {

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(urlToRead);
        HttpResponse response = client.execute(request);

        BufferedReader reader = new BufferedReader(
                new InputStreamReader(response.getEntity().getContent()));

        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        return result.toString();
    }
}
