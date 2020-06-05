package com.matech.agenda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;

import com.matech.agenda.modelo.Aluno;

import java.util.ArrayList;
import java.util.List;

public class AlunoDAO extends SQLiteOpenHelper {

    // importando sql helper e informando nome do bando, factory sao customizacoes e
    // version e versao banco.
    public AlunoDAO(Context context) {
        super(context, "DBAgenda", null, 2);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        String sql = "CREATE TABLE Alunos (id INTEGER PRIMARY KEY, nome TEXT NOT NULL, endereco TEXT, telefone TEXT, site TEXT, nota REAL, caminhoFoto TEXT);";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String sql = "";
        switch (oldVersion) {
            case 1:
                sql = "ALTER TABLE Alunos ADD COLUMN caminhoFoto TEXT";
                db.execSQL(sql); // indo para versao 2
        }
    }

    public void Inserir(Aluno aluno) {

        // solicitando para o sql lite uma databse gravavel
        SQLiteDatabase db = getWritableDatabase();

        // o metodo foi extraido atraves do menu refactory, extract method
        ContentValues dados = getDadosDoAluno(aluno);

        // insert na tabela referica, segundo parametro so usa se quiser
        // inserir linhas em branco em geral nao usa
        db.insert("Alunos", null, dados );
    }

    @NonNull
    private ContentValues getDadosDoAluno(Aluno aluno) {
        // coleção de dados a serem inseridos similar ao MAP do java
        ContentValues dados = new ContentValues();
        dados.put("nome", aluno.getNome());
        dados.put("endereco", aluno.getEndereco());
        dados.put("telefone", aluno.getTelefone());
        dados.put("site", aluno.getSite());
        dados.put("nota", aluno.getNota());
        dados.put("caminhoFoto", aluno.getCaminhoFoto());
        return dados;
    }

    public List<Aluno> BuscarAlunos() {

        String sql = "SELECT * FROM Alunos;";

        // solicitando para o sql lite uma databse para leitura
        SQLiteDatabase db = getReadableDatabase();

        // cursor similar a recordset
        Cursor c = db.rawQuery(sql, null);

        // instanciando array list para devolver a listagem de
        // de dados no formato esperado para carregar a lista.
        List<Aluno> alunos = new ArrayList<Aluno>();

        // looping ate final do recordset
        while (c.moveToNext()) {
            Aluno aluno = new Aluno();
            aluno.setId(c.getLong(c.getColumnIndex("id")));
            aluno.setNome(c.getString(c.getColumnIndex("nome")));
            aluno.setEndereco(c.getString(c.getColumnIndex("endereco")));
            aluno.setTelefone(c.getString(c.getColumnIndex("telefone")));
            aluno.setSite(c.getString(c.getColumnIndex("site")));
            aluno.setNota(c.getDouble(c.getColumnIndex("nota")));
            aluno.setCaminhoFoto(c.getString(c.getColumnIndex("caminhoFoto")));
            alunos.add(aluno);
        }

        // finalizando
        c.close();

        // devolvendo a lista de alunos
        return alunos;

    }

    public void Deletar(Aluno aluno) {
        SQLiteDatabase db = getWritableDatabase();

        String [] params = {String.valueOf(aluno.getId())};
        db.delete("Alunos", "id = ?", params);
    }

    public void Alterar(Aluno aluno) {

        SQLiteDatabase db = getWritableDatabase();

        // o metodo foi extraido atraves do menu refactory, extract method
        ContentValues dados = getDadosDoAluno(aluno);

        String[] params ={aluno.getId().toString()};
        db.update("Alunos", dados, "id = ?", params);
    }

    public boolean ValidaAluno(String telefone) {
        SQLiteDatabase db = getReadableDatabase();
        String strSql = "SELECT * FROM Alunos WHERE telefone=?";
        Cursor c = db.rawQuery(strSql, new String[]{telefone});
        int resultados = c.getCount();
        c.close();
        return resultados > 0;
    }
}
