package br.com.appco.copiadordecodigos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

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
}
