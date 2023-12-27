package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.components.ButtonMain;
import br.com.appco.copiadordecodigos.databinding.ActivityCadastro2Binding;
import br.com.appco.copiadordecodigos.model.Loja;

public class Cadastro2Activity extends AppCompatActivity {

    private ActivityCadastro2Binding binding;
    private Loja loja;
    private ButtonMain buttonMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastro2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        buttonMain = binding.buttonContinuarCadastro;

        binding.imageVoltar.setOnClickListener(view -> finish());

        if(intent.hasExtra("info_loja")) {
           loja = (Loja) intent.getSerializableExtra("info_loja");

           carregarSpinnerCidade();
           carregarSpinnerEstado();
            binding.spinnerCidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    String[] bairros;
                    switch (i) {
                        case 1:
                            bairros = getResources().getStringArray(R.array.bairrosBelem);
                            break;
                        case 2:
                            bairros = getResources().getStringArray(R.array.bairrosCameta);
                            break;

                        default:
                            bairros = new String[0];
                            break;
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                            Cadastro2Activity.this, android.R.layout.simple_spinner_item,
                            bairros
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    binding.spinnerBairro.setAdapter(adapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            buttonMain.binding.constrainLayout.setOnClickListener(view -> recuperarDados());
        }
    }

    private void recuperarDados() {
        if (!binding.spinnerCidade.getSelectedItem().toString().equals("Selecione uma cidade")) {
            if (!binding.spinnerBairro.getSelectedItem().toString().equals("Selecione um bairro")) {
                if (!binding.spinnerEstado.getSelectedItem().toString().equals("Selecione um Estado")) {

                    Loja loja1 = new Loja();
                    loja1.setEndereco(loja.getEndereco());
                    loja1.setNumEndereco(loja.getNumEndereco());
                    loja1.setPontoReferencia(loja.getPontoReferencia());
                    loja1.setCidade(binding.spinnerCidade.getSelectedItem().toString());
                    loja1.setBairro(binding.spinnerBairro.getSelectedItem().toString());
                    loja1.setEstado(binding.spinnerEstado.getSelectedItem().toString());

                    Intent intent = new Intent(this, Cadastro3Activity.class);
                    intent.putExtra("info_loja", loja1);
                    startActivity(intent);

                }else {
                    Toast.makeText(this, "Selecione um estado", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Selecione um bairro", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Selecione uma cidade", Toast.LENGTH_SHORT).show();
        }
    }

    private void carregarSpinnerCidade() {
        String[] cidades = getResources().getStringArray(R.array.cidades);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                cidades
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCidade.setAdapter(adapter);

    }

    private void carregarSpinnerEstado() {
        String[] estados = getResources().getStringArray(R.array.estado);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                estados
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEstado.setAdapter(adapter);

    }
}