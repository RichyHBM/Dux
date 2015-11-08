package utilities;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class WebRequest {
    public static String jsonPost(String url, String json) throws Exception {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setDoOutput(true);
        con.setDoInput(true);
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestMethod("POST");

        OutputStreamWriter outputWriter = new OutputStreamWriter(con.getOutputStream());
        outputWriter.write(json);
        outputWriter.flush();

        int responseCode = con.getResponseCode();
        if(responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("Server returned " + responseCode);
        }

        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(),"utf-8"));

        String line = null;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        return sb.toString();
    }
}
