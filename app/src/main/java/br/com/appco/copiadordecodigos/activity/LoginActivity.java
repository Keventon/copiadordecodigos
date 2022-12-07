package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityLoginBinding;
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth autenticacao;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();

        binding.buttonAcessar.setOnClickListener(view -> {
            recuperarCampos();
        });

    }

    public void recuperarCampos() {

        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        String email = binding.editCampoEmail.getText().toString();
        String senha = binding.editCampoSenha.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                progressDialog.dismiss();
                autenticarUsuario(email, senha);
            }else {
                progressDialog.dismiss();
                binding.editCampoSenha.requestFocus();
                binding.editCampoSenha.setError("Preencha este campo.");
            }
        }else {
            progressDialog.dismiss();
            binding.editCampoEmail.requestFocus();
            binding.editCampoEmail.setError("Preencha este campo.");
        }
    }

    public void autenticarUsuario(String email, String senha) {
        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                startActivity(new Intent(getApplicationContext(), ContasActivity.class));
            } else {
                Toast.makeText(this, "Email ou senha incorretos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}