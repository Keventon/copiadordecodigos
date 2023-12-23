package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityCadastroBinding;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageVoltar.setOnClickListener(view -> finish());
    }
}