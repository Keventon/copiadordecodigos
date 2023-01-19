package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityAbrirImagemComprovanteBinding;

public class AbrirImagemComprovanteActivity extends AppCompatActivity {

    private ActivityAbrirImagemComprovanteBinding binding;
    private String imagemRecebida;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAbrirImagemComprovanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            imagemRecebida = (String) bundle.getSerializable("imagemComprovante");

            Picasso.get()
                    .load(imagemRecebida)
                    .into(binding.imageFotoComprovante);


        }

        binding.imageView2.setOnClickListener(view -> finish());
    }

    private Bitmap getBitmap(Uri caminhoUri){
        Bitmap bitmap = null;

        try {
            if (Build.VERSION.SDK_INT < 28) {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), caminhoUri);
            }else {
                ImageDecoder.Source source = ImageDecoder.createSource(getContentResolver(), caminhoUri);
                bitmap = ImageDecoder.decodeBitmap(source);
            }
        }catch (Exception e) {

        }

        return bitmap;

    }
}