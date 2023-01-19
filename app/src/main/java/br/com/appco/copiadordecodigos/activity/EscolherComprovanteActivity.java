package br.com.appco.copiadordecodigos.activity;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityEscolherComprovanteBinding;
import br.com.appco.copiadordecodigos.model.Boleto;

public class EscolherComprovanteActivity extends AppCompatActivity {

    public Uri imagemSelecionada;
    private ActivityEscolherComprovanteBinding binding;
    private String currentPhotoPath;
    private Boleto boleto;
    int resultCode = 0;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEscolherComprovanteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            boleto = (Boleto) bundle.getSerializable("info_boleto");


        }

        binding.imageView3.setOnClickListener(view -> finish());

        binding.buttonAbrirCamera.setOnClickListener(view -> verificarPermissaoCamera());
        binding.buttonAbrirGaleria.setOnClickListener(view -> verificarPermissaoGaleria());
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();

        return  image;
    }

    private void verificarPermissaoCamera() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirCamera();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Permissão Negada", Toast.LENGTH_SHORT).show();
            }


        };

        showDialogPermissao(
                permissionlistener,
                new String[]{Manifest.permission.CAMERA},
                "Se você não aceitar a permissão não poderá acessar a Câmera do dispositivo, deseja ativar a permissão agora?"
        );
    }

    private void verificarPermissaoGaleria() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                abrirGaleria();
            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "Permissão Negada", Toast.LENGTH_SHORT).show();
            }


        };

        showDialogPermissao(
                permissionlistener,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                "Se você não aceitar a permissão não poderá acessar a Galeria do dispositivo, deseja ativar a permissão agora?"
        );

    }

    private void showDialogPermissao(PermissionListener permissionListener, String[] permissoes, String mensagem) {
        TedPermission.create()
                .setPermissionListener(permissionListener)
                .setDeniedTitle("Permissão negada")
                .setDeniedMessage(mensagem)
                .setDeniedCloseButtonText("Não")
                .setGotoSettingButtonText("Sim")
                .setPermissions(permissoes)
                .check();
    }

    private void abrirCamera() {
        resultCode = 0;
        switch (resultCode) {
            case 0:
                resultCode = 2;
                break;

        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this,
                    "br.com.appco.copiadordecodigos.fileprovider",
                    photoFile);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            resultLauncher.launch(takePictureIntent);
        }
    }

    private void abrirGaleria() {
        resultCode = 1;
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        resultLauncher.launch(intent);
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

    private void salvarImagemFirebase(Uri caminhoImagem, Boleto boleto) {

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog_salvando_comprovante);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        StorageReference storageReference = ConfiguracoesFirebase.getFirebaseStorage()
                .child("imagens")
                .child("comprovantes")
                .child(boleto.getId())
                .child("imagem.jpeg");

        String imageName = System.currentTimeMillis() + "." + "jpeg";
        storageReference.child(imageName).putFile(caminhoImagem).addOnCompleteListener(task -> {
            task.getResult().getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                String imageUrlUploaded = uri.toString();
                Boleto boleto1 = new Boleto();
                boleto1.setImagemComprovante(imageUrlUploaded);
                boleto1.setId(boleto.getId());
                boleto1.setNomeFarmacia(boleto.getNomeFarmacia());
                boleto1.salvarImagem(boleto.getId(), boleto.getNomeFarmacia(), imageUrlUploaded, (error, ref) -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Imagem adicionada com sucesso", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EscolherComprovanteActivity.this, ContasActivity.class));
                });
            });
        });

    }

    private void salvarBoletoComrovante(Boleto boleto){


    }

    private final ActivityResultLauncher<Intent> resultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    binding.buttonEscolherComprovante.setVisibility(View.VISIBLE);
                    String caminhoImagem;

                    if (resultCode == 1) {//Galeria
                        imagemSelecionada = result.getData().getData();
                        binding.imageComprovante.setImageBitmap(getBitmap(imagemSelecionada));
                        caminhoImagem = imagemSelecionada.toString();

                        binding.cardComprovante.setOnClickListener(view -> {
                            Intent intent = new Intent(getApplicationContext(), AbrirImagemComprovanteActivity.class);
                            intent.putExtra("imagemComprovante", caminhoImagem);
                            startActivity(intent);
                        });
                    }else {//Camera
                        File file = new File(currentPhotoPath);
                        caminhoImagem = String.valueOf(file.toURI());

                        binding.imageComprovante.setImageURI(Uri.fromFile(file));
                        binding.cardComprovante.setOnClickListener(view -> {
                            Intent intent = new Intent(getApplicationContext(), AbrirImagemComprovanteActivity.class);
                            intent.putExtra("imagemComprovante", caminhoImagem);
                            startActivity(intent);
                        });

                    }

                    binding.buttonEscolherComprovante.setOnClickListener(view -> {

                        if (boleto != null) {
                            salvarImagemFirebase(imagemSelecionada, boleto);
                        }else {
                            Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
    );
}