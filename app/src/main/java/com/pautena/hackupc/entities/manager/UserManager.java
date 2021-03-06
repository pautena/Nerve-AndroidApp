package com.pautena.hackupc.entities.manager;

import android.content.Context;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.services.ApiServiceAdapter;

import io.realm.Realm;

/**
 * Created by pautenavidal on 2/3/17.
 */
public class UserManager {
    private static final String TAG = UserManager.class.getSimpleName();

    private static UserManager instance;
    private final Context context;

    public static UserManager getInstance(Context context) {

        if (instance == null) {
            instance = new UserManager(context.getApplicationContext());
        }
        return instance;
    }

    private UserManager(Context context) {
        this.context = context;
    }

    public User getMainUser(Realm realm) {
        User user = realm.where(User.class).findFirst();

        Log.d(TAG, "user: " + user);

        return user;
    }

    public void login(Realm realm, String username, String password) {
        User user = getMainUser(realm);

        if (user != null) {
            user.deleteFromRealm();
        }

        ApiServiceAdapter.getInstance(context).login(username, password);


    }

    public User createMainUser(Realm realm, String id, String username, String token) {
        User user = realm.createObject(User.class, id);
        user.setUsername(username);
        user.setServerToken(token);


        String firebaseToken = FirebaseInstanceId.getInstance().getToken();
        user.setFirebaseToken(firebaseToken);


        Log.d(TAG, "createMainUser. user: " + user);

        return user;
    }

    public void register(String email, String username, String password, String checkPassword) {
        ApiServiceAdapter.getInstance(context).register(email, username, password, checkPassword);
    }

    public void logout(Realm realm) {
        realm.delete(User.class);
    }
}
