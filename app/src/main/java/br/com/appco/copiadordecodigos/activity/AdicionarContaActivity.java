package br.com.appco.copiadordecodigos.activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.normal.TedPermission;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

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

        binding.textScanearBoleto.setOnClickListener(view -> scanearBoleto());

        binding.floatingAdicionarConta.setOnClickListener(view -> {
            if (Util.checarConexaoDispositivo(AdicionarContaActivity.this)) {
                if (!binding.editCodigoBarra.getText().toString().isEmpty()) {
                    if (!binding.editNomeEmpresa.getText().toString().isEmpty()) {
                        if (binding.editDataValidade.length() == 10) {
                            double valor = (double) binding.editValorConta.getRawValue() / 100;
                            if (valor > 0) {
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
                                            boleto.setValorMulta(0.0);
                                            boleto.salvar(((error, ref) -> {
                                                binding.progressBarAddConta.setVisibility(View.GONE);
                                                Toast.makeText(getApplicationContext(), "Conta adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                                finish();
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

    private void scanearBoleto() {

        IntentIntegrator integrator = new IntentIntegrator(AdicionarContaActivity.this);
        integrator.setCaptureActivity(CaptureAct.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Clique no botão de aumentar volume para ligar o flash");
        integrator.initiateScan();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {
            if (result.getContents() != null) {
                binding.editCodigoBarra.setText(calculaLinha(result.getContents()));
            }else {
                AlertDialog.Builder builder = new AlertDialog.Builder(AdicionarContaActivity.this);
                builder.setTitle("Erro na leitura do código de barras");
                builder.setMessage("Deseja tentar novamente?");
                builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        scanearBoleto();
                    }
                });

                builder.setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
            }
        }
    }

    public static String calculaLinha(String barra) {
        // Remover caracteres não numéricos.
        String linha = barra.replaceAll("[^0-9]", "");

        if (linha.length() != 44) {
            return null; // 'A linha do Código de Barras está incompleta!'
        }

        String campo1 = linha.substring(0,4)+linha.substring(19,20)+'.'+linha.substring(20,24);
        String campo2 = linha.substring(24,29)+'.'+linha.substring(29,34);
        String campo3 = linha.substring(34,39)+'.'+linha.substring(39,44);
        String campo4 = linha.substring(4,5); // Digito verificador
        String campo5 = linha.substring(5,19); // Vencimento + Valor

        if (  modulo11Banco(  linha.substring(0,4)+linha.substring(5,44)  ) != Integer.valueOf(campo4) ) {
            return null; //'Digito verificador '+campo4+', o correto é '+modulo11_banco(  linha.substr(0,4)+linha.substr(5,99)  )+'\nO sistema não altera automaticamente o dígito correto na quinta casa!'
        }
        return   campo1 + modulo10(campo1)
                +' '
                +campo2 + modulo10(campo2)
                +' '
                +campo3 + modulo10(campo3)
                +' '
                +campo4
                +' '
                +campo5
                ;
    }

    public static int modulo10(String numero) {
        numero = numero.replaceAll("[^0-9]","");
        int soma  = 0;
        int peso  = 2;
        int contador = numero.length()-1;
        while (contador >= 0) {
            int multiplicacao = Integer.valueOf( numero.substring(contador,contador+1) ) * peso;
            if (multiplicacao >= 10) {multiplicacao = 1 + (multiplicacao-10);}
            soma = soma + multiplicacao;
            if (peso == 2) {
                peso = 1;
            } else {
                peso = 2;
            }
            contador = contador - 1;
        }
        int digito = 10 - (soma % 10);
        if (digito == 10) digito = 0;

        return digito;
    }

    public static int modulo11Banco(String numero) {
        numero = numero.replaceAll("[^0-9]","");

        int soma  = 0;
        int peso  = 2;
        int base  = 9;
        int contador = numero.length() - 1;
        for (int i=contador; i >= 0; i--) {
            soma = soma + ( Integer.valueOf(numero.substring(i,i+1)) * peso);
            if (peso < base) {
                peso++;
            } else {
                peso = 2;
            }
        }
        int digito = 11 - (soma % 11);
        if (digito >  9) digito = 0;
        /* Utilizar o dígito 1(um) sempre que o resultado do cálculo padrão for igual a 0(zero), 1(um) ou 10(dez). */
        if (digito == 0) digito = 1;
        return digito;
    }
}