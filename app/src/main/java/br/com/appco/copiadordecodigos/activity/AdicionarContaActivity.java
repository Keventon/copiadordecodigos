package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.database.SQLiteHelper;
import br.com.appco.copiadordecodigos.databinding.ActivityAdicionarContaBinding;
import br.com.appco.copiadordecodigos.model.Boleto;
import br.com.appco.copiadordecodigos.model.Conta;

public class AdicionarContaActivity extends AppCompatActivity {

    ActivityAdicionarContaBinding binding;
    private ContaDAO contaDAO;

    DatabaseReference reference = ConfiguracoesFirebase.getFirebase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdicionarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SQLiteHelper db = new SQLiteHelper(this);

        binding.imageVoltarAdicionarConta.setOnClickListener(view ->  {
            finish();
        });

        binding.floatingAdicionarConta.setOnClickListener(view -> {
            if (!binding.editCodigoBarra.getText().toString().isEmpty() && binding.editCodigoBarra.length() == 51) {
                if (!binding.editNomeEmpresa.getText().toString().isEmpty()) {
                    if (binding.editDataValidade.length() == 10) {
                        if (!binding.editDescricaoConta.getText().toString().isEmpty()) {
                            double valor = (double) binding.editValorConta.getRawValue() / 100;
                            if (valor > 0) {
                                double valorMulta = (double) binding.editValorMulta.getRawValue() / 100;
                                if(valorMulta > 0) {
                                    if(!binding.editNomeEmpresa.getText().toString().isEmpty()) {
                                        String data = binding.editDataValidade.getText().toString();
                                        String descricao = binding.editDescricaoConta.getText().toString().trim();

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
                                                boleto.setDescricao(descricao);
                                                boleto.setStatus(0);
                                                boleto.setDataPagamento("");
                                                boleto.setValor(valor);
                                                boleto.setNomeEmpresa(binding.editNomeEmpresa.getText().toString().trim());
                                                boleto.setNomeFarmacia(nomeFarmacia);
                                                boleto.setValorMulta(valorMulta);
                                                boleto.salvar();
                                                Toast.makeText(getApplicationContext(), "Conta adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(getApplicationContext(), ContasActivity.class));
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                    }else {
                                        binding.editNomeEmpresa.requestFocus();
                                        binding.editNomeEmpresa.setText("Coloque o nome da Empresa");
                                    }
                                }else {
                                    binding.editValorMulta.requestFocus();
                                    binding.editValorMulta.setError("Coloque o valor de multa após o atraso do boleto");
                                }
                            }else {
                                binding.editValorConta.requestFocus();
                                binding.editValorConta.setError("Defina o valor da seu boleto");
                            }
                        }else {
                            binding.editDescricaoConta.requestFocus();
                            binding.editDescricaoConta.setError("Coloque uma descrição");
                        }
                    }else {
                        binding.editDataValidade.requestFocus();
                        binding.editDataValidade.setError("Coloque a data de validade");
                    }
                }else{
                    binding.editNomeEmpresa.requestFocus();
                    binding.editNomeEmpresa.setError("Coloque o nome da empresa");
                }
            }else {
                binding.editCodigoBarra.requestFocus();
                binding.editCodigoBarra.setError("Adicione os números do boleto");
            }
        });
    }
}