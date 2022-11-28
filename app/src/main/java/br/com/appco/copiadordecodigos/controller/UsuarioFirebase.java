package br.com.appco.copiadordecodigos.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;

public class UsuarioFirebase {

    public static String getIdentificadorUsuario() {
        FirebaseAuth usuario = ConfiguracoesFirebase.getFirebaseAutenticacao();
        String email = usuario.getCurrentUser().getEmail();
        String identificadorUsuario = Base64Custom.codificarBase64(email);

        return identificadorUsuario;
    }


    public static FirebaseUser getUsuarioAtual(){
        FirebaseAuth usuario = ConfiguracoesFirebase.getFirebaseAutenticacao();
        return  usuario.getCurrentUser();
    }

    public  static Usuario getDadosUsuarioLogado(){

        FirebaseUser firebaseUser = getUsuarioAtual();
        Usuario usuario = new Usuario();
        //usuario.setEmail(firebaseUser.getEmail());
        usuario.setNome(firebaseUser.getDisplayName());
        //  usuario.setToken(""); // falta recuperar token do usuario logado pra notificar no grupo

        return  usuario;
    }

}
