package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

public class PopupConquista {

    private final TextView nomeView, descView;
    private final ImageView imagemView;

    private final Dialog dialog;

    public PopupConquista(Context context) {

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_conquista);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ImageView fechar = dialog.findViewById(R.id.popupconquista_fechar);
        fechar.setOnClickListener(v -> fecharPopupConquista());

        nomeView = dialog.findViewById(R.id.popupconquista_titulo);
        descView = dialog.findViewById(R.id.popupconquista_desc);
        imagemView = dialog.findViewById(R.id.popupconquista_icon);
    }

    public void abrirPopupConquista(String nome, String descricao, Drawable imagem) {
        nomeView.setText(nome);
        descView.setText(descricao);
        imagemView.setImageDrawable(imagem);

        dialog.show();
    }

    private void fecharPopupConquista() {
        dialog.dismiss();
    }

}
