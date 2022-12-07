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

            if (!binding.spinnerEscolherFarmacia.getSelectedItem().toString().equals("Escolha uma fármacia")) {
                DatabaseReference npmeFarmaciaRef = reference
                        .child("usuario")
                        .child(UsuarioFirebase.getIdentificadorUsuario())
                        .child("nomeFarmacia");
                npmeFarmaciaRef.setValue(binding.spinnerEscolherFarmacia.getSelectedItem().toString());
                startActivity(new Intent(getApplicationContext(), ContasActivity.class));
            }else {
                Toast.makeText(this, "Escolha uma fármacia", Toast.LENGTH_SHORT).show();
            }
        });
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