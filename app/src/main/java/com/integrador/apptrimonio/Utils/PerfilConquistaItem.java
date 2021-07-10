package com.integrador.apptrimonio.Utils;

import android.graphics.drawable.Drawable;

public class PerfilConquistaItem {

    private final String nome, descricao;
    private final Drawable background, imagem;
    private final boolean desbloqueado;

    public PerfilConquistaItem(String nome, String descricao, Drawable background, Drawable imagem, boolean desbloqueado) {
        this.nome = nome;
        this.descricao = descricao;
        this.background = background;
        this.imagem = imagem;
        this.desbloqueado = desbloqueado;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public Drawable getBackground() {
        return background;
    }

    public Drawable getImagem() {
        return imagem;
    }

    public boolean isDesbloqueado() {
        return desbloqueado;
    }
}
