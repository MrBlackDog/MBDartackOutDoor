package com.artack2;

import com.navigine.naviginesdk.Venue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.PublicKey;

public class HttpClient {

    public HttpClient(){
        JSON_Parser json_parser= new JSON_Parser();
    }
    public Venue readVenueInfo(long venueId) throws IOException {
        String requestUrl = "";
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        //String authHeader = getAuth
        //connection.setRequestProperty();
        connection.connect();

        InputStream in;
        int status = connection.getResponseCode();
        if( status != HttpURLConnection.HTTP_OK)
            in = connection.getErrorStream();
        else
            in = connection.getInputStream();
        String response = convertStreamToString(in);
    //    Venue venue = json_parser.getVenue();
        return  null;
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
