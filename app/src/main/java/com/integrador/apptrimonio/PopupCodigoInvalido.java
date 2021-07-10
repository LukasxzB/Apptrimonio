package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieDrawable;

public class PopupCodigoInvalido {

    private final Dialog dialog;
    private final LottieAnimationView lottieAnimationView;

    public PopupCodigoInvalido(Context context) {
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_codigoinvalido);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);

        lottieAnimationView = dialog.findViewById(R.id.codigoinvalido_icon);
    }

    public void abrirPopupCodigoInvalido(int rawRes, boolean loop, String titulo, String descricao, String tenteNovamente, View.OnClickListener listener) {
        lottieAnimationView.setAnimation(rawRes);
        lottieAnimationView.setRepeatCount(loop ? LottieDrawable.INFINITE : 0);

        TextView tituloText, descricaoText, tenteNovamenteText;
        tituloText = dialog.findViewById(R.id.codigoinvalido_titulo);
        descricaoText = dialog.findViewById(R.id.codigoinvalido_desc);
        tenteNovamenteText = dialog.findViewById(R.id.codigoinvalido_tentenovamente);
        tituloText.setText(titulo);
        descricaoText.setText(descricao);
        tenteNovamenteText.setText(tenteNovamente);

        dialog.findViewById(R.id.codigoinvalido_fechar).setOnClickListener(listener);

        dialog.show();
    }

    public void fecharPopupCodigoInvalido() {
        dialog.dismiss();
    }
}
