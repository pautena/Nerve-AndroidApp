package com.pautena.hackupc.ui.main;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.pautena.hackupc.R;
import com.pautena.hackupc.ui.login.RegisterFragment;
import com.pautena.hackupc.ui.twillio.activity.VideoActivity;

public class MainActivity extends AppCompatActivity  implements MainFragment.MainFragmentCallback{

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        startMainFragment();
    }


    private void startMainFragment(){
        FragmentManager fm = getSupportFragmentManager();

        mainFragment = MainFragment.newInstance();

        fm.beginTransaction()
                .setCustomAnimations(R.anim.enter_anim, R.anim.exit_anim, R.anim.enter_anim, R.anim.exit_anim)
                .replace(R.id.container, mainFragment, RegisterFragment.TAG)
                .addToBackStack(RegisterFragment.TAG)
                .commit();
    }

    @Override
    public void onStartCall() {
        Intent intent = new Intent(this,VideoActivity.class);
        startActivity(intent);
    }
}
