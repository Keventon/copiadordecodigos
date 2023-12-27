package br.com.appco.copiadordecodigos.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;

public class Loja implements Serializable {
    String nomeLoja;
    String nomeProprietario;
    String sobrenomeProprietario;
    String endereco;
    String numEndereco;
    String pontoReferencia;
    String bairro;
    String cidade;
    String estado;
    int assinaturaAtiva;
    String dataCriacao;
    String email;
    String senha;
    String id;
    String telefone;

    public Loja() {
    }

    public void salvar(DatabaseReference.CompletionListener listener) {
        DatabaseReference firebase = ConfiguracoesFirebase.getFirebase();
        firebase.child("loja").child(this.id).setValue(this, listener);
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getNomeLoja() {
        return nomeLoja;
    }

    public void setNomeLoja(String nomeLoja) {
        this.nomeLoja = nomeLoja;
    }

    public String getNomeProprietario() {
        return nomeProprietario;
    }

    public void setNomeProprietario(String nomeProprietario) {
        this.nomeProprietario = nomeProprietario;
    }

    public String getSobrenomeProprietario() {
        return sobrenomeProprietario;
    }

    public void setSobrenomeProprietario(String sobrenomeProprietario) {
        this.sobrenomeProprietario = sobrenomeProprietario;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getNumEndereco() {
        return numEndereco;
    }

    public void setNumEndereco(String numEndereco) {
        this.numEndereco = numEndereco;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public int getAssinaturaAtiva() {
        return assinaturaAtiva;
    }

    public void setAssinaturaAtiva(int assinaturaAtiva) {
        this.assinaturaAtiva = assinaturaAtiva;
    }

    public String getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(String dataCriacao) {
        this.dataCriacao = dataCriacao;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }
}
