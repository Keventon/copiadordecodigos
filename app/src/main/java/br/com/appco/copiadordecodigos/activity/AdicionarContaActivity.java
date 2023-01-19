package br.com.appco.copiadordecodigos.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Instrumentation;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ImageDecoder;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;

import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.database.SQLiteHelper;
import br.com.appco.copiadordecodigos.databinding.ActivityAdicionarContaBinding;
import br.com.appco.copiadordecodigos.model.Boleto;
import br.com.appco.copiadordecodigos.model.Conta;
import br.com.appco.copiadordecodigos.util.Util;

public class AdicionarContaActivity extends AppCompatActivity {

    ActivityAdicionarContaBinding binding;
    Uri imagemSelecionada;
    //private ContaDAO contaDAO;

    DatabaseReference reference = ConfiguracoesFirebase.getFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdicionarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBarAddConta.setVisibility(View.GONE);

        SQLiteHelper db = new SQLiteHelper(this);

        binding.imageVoltarAdicionarConta.setOnClickListener(view ->  {
            finish();
        });

        binding.floatingAdicionarConta.setOnClickListener(view -> {
            if (Util.checarConexaoDispositivo(AdicionarContaActivity.this)) {
                if (!binding.editCodigoBarra.getText().toString().isEmpty()) {
                    if (!binding.editNomeEmpresa.getText().toString().isEmpty()) {
                        if (binding.editDataValidade.length() == 10) {
                            double valor = (double) binding.editValorConta.getRawValue() / 100;
                            if (valor > 0) {
                                double valorMulta = (double) binding.editValorMulta.getRawValue() / 100;
                                if(valorMulta > 0) {
                                    if(!binding.editNomeEmpresa.getText().toString().isEmpty()) {
                                        String data = binding.editDataValidade.getText().toString();
                                        String mesAno = data.substring(3);

                                        binding.progressBarAddConta.setVisibility(View.VISIBLE);
                                        DatabaseReference npmeFarmaciaRef = reference
                                                .child("usuario")
                                                .child(UsuarioFirebase.getIdentificadorUsuario())
                                                .child("nomeFarmacia");

                                        npmeFarmaciaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                String nomeFarmacia = snapshot.getValue().toString();

                                                Boleto boleto = new Boleto();
                                                boleto.setCodigo(binding.editCodigoBarra.getText().toString());
                                                boleto.setDataValidade(data);
                                                boleto.setStatus(0);
                                                boleto.setMes(mesAno);
                                                boleto.setDataPagamento("");
                                                boleto.setValor(valor);
                                                boleto.setImagemComprovante("");
                                                boleto.setNomeEmpresa(binding.editNomeEmpresa.getText().toString().trim());
                                                boleto.setNomeFarmacia(nomeFarmacia);
                                                boleto.setValorMulta(valorMulta);
                                                boleto.salvar(((error, ref) -> {
                                                    binding.progressBarAddConta.setVisibility(View.GONE);
                                                    Toast.makeText(getApplicationContext(), "Conta adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                                    startActivity(new Intent(getApplicationContext(), ContasActivity.class));
                                                }));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });

                                    }else {
                                        binding.progressBarAddConta.setVisibility(View.GONE);
                                        binding.editNomeEmpresa.requestFocus();
                                        binding.editNomeEmpresa.setText("Coloque o nome da Empresa");
                                    }
                                }else {
                                    binding.progressBarAddConta.setVisibility(View.GONE);
                                    binding.editValorMulta.requestFocus();
                                    binding.editValorMulta.setError("Coloque o valor de multa após o atraso do boleto");
                                }
                            }else {
                                binding.progressBarAddConta.setVisibility(View.GONE);
                                binding.editValorConta.requestFocus();
                                binding.editValorConta.setError("Defina o valor da seu boleto");
                            }
                        }else {
                            binding.progressBarAddConta.setVisibility(View.GONE);
                            binding.editDataValidade.requestFocus();
                            binding.editDataValidade.setError("Coloque a data de validade");
                        }
                    }else{
                        binding.progressBarAddConta.setVisibility(View.GONE);
                        binding.editNomeEmpresa.requestFocus();
                        binding.editNomeEmpresa.setError("Coloque o nome da empresa");
                    }
                }else {
                    binding.progressBarAddConta.setVisibility(View.GONE);
                    binding.editCodigoBarra.requestFocus();
                    binding.editCodigoBarra.setError("Adicione os números do boleto");
                }
            }else {
                binding.progressBarAddConta.setVisibility(View.GONE);
                Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
            }
        });
    }
}