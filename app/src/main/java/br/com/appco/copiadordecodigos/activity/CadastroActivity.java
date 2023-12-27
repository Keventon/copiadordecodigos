package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.components.ButtonMain;
import br.com.appco.copiadordecodigos.databinding.ActivityCadastroBinding;
import br.com.appco.copiadordecodigos.model.Loja;

public class CadastroActivity extends AppCompatActivity {

    private ActivityCadastroBinding binding;
    private ButtonMain buttonBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonBackground = binding.buttonCadastro;
        binding.imageVoltar.setOnClickListener(view -> finish());

        binding.buttonCadastro.binding.constrainLayout.setOnClickListener(view -> recuperarDados());

    }

    private void recuperarDados() {
        if (!binding.editEndereco.getText().toString().equals("")) {
            if (!binding.editNumEndereco.getText().toString().equals("")) {
                if (!binding.editPontoReferencia.getText().toString().equals("")) {
                    Loja loja = new Loja();
                    loja.setEndereco(binding.editEndereco.getText().toString().trim());
                    loja.setNumEndereco(binding.editNumEndereco.getText().toString().trim());
                    loja.setPontoReferencia(binding.editPontoReferencia.getText().toString().trim());

                    Intent intent = new Intent(this, Cadastro2Activity.class);
                    intent.putExtra("info_loja", loja);
                    startActivity(intent);

                }else {
                    menssagemErro(binding.editPontoReferencia, "Coloque um ponto de referência");
                }
            }else {
                menssagemErro(binding.editNumEndereco, "Preencha o número de seu endereço");
            }
        }else {
            menssagemErro(binding.editEndereco, "Preencha o endereço");
        }
    }

    private void menssagemErro(EditText editText, String messagem) {
        editText.setError(messagem);
        editText.requestFocus();
    }
}