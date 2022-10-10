package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.ActivityEditarContaBinding;
import br.com.appco.copiadordecodigos.model.Conta;

public class EditarContaActivity extends AppCompatActivity {

    ActivityEditarContaBinding binding;
    private ContaDAO contaDAO;
    private Conta contaAtual;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditarContaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageVoltarEditarConta.setOnClickListener(view ->  {
            finish();
        });

        contaAtual = (Conta) getIntent().getSerializableExtra("contaSelecionada");

        if (contaAtual != null) {
            Double contaConvertida = contaAtual.getValor() * 10;

            binding.editDescricaoContaEditar.setText(contaAtual.getDescricao());
            binding.editValorContaEditar.setText(String.valueOf(contaConvertida));
            binding.editDataValidadeEditar.setText(contaAtual.getDataValidade());

            if (contaAtual.getCodigo().equals("null")) {
                binding.editCodigoBarraEditar.setText("");
                binding.editCodigoBarraEditar.setHint("");
                binding.editCodigoBarraEditar.setEnabled(false);
            }
        }

        binding.buttonConfirmarEdicao.setOnClickListener(view ->  {
            if (binding.editCodigoBarraEditar.length() == 51) {
                if (binding.editDataValidadeEditar.length() == 10) {
                    if (!binding.editDescricaoContaEditar.getText().toString().isEmpty()) {
                        double valor = (double) binding.editValorContaEditar.getRawValue() / 100;
                        String data = binding.editDataValidadeEditar.getText().toString();
                        String codigo = binding.editCodigoBarraEditar.getText().toString();
                        String descricao = binding.editDescricaoContaEditar.getText().toString();

                        contaDAO = new ContaDAO(this);

                        Conta conta = new Conta();
                        conta.setCodigo(codigo);
                        conta.setDescricao(descricao);
                        conta.setId(contaAtual.getId());
                        conta.setDataValidade(data);
                        conta.setStatus(0);
                        conta.setValor(valor);
                        conta.setDataPagamento("null");

                        if (contaDAO.atualizar(conta)) {
                            Toast.makeText(this, "Conta atualizada com sucesso", Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            Toast.makeText(this, "Erro ao atualizar conta", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(this, "Você não colocou uma descrição para sua conta", Toast.LENGTH_SHORT).show();
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