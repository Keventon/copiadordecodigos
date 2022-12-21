package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityCadastrarFuncionarioBinding;
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;
import br.com.appco.copiadordecodigos.util.Util;

public class CadastrarFuncionarioActivity extends AppCompatActivity {

    private ActivityCadastrarFuncionarioBinding binding;
    private FirebaseAuth auth= ConfiguracoesFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastrarFuncionarioBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.imageVoltarCadastrarFuncionario.setOnClickListener(view -> startActivity(new Intent(CadastrarFuncionarioActivity.this,
                LoginActivity.class)));

        binding.buttonCadastrarFuncionRio.setOnClickListener(view -> cadastrarFuncionario());
    }

    private void cadastrarFuncionario() {
        String nomeFuncionario = binding.editNomeFuncionario.getText().toString();
        String email = "douglasfarmacia@gmail.com";
        String senha = "palmeiras";
        String emailBase64 = Base64Custom.codificarBase64(email);

        if (!nomeFuncionario.isEmpty()) {

            auth.createUserWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Usuario usuario = new Usuario();
                        usuario.setNomeFarmacia("Farmácia 1");
                        usuario.setSenha(senha);
                        usuario.setIdentificador("F");
                        usuario.setEmail(emailBase64);
                        usuario.setId(emailBase64);
                        usuario.salvar();
                        Toast.makeText(CadastrarFuncionarioActivity.this,
                                "Funcionário cadastrado com sucesso",
                                Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(CadastrarFuncionarioActivity.this,
                                "Erro ao cadastrar funcionário",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}