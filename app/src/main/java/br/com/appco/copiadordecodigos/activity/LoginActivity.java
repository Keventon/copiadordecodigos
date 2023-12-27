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
import br.com.appco.copiadordecodigos.activity.components.ButtonMain;
import br.com.appco.copiadordecodigos.activity.components.CustomAlertDialog;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityLoginBinding;
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private FirebaseAuth autenticacao;
    private ButtonMain buttonMain;
    private CustomAlertDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.GONE);
        buttonMain = binding.buttonAcessar;

        autenticacao = ConfiguracoesFirebase.getFirebaseAutenticacao();

        buttonMain.binding.constrainLayout.setOnClickListener(view -> {
            binding.editCampoEmail.setClickable(false);
            binding.editCampoSenha.setClickable(false);
            recuperarCampos();
        });

        binding.textRealizarCadastro.setOnClickListener(view -> startActivity(new Intent(LoginActivity.this, CadastroActivity.class)));

    }

    public void recuperarCampos() {

        binding.progressBar.setVisibility(View.VISIBLE);

        String email = binding.editCampoEmail.getText().toString();
        String senha = binding.editCampoSenha.getText().toString();

        if (!email.isEmpty()) {
            if (!senha.isEmpty()) {
                autenticarUsuario(email, senha);
            }else {
                binding.editCampoEmail.setClickable(true);
                binding.editCampoSenha.setClickable(true);
                binding.progressBar.setVisibility(View.GONE);
                binding.editCampoSenha.requestFocus();
                binding.editCampoSenha.setError("Preencha este campo.");
            }
        }else {
            binding.editCampoEmail.setClickable(true);
            binding.editCampoSenha.setClickable(true);
            binding.progressBar.setVisibility(View.GONE);
            binding.editCampoEmail.requestFocus();
            binding.editCampoEmail.setError("Preencha este campo.");
        }
    }

    public void autenticarUsuario(String email, String senha) {
        autenticacao.signInWithEmailAndPassword(email, senha).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                binding.progressBar.setVisibility(View.GONE);
                startActivity(new Intent(getApplicationContext(), ContasActivity.class));
            } else {
                binding.editCampoEmail.setClickable(true);
                binding.editCampoSenha.setClickable(true);
                binding.progressBar.setVisibility(View.GONE);

                dialog = new CustomAlertDialog(
                        LoginActivity.this,
                        R.drawable.warning_message_with_background,
                        "Aviso",
                        "Verifique se seu e-mail e senha estão corretos.",
                        "Tentar novamente",
                        "Voltar",
                        false,
                        R.drawable.button_custom_alert_button_orange,
                        v -> dialog.dismiss(),
                        v -> {

                        }
                );

                dialog.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
}