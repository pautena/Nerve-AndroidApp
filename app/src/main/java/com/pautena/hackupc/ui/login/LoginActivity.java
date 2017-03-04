package com.pautena.hackupc.ui.login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;

import com.pautena.hackupc.R;
import com.pautena.hackupc.entities.manager.UserManager;
import com.pautena.hackupc.services.ApiServiceAdapter;
import com.pautena.hackupc.ui.twillio.activity.VideoActivity;

import io.realm.Realm;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements LoginFragment.LoginCallback,
        RegisterFragment.RegisterCallback {
    private Realm realm;

    private LoginFragment loginFragment;
    private RegisterFragment registerFragment;

    private BroadcastReceiver loginReceiver = new BroadcastReceiver() {
        private int code;

        @Override
        public void onReceive(Context context, Intent intent) {
            code = intent.getExtras().getInt(ApiServiceAdapter.BROADCAST_ARG_CODE);

            if (isSuccess()) {
                goToMainActivity();
                finish();
            } else {
                loginFragment.setError(getString(R.string.error_incorrect_password));
            }
        }

        private boolean isSuccess() {
            return code >= 200 && code < 300;
        }
    };

    private BroadcastReceiver registerReceiver = new BroadcastReceiver() {
        private int code;

        @Override
        public void onReceive(Context context, Intent intent) {
            code = intent.getExtras().getInt(ApiServiceAdapter.BROADCAST_ARG_CODE);

            if (isSuccess()) {
                onClickLogin();
            } else {
                registerFragment.setError(getResources().getString(R.string.error_register));
            }
        }

        private boolean isSuccess() {
            return code >= 200 && code < 300;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        realm = Realm.getDefaultInstance();

        loginFragment = LoginFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, loginFragment, LoginFragment.TAG)
                .commit();

        registerReceivers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
        unregisterReceivers();
    }

    private void registerReceivers() {
        IntentFilter intentFilter = new IntentFilter(ApiServiceAdapter.BROADCAST_FINISH_LOGIN);
        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, intentFilter);

        intentFilter = new IntentFilter(ApiServiceAdapter.BROADCAST_FINISH_REGISTER);
        LocalBroadcastManager.getInstance(this).registerReceiver(registerReceiver, intentFilter);

    }

    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(registerReceiver);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, VideoActivity.class);
        startActivity(intent);
    }


    @Override
    public void onClickRegister() {
        registerFragment = RegisterFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, registerFragment, RegisterFragment.TAG)
                .addToBackStack(RegisterFragment.TAG)
                .commit();
    }

    @Override
    public void onLogin(String email, String password) {

        UserManager.getInstance(this).login(realm,email, password);
    }

    @Override
    public void onClickLogin() {
        loginFragment = LoginFragment.newInstance().newInstance();
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, loginFragment, LoginFragment.TAG)
                .addToBackStack(LoginFragment.TAG)
                .commit();
    }

    @Override
    public void onRegister(String email, String username, String password, String checkPassword) {
        UserManager.getInstance(this).register(email, username, password, checkPassword);
    }
}

