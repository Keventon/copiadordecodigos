package br.com.appco.copiadordecodigos.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.components.ButtonMain;
import br.com.appco.copiadordecodigos.activity.components.CustomAlertDialog;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.databinding.ActivityCadastro4Binding;
import br.com.appco.copiadordecodigos.model.Loja;
import br.com.appco.copiadordecodigos.util.Base64Custom;

public class Cadastro4Activity extends AppCompatActivity {

    private ActivityCadastro4Binding binding;
    private Loja loja;
    private CustomAlertDialog dialog = null;
    private ButtonMain buttonBackground;
    private FirebaseAuth auth = ConfiguracoesFirebase.getFirebaseAutenticacao();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastro4Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        buttonBackground = binding.buttonCadastro;

        binding.imageVoltar.setOnClickListener(view -> finish());

        Intent intent = getIntent();

        binding.imageVoltar.setOnClickListener(view -> finish());

        if(intent.hasExtra("info_loja")) {
            loja = (Loja) intent.getSerializableExtra("info_loja");

            binding.buttonCadastro.setOnClickListener(view -> recuperarDados());
        }
    }

    private void recuperarDados() {
        if (!binding.editEmail.getText().toString().equals("")) {
            if (!binding.editSenha.getText().toString().equals("")) {
                if (!binding.editConfirmarSenha.getText().toString().equals(binding.editSenha.getText().toString())) {
                    Loja loja1 = new Loja();
                    loja1.setEndereco(loja.getEndereco());
                    loja1.setNumEndereco(loja.getNumEndereco());
                    loja1.setPontoReferencia(loja.getPontoReferencia());
                    loja1.setCidade(loja.getCidade());
                    loja1.setBairro(loja.getBairro());
                    loja1.setEstado(loja.getEstado());
                    loja1.setNomeProprietario(loja.getNomeProprietario());
                    loja1.setSobrenomeProprietario(loja.getSobrenomeProprietario());
                    loja1.setNomeLoja(loja.getNomeLoja());
                    loja1.setEmail(binding.editEmail.getText().toString().trim());
                    loja1.setSenha(binding.editSenha.getText().toString().trim());

                    auth.createUserWithEmailAndPassword(binding.editEmail.getText().toString().trim(), binding.editSenha.getText().toString().trim())
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    String id = Base64Custom.codificarBase64(binding.editEmail.getText().toString().trim());
                                    loja1.setId(id);
                                    loja1.salvar((error, ref) -> {
                                        dialogCompleteRegister();
                                    });
                                }else {
                                    buttonBackground.buttonEncerrar();
                                    String excessao = "";
                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        excessao = "Digite uma senha mais forte.";
                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        excessao = "Por favor, digite um e-mail válido.";
                                    } catch (FirebaseAuthUserCollisionException e) {
                                        excessao = "Esta conta já foi cadastrada.";
                                    } catch (Exception e) {
                                        excessao = "Erro ao cadastrar empresa: " + e.getMessage();
                                        e.printStackTrace();
                                    }

                                    dialogExcpetionRegister(excessao);
                                }
                            });

                }else {
                    menssagemErro(binding.editConfirmarSenha, "Senhas não coincidem");
                }
            }else {
                menssagemErro(binding.editSenha, "Preencha uma senha");
            }
        }else {
            menssagemErro(binding.editEmail, "Preencha seu e-mail");
        }
    }

    private void dialogCompleteRegister() {

        dialog = new CustomAlertDialog(
                Cadastro4Activity.this,
                R.drawable.success_message_with_background,
                "Cadastrado com sucesso",
                "Agora entre com seu mesmo e-mail e senha cadastrado.",
                "Entrar agora",
                "Voltar",
                false,
                R.drawable.button_custom_alert_button_green,
                v -> {
                    dialog.dismiss();
                    buttonBackground.buttonEncerrar();
                    auth = ConfiguracoesFirebase.getFirebaseAutenticacao();
                    auth.signOut();
                    finish();
                },
                v -> {

                }
        );

        dialog.show();

    }

    private void dialogExcpetionRegister(String exception) {

        dialog = new CustomAlertDialog(
                Cadastro4Activity.this,
                R.drawable.warning_message_with_background,
                "Atenção",
                exception,
                "Ok",
                "Voltar",
                false,
                R.drawable.button_custom_alert_button_orange,
                v -> {
                    dialog.dismiss();
                    buttonBackground.buttonEncerrar();
                },
                v -> {

                }
        );

        dialog.show();
    }

    private void menssagemErro(EditText editText, String messagem) {
        editText.setError(messagem);
        editText.requestFocus();
    }
}