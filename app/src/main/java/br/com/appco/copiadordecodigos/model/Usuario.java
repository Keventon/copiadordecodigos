package br.com.appco.copiadordecodigos.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;

public class Usuario {
    private String nome;
    private String email;
    private String senha;
    private String id;
    private String nomeFarmacia;
    private String identificador;

    public Usuario() {
    }

    public void salvar() {
        DatabaseReference firebase = ConfiguracoesFirebase.getFirebase();
        firebase.child("usuario").child(this.id).setValue(this);
    }

    public void atualizarNomeFarmacia(String nomeFarmacia, DatabaseReference.CompletionListener listener) {
        DatabaseReference reference = ConfiguracoesFirebase.getFirebase();
        DatabaseReference npmeFarmaciaRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");
        npmeFarmaciaRef.setValue(nomeFarmacia, listener);
    }

    public String getNomeFarmacia() {
        return nomeFarmacia;
    }

    public void setNomeFarmacia(String nomeFarmacia) {
        this.nomeFarmacia = nomeFarmacia;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Exclude
    public String getSenha() {
        return senha;
    }

    @Exclude
    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }
}
