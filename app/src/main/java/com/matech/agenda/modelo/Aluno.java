package com.matech.agenda.modelo;

import java.io.Serializable;

public class Aluno implements Serializable {
    private Long id;
    private String nome;
    private String endereco;
    private String telefone;
    private String site;
    private Double nota;
    private String caminhoFoto;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    // Como não existe um toString no Aluno.java ele usa a implementação padrão do object e temos o que aparece na tela do celular.
    // Para que algo seja mostrado é preciso sobrescrever o toString no Aluno.java. Ao final da tela, depois de setNome, digitamos toString e
    // damos um "Enter" e isso será sobrescrito. Apagamos o super.toString e falamos o que deve ser devolvido, no caso, o nome do aluno
    // através do getNome. Teremos o seguinte, return getId() + " - " + getNome()
    @Override
    public String toString() {
        return getId() + " - " + getNome();
    }
}
