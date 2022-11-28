package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.firebase.ktx.Firebase;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.ContasActivity;
import br.com.appco.copiadordecodigos.util.FirebaseHelper;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(this::abrirAutenticacao, 2000);

    }

    private void abrirAutenticacao() {
        if (!FirebaseHelper.getAutenticado()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else {
            startActivity(new Intent(MainActivity.this, ContasActivity.class));
        }
    }
}