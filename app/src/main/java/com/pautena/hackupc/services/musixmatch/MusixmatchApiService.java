package com.pautena.hackupc.services.musixmatch;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by pautenavidal on 3/3/17.
 */

public interface MusixmatchApiService {

    @GET("/track.search")
    Call<JsonObject> search(String query);
}
