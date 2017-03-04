package com.pautena.hackupc.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.User;
import com.pautena.hackupc.entities.manager.UserManager;
import com.pautena.hackupc.ui.login.LoginActivity;
import com.pautena.hackupc.ui.twillio.activity.VideoActivity;
import com.pautena.hackupc.utils.SongLoader;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class SplashScreen extends AppCompatActivity {
    private final static String TAG = SplashScreen.class.getSimpleName();
    private static final int MY_PERMISSIONS_REQUEST = 1;

    static{ System.loadLibrary("opencv_java3"); }

    private Realm realm;
    private User user;

    private int runDelay;
    private int runSmallDelay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        setupRealm();

        realm = Realm.getDefaultInstance();
        runDelay = getResources().getInteger(R.integer.splash_screen_delay);
        runSmallDelay = getResources().getInteger(R.integer.splash_screen_small_delay);

        new SongLoader().load(realm);
        FirebaseApp.initializeApp(this);
        user = UserManager.getInstance(this).getMainUser(realm);


        if (!requestPermissions()) {
            nextActivity(runDelay);
        }
    }

    private void setupRealm() {
        String realmName = getResources().getString(R.string.realm_name);
        int realmVersion = getResources().getInteger(R.integer.realm_version);

        //Configure realm
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(realmName)
                .schemaVersion(realmVersion)
                //.migration(new MyRealmMigration())
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        nextActivity(runSmallDelay);
    }

    private boolean requestPermissions() {

        List<String> permisions = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.GET_ACCOUNTS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.READ_CONTACTS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_BOOT_COMPLETED) != PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permisions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permisions.isEmpty()) {

            String[] permissionsArray = permisions.toArray(new String[permisions.size()]);

            ActivityCompat.requestPermissions(this,
                    permissionsArray,
                    MY_PERMISSIONS_REQUEST);
            return true;
        }

        return false;
    }

    private void nextActivity(int delay) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                if (user != null) {
                    intent = new Intent(SplashScreen.this, VideoActivity.class);
                } else {
                    intent = new Intent(SplashScreen.this, LoginActivity.class);
                }
                startActivity(intent);
                finish();
            }
        }, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
