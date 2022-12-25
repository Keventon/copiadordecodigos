package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.ktx.Firebase;

import java.util.Base64;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.ContasActivity;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;
import br.com.appco.copiadordecodigos.util.FirebaseHelper;
import br.com.appco.copiadordecodigos.util.Util;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth = ConfiguracoesFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Handler().postDelayed(this::abrirAutenticacao, 2000);

        //auth.signOut();


    }

    private void abrirAutenticacao() {

        if (!FirebaseHelper.getAutenticado()) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }else {
            startActivity(new Intent(MainActivity.this, ContasActivity.class));
        }
    }
}