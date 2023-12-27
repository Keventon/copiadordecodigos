package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.components.ButtonMain;
import br.com.appco.copiadordecodigos.databinding.ActivityCadastro3Binding;
import br.com.appco.copiadordecodigos.model.Loja;

public class Cadastro3Activity extends AppCompatActivity {

    private ActivityCadastro3Binding binding;
    private Loja loja;
    private ButtonMain buttonMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastro3Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        buttonMain = binding.buttonCadastro;

        binding.imageVoltar.setOnClickListener(view -> finish());

        if(intent.hasExtra("info_loja")) {
            loja = (Loja) intent.getSerializableExtra("info_loja");

            buttonMain.binding.constrainLayout.setOnClickListener(view -> recuperarDados());
        }
    }

    private void recuperarDados() {
        if (!binding.editNomeProprietario.getText().toString().equals("")) {
            if (!binding.editSobrenomeProprietario.getText().toString().equals("")) {
                if (!binding.editNomeLoja.getText().toString().equals("")) {
                    if (!binding.editTelefone.getText().toString().equals("") && binding.editTelefone.getText().length() == 19) {
                        Loja loja1 = new Loja();
                        loja1.setEndereco(loja.getEndereco());
                        loja1.setNumEndereco(loja.getNumEndereco());
                        loja1.setPontoReferencia(loja.getPontoReferencia());
                        loja1.setCidade(loja.getCidade());
                        loja1.setBairro(loja.getBairro());
                        loja1.setEstado(loja.getEstado());
                        loja1.setNomeProprietario(binding.editNomeProprietario.getText().toString().trim());
                        loja1.setSobrenomeProprietario(binding.editSobrenomeProprietario.getText().toString().trim());
                        loja1.setNomeLoja(binding.editNomeLoja.getText().toString().trim());
                        loja1.setTelefone(binding.editTelefone.getText().toString().trim());

                        Intent intent = new Intent(this, Cadastro4Activity.class);
                        intent.putExtra("info_loja", loja1);
                        startActivity(intent);
                    }else {
                        menssagemErro(binding.editTelefone, "Preencha um telefone válido");
                    }

                }else {
                    menssagemErro(binding.editNomeLoja, "Preencha o nome da sua loja");
                }
            }else {
                menssagemErro(binding.editSobrenomeProprietario, "Preencha seu sobrenome");
            }
        }else {
            menssagemErro(binding.editNomeProprietario, "Preencha seu nome");
        }
    }

    private void menssagemErro(EditText editText, String messagem) {
        editText.setError(messagem);
        editText.requestFocus();
    }
}