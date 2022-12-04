package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityEscolherBoletoBinding;

public class EscolherBoletoActivity extends AppCompatActivity {

    private ActivityEscolherBoletoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEscolherBoletoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}