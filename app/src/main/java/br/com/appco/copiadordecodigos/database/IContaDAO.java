package br.com.appco.copiadordecodigos.database;

import java.util.List;

import br.com.appco.copiadordecodigos.model.Conta;

public interface IContaDAO {

    public boolean salvar(Conta conta);
    public boolean atualizar(Conta conta);
    public boolean deletar(Conta conta);
    public List<Conta> listar();
    public List<Conta> listarContasPagas();
}
