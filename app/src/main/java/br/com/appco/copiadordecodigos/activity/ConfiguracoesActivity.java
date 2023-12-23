package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityConfiguracoesBinding;

public class ConfiguracoesActivity extends AppCompatActivity {

    private ActivityConfiguracoesBinding binding;
    private FirebaseAuth auth = ConfiguracoesFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConfiguracoesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageVoltar.setOnClickListener(view -> finish());

        binding.cardSair.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(ConfiguracoesActivity.this, LoginActivity.class));
        });

    }
}