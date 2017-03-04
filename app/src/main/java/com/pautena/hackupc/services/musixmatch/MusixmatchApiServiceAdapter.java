package com.pautena.hackupc.services.musixmatch;

import android.content.Context;
import android.util.Log;

import com.google.gson.JsonObject;
import com.pautena.hackupc.R;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pautenavidal on 3/3/17.
 */

public class MusixmatchApiServiceAdapter {
    private static final String TAG = "MusixApiAdapter";

    private static MusixmatchApiServiceAdapter instance;
    private final Context context;

    public static MusixmatchApiServiceAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new MusixmatchApiServiceAdapter(context.getApplicationContext());
        }
        return instance;
    }


    private String apikey;
    private MusixmatchApiService service;

    public MusixmatchApiServiceAdapter(Context context) {
        this.context = context;

        String url = getApiUrl(context);

        apikey = context.getResources().getString(R.string.musixmatch_api_key);

        OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();
                HttpUrl originalHttpUrl = original.url();

                HttpUrl url = originalHttpUrl.newBuilder()
                        .addQueryParameter("apikey",apikey)
                        .build();

                // Request customization: add request headers
                Request.Builder requestBuilder = original.newBuilder()
                        .url(url);

                Request request = requestBuilder.build();
                return chain.proceed(request);
            }
        });

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(httpClient.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        Log.d(TAG, url);
        service = retrofit.create(MusixmatchApiService.class);
    }

    public static String getApiUrl(Context context) {

        String apiUrl = context.getResources().getString(R.string.api_musixmatch_url);
        return "http://" + apiUrl;
    }


    public void search(String query,CallbackSearch callback){

        Call<JsonObject> call = service.search(query);


        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                Log.d(TAG,"search("+response.code()+"): "+response.body());

                if(response.isSuccessful()){

                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {

            }
        });

    }


}
