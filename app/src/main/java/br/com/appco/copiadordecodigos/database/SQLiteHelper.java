package br.com.appco.copiadordecodigos.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import br.com.appco.copiadordecodigos.model.Conta;

public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int VERSAO_BANCO = 1;//Versão do banco
    private static final String BANCO_CONTAS = "bd_contas";//Nome do banco


    public SQLiteHelper(Context context) {
        super(context, BANCO_CONTAS, null, VERSAO_BANCO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String QUERY_COLUNA = "CREATE TABLE IF NOT EXISTS tb_conta(id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " codigo VARCHAR(55), valor DOUBLE(10), data_validade VARCHAR(15)," +
                " data_pagamento VARCHAR(15), status INTEGER(5), descricao VARCHAR(45))";

        try {
            db.execSQL(QUERY_COLUNA);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

    }
}
