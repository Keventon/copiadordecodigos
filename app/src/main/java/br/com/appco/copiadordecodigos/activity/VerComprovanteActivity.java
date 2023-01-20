package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.squareup.picasso.Picasso;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityVerComprovanteBinding;

public class VerComprovanteActivity extends AppCompatActivity {

    private ActivityVerComprovanteBinding binding;

    private String imagemRecebida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerComprovanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imagemRecebida = (String) bundle.getSerializable("url_comprovante");

            Picasso.get()
                    .load(imagemRecebida)
                    .into(binding.imageFotoComprovante);


        }

        binding.imageView2.setOnClickListener(view -> finish());
    }
}