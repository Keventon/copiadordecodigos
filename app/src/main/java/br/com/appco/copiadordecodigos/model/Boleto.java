package br.com.appco.copiadordecodigos.model;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;

public class Boleto implements Serializable {
    private String nome;
    private String nomeEmpresa;
    private String id;
    private String descricao;
    private String codigo;
    private String dataPagamento;
    private String dataValidade;
    private String nomeFarmacia;
    private Double valor;
    private Double valorMulta;
    private int status;

    public Boleto() {
    }

    public void salvar(DatabaseReference.CompletionListener listener) {
        DatabaseReference firebaseRef = ConfiguracoesFirebase.getFirebase();
        DatabaseReference firebase = firebaseRef.child("boletos").child(nomeFarmacia).push();
        setId(firebase.getKey());
        firebase.setValue(this, listener);
    }

    public void atualizar(DatabaseReference.CompletionListener listener) {
        DatabaseReference firebaseRef = ConfiguracoesFirebase.getFirebase();
        DatabaseReference firebase = firebaseRef.child("boletos").child(nomeFarmacia).child(getId());
        firebase.setValue(this, listener);
    }

    public String getNomeEmpresa() {
        return nomeEmpresa;
    }

    public void setNomeEmpresa(String nomeEmpresa) {
        this.nomeEmpresa = nomeEmpresa;
    }

    public Double getValorMulta() {
        return valorMulta;
    }

    public void setValorMulta(Double valorMulta) {
        this.valorMulta = valorMulta;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(String dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(String dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getNomeFarmacia() {
        return nomeFarmacia;
    }

    public void setNomeFarmacia(String nomeFarmacia) {
        this.nomeFarmacia = nomeFarmacia;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
