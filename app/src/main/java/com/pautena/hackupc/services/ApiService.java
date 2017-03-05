package com.pautena.hackupc.services;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

/**
 * Created by pautenavidal on 10/8/16.
 */
public interface ApiService {

    @FormUrlEncoded
    @POST("/users/register")
    Call<JsonObject> register(@Field("email") String email,
                              @Field("password") String password,
                              @Field("check_password") String checkPassword,
                              @Field("username") String firstName);


    @FormUrlEncoded
    @POST("users/login")
    Call<JsonObject> login(@Field("username") String username,
                           @Field("password") String password);

    @FormUrlEncoded
    @POST("methods/setPushNotificationsToken")
    Call<JsonObject> updateUserFirebaseToken(@Field("token") String firebaseToken);

    @FormUrlEncoded
    @POST("methods/createRoom")
    Call<JsonObject> createRoom(@Field("songTitle") String songTitle,
                                @Field("songArtist") String songArtist,
                                @Field("songId") String songId,
                                @Field("songImage") String songImg,
                                @Field("singer1") String singer1Username);

    @FormUrlEncoded
    @POST("methods/requestPartner")
    Call<JsonObject> requestJoin(@Field("roomId") String roomId,
                                 @Field("partnerId") String partnerId);

    @FormUrlEncoded
    @POST("methods/acceptRequest")
    Call<JsonObject> acceptRequest(@Field("roomId") String roomId);

    @POST("methods/getUsers")
    Call<JsonArray> getUsers();

}
