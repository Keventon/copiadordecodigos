package br.com.appco.copiadordecodigos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import br.com.appco.copiadordecodigos.model.Conta;

public class ContaDAO {
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase db;

    public ContaDAO(Context context) {
        sqLiteHelper = new SQLiteHelper(context);
        db = sqLiteHelper.getWritableDatabase();
    }

    public long adicionarConta(Conta conta) {
        ContentValues values = new ContentValues();
        values.put("codigo", conta.getCodigo());
        values.put("valor", conta.getValor());
        values.put("data_validade", conta.getDataValidade());
        values.put("data_pagamento", conta.getDataPagamento());
        values.put("status", conta.getStatus());
        values.put("descricao", conta.getDescricao());

        return db.insert("tb_conta", null, values);
    }
}
