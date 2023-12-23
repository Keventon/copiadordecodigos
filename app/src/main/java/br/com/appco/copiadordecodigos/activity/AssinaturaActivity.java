package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityAssinaturaBinding;

public class AssinaturaActivity extends AppCompatActivity {

    private ActivityAssinaturaBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAssinaturaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.buttonBackground.setOnClickListener(view -> entrarEmContatoWhatsApp());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    private void entrarEmContatoWhatsApp() {
        String numeroWhatsApp = "5591984828219";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(
                "http://api.whatsapp.com/send?phone="
                        + numeroWhatsApp + "&text=" + "Olá, quero pagar minha assinatura."
        ));
        startActivity(intent);
    }
}