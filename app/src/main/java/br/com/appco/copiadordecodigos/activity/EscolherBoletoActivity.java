package br.com.appco.copiadordecodigos.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityEscolherBoletoBinding;
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Util;

public class EscolherBoletoActivity extends AppCompatActivity {

    private ActivityEscolherBoletoBinding binding;
    private FirebaseAuth autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();

    private DatabaseReference reference = ConfiguracoesFirebase.getFirebase();
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEscolherBoletoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        carregarSpinnerFarmacias();

        binding.imageVoltarEscolherBoleto.setOnClickListener(view -> finish());

        binding.spinnerEscolherFarmacia.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 0) {
                    binding.buttonContinuarEscolherBoleto.setVisibility(View.VISIBLE);
                }else {
                    binding.buttonContinuarEscolherBoleto.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.buttonContinuarEscolherBoleto.setOnClickListener(view -> {

            progressDialog = new ProgressDialog(EscolherBoletoActivity.this);
            progressDialog.show();
            progressDialog.setContentView(R.layout.progress_dialog);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            if (!binding.spinnerEscolherFarmacia.getSelectedItem().toString().equals("Escolha uma fármacia")) {
                if (Util.checarConexaoDispositivo(EscolherBoletoActivity.this)) {
                    Usuario usuario = new Usuario();
                    usuario.atualizarNomeFarmacia(binding.spinnerEscolherFarmacia.getSelectedItem().toString(), ((error, ref) -> {
                        progressDialog.dismiss();
                        finish();
                    }));
                }else {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Sem conexão com a internet", Toast.LENGTH_SHORT).show();
                }
            }else {
                progressDialog.dismiss();
                Toast.makeText(this, "Escolha uma fármacia", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void carregarSpinnerFarmacias() {
        String[] farmacias = getResources().getStringArray(R.array.farmacias);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                farmacias
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerEscolherFarmacia.setAdapter(adapter);

    }
}