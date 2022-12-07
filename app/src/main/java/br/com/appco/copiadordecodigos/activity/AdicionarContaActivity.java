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
            if (binding.checkAdicionarConta.isChecked()) {
                if (binding.editDataValidade.length() == 10) {
                    if (!binding.editDescricaoConta.getText().toString().isEmpty()) {
                        double valor = (double) binding.editValorConta.getRawValue() / 100;
                        if (valor > 0) {
                            String data = binding.editDataValidade.getText().toString();
                            String descricao = binding.editDescricaoConta.getText().toString();

                            DatabaseReference npmeFarmaciaRef = reference
                                    .child("usuario")
                                    .child(UsuarioFirebase.getIdentificadorUsuario())
                                    .child("nomeFarmacia");

                            npmeFarmaciaRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    String nomeFarmacia = snapshot.getValue().toString();
                                    Boleto boleto = new Boleto();
                                    boleto.setCodigo("null");
                                    boleto.setDataPagamento(data);
                                    boleto.setDescricao(descricao);
                                    boleto.setStatus(0);
                                    boleto.setDataPagamento("");
                                    boleto.setValor(valor);
                                    boleto.setNomeFarmacia(nomeFarmacia);
                                    //boleto.setValorMulta();
                                    Toast.makeText(getApplicationContext(), "Conta adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                    finish();
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }else {
                            Toast.makeText(this, "Defina o valor da seu boleto", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "Você não colocou uma descrição para seu boleto", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Você não definiu uma data", Toast.LENGTH_SHORT).show();
                }
            }else {
                if (binding.editCodigoBarra.length() == 51) {
                    if (binding.editDataValidade.length() == 10) {
                        if (!binding.editDescricaoConta.getText().toString().isEmpty()) {
                            double valor = (double) binding.editValorConta.getRawValue() / 100;
                            if (valor > 0) {
                                String data = binding.editDataValidade.getText().toString();
                                String codigo = binding.editCodigoBarra.getText().toString();
                                String descricao = binding.editDescricaoConta.getText().toString();

                                contaDAO = new ContaDAO(this);

                                Conta conta = new Conta();
                                conta.setCodigo(codigo);
                                conta.setDescricao(descricao);
                                conta.setDataValidade(data);
                                conta.setStatus(0);
                                conta.setValor(valor);
                                conta.setDataPagamento("null");

                                contaDAO.salvar(conta);
                                Toast.makeText(this, "Boleto adicionada com sucesso", Toast.LENGTH_SHORT).show();
                                finish();
                            }else {
                                Toast.makeText(this, "Defina o valor da seu boleto", Toast.LENGTH_SHORT).show();
                            }
                        }else {
                            Toast.makeText(this, "Você não colocou uma descrição para seu boleto", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "Você não definiu uma data", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}