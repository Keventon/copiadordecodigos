package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.ActivityEditarContaBinding;
import br.com.appco.copiadordecodigos.model.Boleto;
import br.com.appco.copiadordecodigos.model.Conta;

public class EditarContaActivity extends AppCompatActivity {

    ActivityEditarContaBinding binding;
    private ContaDAO contaDAO;
    private Boleto contaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageVoltarEditarConta.setOnClickListener(view ->  {
            finish();
        });

        contaAtual = (Boleto) getIntent().getSerializableExtra("contaSelecionada");

        if (contaAtual != null) {
            Double contaConvertida = contaAtual.getValor() * 10;
            Double contaConvertidaMulta = contaAtual.getValorMulta() * 10;

            binding.editNomeEmpresaContaEditar.setText(contaAtual.getNomeEmpresa());
            binding.editValorContaEditar.setText(String.valueOf(contaConvertida));
            binding.editValorMulta.setText(String.valueOf(contaConvertidaMulta));
            binding.editDataValidadeEditar.setText(contaAtual.getDataValidade());
            binding.editCodigoBarraEditar.setText(contaAtual.getCodigo());

            if (contaAtual.getCodigo().equals("null")) {
                binding.editCodigoBarraEditar.setText("");
                binding.editCodigoBarraEditar.setHint("");
                binding.editCodigoBarraEditar.setEnabled(false);
            }
        }

        binding.buttonConfirmarEdicao.setOnClickListener(view ->  {
            if (!binding.editCodigoBarraEditar.getText().toString().isEmpty()){
                if (binding.editDataValidadeEditar.length() == 10) {
                    if (!binding.editNomeEmpresaContaEditar.getText().toString().isEmpty()) {
                        double valor = (double) binding.editValorContaEditar.getRawValue() / 100;
                        double valorMulta = (double) binding.editValorMulta.getRawValue() / 100;
                        String data = binding.editDataValidadeEditar.getText().toString();
                        String codigo = binding.editCodigoBarraEditar.getText().toString();
                        String descricao = binding.editNomeEmpresaContaEditar.getText().toString();

                        Boleto boleto = new Boleto();
                        boleto.setCodigo(codigo);
                        boleto.setDataPagamento("");
                        boleto.setNomeEmpresa(descricao);
                        boleto.setStatus(0);
                        boleto.setNomeFarmacia(contaAtual.getNomeFarmacia());
                        boleto.setValor(valor);
                        boleto.setValorMulta(valorMulta);
                        boleto.setDataValidade(data);
                        boleto.setId(contaAtual.getId());
                        boleto.atualizar(((error, ref) -> {
                            Toast.makeText(this, "Boleto editado com sucesso", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ContasActivity.class));
                        }));


                    }else {
                        Toast.makeText(this, "Você não colocou o nome da empresa para sua conta", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "Você não definiu uma data", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}