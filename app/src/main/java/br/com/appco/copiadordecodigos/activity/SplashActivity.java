package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;

import br.com.appco.copiadordecodigos.R;

public class SplashActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
    }
}