package br.com.appco.copiadordecodigos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import br.com.appco.copiadordecodigos.model.Conta;

public class ContaDAO implements IContaDAO{
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase db;
    private SQLiteDatabase lerDB;

    public ContaDAO(Context context) {
        sqLiteHelper = new SQLiteHelper(context);
        db = sqLiteHelper.getWritableDatabase();
        lerDB = sqLiteHelper.getReadableDatabase();
    }

    public List<Conta> listarContas() {
        List<Conta> contas = new ArrayList<>();
        Cursor cursor = db.query("tb_conta",
                new String[]{"id", "codigo", "valor", "data_validade", "data_pagamento", "status", "descricao"},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            Conta conta = new Conta();
            conta.setId(cursor.getInt(0));
            conta.setCodigo(cursor.getString(1));
            conta.setValor(cursor.getDouble(2));
            conta.setDataValidade(cursor.getString(3));
            conta.setDataPagamento(cursor.getString(4));
            conta.setStatus(cursor.getInt(5));
            conta.setDescricao(cursor.getString(6));
            contas.add(conta);
        }

        return contas;
    }

    @Override
    public boolean salvar(Conta conta) {

        try {
            ContentValues values = new ContentValues();
            values.put("codigo", conta.getCodigo());
            values.put("valor", conta.getValor());
            values.put("data_validade", conta.getDataValidade());
            values.put("data_pagamento", conta.getDataPagamento());
            values.put("status", conta.getStatus());
            values.put("descricao", conta.getDescricao());

            db.insert("tb_conta", null, values);

        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean atualizar(Conta conta) {

        ContentValues values = new ContentValues();
        values.put("codigo", conta.getCodigo());
        values.put("valor", conta.getValor());
        values.put("data_validade", conta.getDataValidade());
        values.put("data_pagamento", conta.getDataPagamento());
        values.put("status", conta.getStatus());
        values.put("descricao", conta.getDescricao());

        try {
            String[] args = {String.valueOf(conta.getId())};
            db.update("tb_conta", values, "id=?", args);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean deletar(Conta conta) {

        try {
            String[] args = {String.valueOf(conta.getId())};
            db.delete("tb_conta","id=?", args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<Conta> listar() {
        List<Conta> contas = new ArrayList<>();

        String sql = "SELECT * FROM tb_conta WHERE status = 0;";

        Cursor cursor = lerDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Conta conta = new Conta();
            conta.setId(cursor.getInt(0));
            conta.setCodigo(cursor.getString(1));
            conta.setValor(cursor.getDouble(2));
            conta.setDataValidade(cursor.getString(3));
            conta.setDataPagamento(cursor.getString(4));
            conta.setStatus(cursor.getInt(5));
            conta.setDescricao(cursor.getString(6));
            contas.add(conta);
        }

        return contas;
    }

    @Override
    public List<Conta> listarContasPagas() {
        List<Conta> contas = new ArrayList<>();

        String sql = "SELECT * FROM tb_conta WHERE status = 1;";

        Cursor cursor = lerDB.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            Conta conta = new Conta();
            conta.setId(cursor.getInt(0));
            conta.setCodigo(cursor.getString(1));
            conta.setValor(cursor.getDouble(2));
            conta.setDataValidade(cursor.getString(3));
            conta.setDataPagamento(cursor.getString(4));
            conta.setStatus(cursor.getInt(5));
            conta.setDescricao(cursor.getString(6));
            contas.add(conta);
        }

        return contas;
    }
}
