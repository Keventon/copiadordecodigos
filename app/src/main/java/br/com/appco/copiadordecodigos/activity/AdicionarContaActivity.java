package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.database.SQLiteHelper;
import br.com.appco.copiadordecodigos.databinding.ActivityAdicionarContaBinding;
import br.com.appco.copiadordecodigos.databinding.ActivityContasBinding;
import br.com.appco.copiadordecodigos.model.Conta;

public class AdicionarContaActivity extends AppCompatActivity {

    ActivityAdicionarContaBinding binding;
    private ContaDAO contaDAO;

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
            if (binding.editCodigoBarra.length() == 51) {
                if (binding.editDataValidade.length() == 10) {
                    if (!binding.editDescricaoConta.getText().toString().isEmpty()) {
                        double valor = (double) binding.editValorConta.getRawValue() / 100;
                        String data = binding.editDataValidade.getText().toString();
                        String codigo = binding.editCodigoBarra.getText().toString();
                        String descricao = binding.editDescricaoConta.getText().toString();

                        contaDAO = new ContaDAO(this);

                        Conta conta = new Conta();
                        conta.setCodigo(codigo);
                        conta.setDescricao(descricao);
                        conta.setDataValidade(data);
                        conta.setValor(valor);
                        conta.setDataPagamento("null");

                        contaDAO.adicionarConta(conta);
                        Toast.makeText(this, "Conta adicionada com sucesso", Toast.LENGTH_SHORT).show();
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