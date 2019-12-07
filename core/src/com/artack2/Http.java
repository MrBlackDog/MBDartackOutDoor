package com.artack2;

import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Http {

    JsonReader run(String url) throws IOException {
    //String run(String url, Callback callback)  {
        OkHttpClient client = new OkHttpClient.Builder().build();
        Request request = new Request.Builder()
                .url("https://api.navigine.com/venue/getAll?locationId=Skolkovo&sublocationId={2}&userHash={46E2-62B2-A3FB-CC8D}&format={json}").get().build();

        Response response = client.newCall(request).execute();
        //Если не заробит
        //Response response = client.newCall(request).execute();

        // try( Response response = client.newCall(request).execute()) ;
        return new JsonReader();
    }

    public Http() throws IOException {
    }
    // GET SERVER_URL/venue/getAll?sublocationId={sublocationId}&userHash={userHash}&format={format}
}
