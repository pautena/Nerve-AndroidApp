package com.pautena.hackupc.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.RequestUser;
import com.pautena.hackupc.entities.Song;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.entities.VideoRoom;
import com.pautena.hackupc.entities.manager.UserManager;
import com.pautena.hackupc.services.callback.CreateRoomCallback;
import com.pautena.hackupc.services.callback.FinishGetFriendsCallback;
import com.pautena.hackupc.services.callback.RequestJoinCallback;
import com.twilio.video.VideoRenderer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceAdapter {
    private static final String TAG = ApiServiceAdapter.class.getSimpleName();

    public static final int BROADCAST_ERROR_CODE = 500;
    public static final String BROADCAST_ARG_CODE = "broadcast.argCode";

    public static final String BROADCAST_FINISH_REGISTER = "broadcastFinishRegister";

    public static final String BROADCAST_FINISH_LOGIN = "broadcastFinishLogin";

    public static final String BROADCAST_FINISH_LOGOUT = "broadcastFinishLogout";

    private static ApiServiceAdapter instance;

    public static ApiServiceAdapter getInstance(Context context) {
        if (instance == null) {
            instance = new ApiServiceAdapter(context.getApplicationContext());
        }
        return instance;
    }

    public static String getApiUrl(Context context) {

        String apiUrl = context.getResources().getString(R.string.api_url);
        int apiPort = context.getResources().getInteger(R.integer.api_port);
        return "http://" + apiUrl + ":" + apiPort;
    }

    private Realm realm = Realm.getDefaultInstance();
    private ApiService service;
    private Context context;

    private ApiServiceAdapter(Context context) {
        this.context = context;

        initializeRetrofit();
    }

    private void initializeRetrofit() {
        OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS);

        final User user = UserManager.getInstance(context).getMainUser(realm);

        if (user != null && user.hasToken()) {
            final String token = user.getServerToken();
            httpBuilder.addInterceptor(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    Request request = original.newBuilder()
                            .header("Authorization", "Bearer " + token)
                            .build();
                    return chain.proceed(request);
                }
            });
        }

        OkHttpClient client = httpBuilder.build();

        Gson gson = new GsonBuilder()
                .setLenient()
                .create();

        String url = getApiUrl(context);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();


        Log.d(TAG, url);
        service = retrofit.create(ApiService.class);
    }

    private void printResponse(String title, Response response) {
        if (response.isSuccessful()) {
            Log.d(TAG, title + " -> response(" + response.code() + "): " + response.body());
        } else {
            try {
                Log.e(TAG, title + " -> response(" + response.code() + "): " + response.errorBody().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void register(String email, String password, String checkPassword, String username) {

        Log.d("TrackingApiServicd", "start register process...");
        Call<JsonObject> call = service.register(email, password, checkPassword, username);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                printResponse("register", response);
                Intent localIntent = new Intent(BROADCAST_FINISH_REGISTER);

                if (response.isSuccessful()) {
                    localIntent.putExtra(BROADCAST_ARG_CODE, response.code());
                } else {
                    localIntent.putExtra(BROADCAST_ARG_CODE, response.code());
                }
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());

                Intent localIntent = new Intent(BROADCAST_FINISH_REGISTER);
                localIntent.putExtra(BROADCAST_ARG_CODE, BROADCAST_ERROR_CODE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
            }
        });
    }


    public void login(final String username, String password) {
        Call<JsonObject> call = service.login(username, password);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                printResponse("login", response);

                if (response.isSuccessful()) {
                    JsonObject body = response.body();
                    String id = body.get("id").getAsString();
                    String token = body.get("token").getAsString();


                    realm.beginTransaction();
                    User user = UserManager.getInstance(context).createMainUser(realm, id, username, token);

                    realm.commitTransaction();
                    initializeRetrofit();

                    updateFirebseToken(user);

                }

                Intent localIntent = new Intent(BROADCAST_FINISH_LOGIN);
                localIntent.putExtra(BROADCAST_ARG_CODE, response.code());
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());

                Intent localIntent = new Intent(BROADCAST_FINISH_LOGIN);
                localIntent.putExtra(BROADCAST_ARG_CODE, BROADCAST_ERROR_CODE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(localIntent);
            }
        });
    }

    public void updateFirebseToken(User user) {

        Call<JsonObject> call = service.updateUserFirebaseToken(user.getId(), user.getFirebaseToken());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                printResponse("updateFirebseToken", response);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());

            }
        });
    }

    public void createRoom(final Song song, final String singer1Id, final CreateRoomCallback callback) {

        Call<JsonObject> call = service.createRoom(song.getId(), singer1Id);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(final Call<JsonObject> call, final Response<JsonObject> response) {
                printResponse("createRoom", response);

                if (response.isSuccessful()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            String id = response.body().getAsJsonArray("ops").get(0)
                                    .getAsJsonObject().get("_id").getAsString();

                            VideoRoom room = realm.createObject(VideoRoom.class, id);
                            room.setSong(song);
                            room.setSinger1Id(singer1Id);
                            callback.onCreateRoomFinish(room);

                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());

            }
        });

    }

    public void requestJoinToRoom(final VideoRoom videoRoom, final RequestUser user, final RequestJoinCallback callback) {
        Call<JsonObject> call = service.requestJoin(videoRoom.getId(), user.getId());

        final String videoRoomId = videoRoom.getId();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                printResponse("requestJoinToRoom", response);

                if (response.isSuccessful()) {
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {

                            videoRoom.setSinger2Id(user.getId());
                        }
                    });

                    callback.onFinishRequest();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());

            }
        });
    }

    public void acceptRequest(VideoRoom videoRoom) {
        Call<JsonObject> call = service.acceptRequest(videoRoom.getId());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                printResponse("requestJoinToRoom", response);

            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "error: " + t.getMessage());

            }
        });
    }

    public void getAllUsers(final FinishGetFriendsCallback callback) {
        Call<JsonArray> call = service.getUsers();
        call.enqueue(new Callback<JsonArray>() {
                         @Override
                         public void onResponse(final Call<JsonArray> call, final Response<JsonArray> response) {
                             printResponse("getAllUsers", response);

                             if (response.isSuccessful()) {
                                 final JsonArray body = response.body();

                                 realm.executeTransactionAsync(new Realm.Transaction() {
                                     @Override
                                     public void execute(Realm realm) {
                                         final List<RequestUser> requestUsers = new ArrayList<>();
                                         for (int i = 0; i < body.size(); i++) {
                                             JsonObject object = body.get(i).getAsJsonObject();

                                             RequestUser requestUser = new RequestUser(object.get("_id").getAsString(),
                                                     object.get("username").getAsString(),
                                                     object.getAsJsonArray("emails").get(0).getAsJsonObject().get("address").getAsString());

                                             requestUsers.add(requestUser);
                                         }

                                         realm.copyToRealmOrUpdate(requestUsers);
                                     }
                                 }, new Realm.Transaction.OnSuccess() {
                                     @Override
                                     public void onSuccess() {
                                         callback.onFinishGetFriends();
                                     }
                                 }, new Realm.Transaction.OnError() {
                                     @Override
                                     public void onError(Throwable error) {
                                     }
                                 });
                             }

                         }

                         @Override
                         public void onFailure(Call<JsonArray> call, Throwable t) {
                             Log.e(TAG, "error: " + t.getMessage());

                         }
                     }

        );
    }
}
