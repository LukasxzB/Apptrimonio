package com.integrador.apptrimonio.Utils;

public class GerenciarObjetoAdicionadoItem {

    private final String idObjeto, nome;
    private final int numero;
    private final boolean ultimo;

    public GerenciarObjetoAdicionadoItem(String idObjeto, String nome, int numero, boolean ultimo) {
        this.idObjeto = idObjeto;
        this.nome = nome;
        this.numero = numero;
        this.ultimo = ultimo;
    }

    public String getIdObjeto() {
        return idObjeto;
    }

    public String getNome() {
        return nome;
    }

    public int getNumero() {
        return numero;
    }

    public boolean isUltimo() {
        return ultimo;
    }
}
