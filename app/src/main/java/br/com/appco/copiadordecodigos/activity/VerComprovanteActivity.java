package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ActivityVerComprovanteBinding;

public class VerComprovanteActivity extends AppCompatActivity {

    private ActivityVerComprovanteBinding binding;

    private String imagemRecebida;
    private Bitmap bitmap;
    private BitmapDrawable bitmapDrawable;

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

        binding.imageFotoComprovante.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    VerComprovanteActivity.this, R.style.BottomSheetTheme
            );

            View bottomSheetView = LayoutInflater.from(VerComprovanteActivity.this)
                    .inflate(
                            R.layout.dialog_layout_fazer_download,
                            (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                    );

            Button buttonSim = bottomSheetView.findViewById(R.id.buttonSimDownload);
            Button buttonNao = bottomSheetView.findViewById(R.id.buttonNaoDownload);

            buttonNao.setOnClickListener(view1 -> bottomSheetDialog.dismiss());

            buttonSim.setOnClickListener(view1 -> {
                bitmapDrawable = (BitmapDrawable) binding.imageFotoComprovante.getDrawable();
                bitmap = bitmapDrawable.getBitmap();

                FileOutputStream fileOutputStream = null;

                File sdCard = Environment.getExternalStorageDirectory();
                File directory = new File(sdCard.getAbsoluteFile() + "/Download");
                directory.mkdir();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());

                File outfile = new File(directory, fileName);

                Toast.makeText(this, "Imagem salva com sucesso", Toast.LENGTH_SHORT).show();

                try {
                    fileOutputStream = new FileOutputStream(outfile);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                    Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    intent.setData(Uri.fromFile(outfile));
                    sendBroadcast(intent);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            });



            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });

        binding.imageView2.setOnClickListener(view -> finish());
    }
}