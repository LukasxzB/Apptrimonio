package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyUtils;

public class PopupGerenciar {

    private final Dialog dialog;
    private final LottieAnimationView[] checks = new LottieAnimationView[3];
    private final boolean[] ativos = new boolean[3];

    private final Context context;

    public PopupGerenciar(Context context) {
        this.context = context;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_gerenciarobjeto);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        Utils utils = new Utils(context);
        VolleyUtils volleyUtils = new VolleyUtils(context);

        //listener do botão fechar
        ImageView fechar = dialog.findViewById(R.id.gerenciar_fechar);
        fechar.setOnClickListener(v -> fecharPopup());

        //listeners dos motivos
        dialog.findViewById(R.id.gerenciar_1).setOnClickListener(v -> check(0));
        dialog.findViewById(R.id.gerenciar_2).setOnClickListener(v -> check(1));
        dialog.findViewById(R.id.gerenciar_3).setOnClickListener(v -> check(2));

        checks[0] = dialog.findViewById(R.id.gerenciar_1_check);
        checks[1] = dialog.findViewById(R.id.gerenciar_2_check);
        checks[2] = dialog.findViewById(R.id.gerenciar_3_check);

    }

    public void abrirPopup() {
        dialog.show();
    }

    public void fecharPopup() {
        dialog.dismiss();
    }

    public boolean isDone() { //retorna true caso tiver motivos selecionados
        String motivos = getMotivos();
        if (motivos.length() <= 0) {
            Toast.makeText(context, context.getResources().getString(R.string.verErrorCheck), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void check(int id) {
        if (ativos[id]) {
            checks[id].setSpeed(-1);
        } else {
            checks[id].setSpeed(1);
        }

        checks[id].playAnimation();
        ativos[id] = !ativos[id];
    }

    public String getMotivos() {
        StringBuilder motivos = new StringBuilder();
        for (int i = 0; i < ativos.length; i++) {
            if (ativos[i]) {
                motivos.append((char) (i + 65)).append(" ");
            }
        }
        return motivos.toString().toLowerCase();
    }

    public void setButtonListener(View.OnClickListener listener) {
        dialog.findViewById(R.id.gerenciar_botao).setOnClickListener(listener);
    }

    public void setText(String titulo, String desc, String warn) {
        ((TextView) dialog.findViewById(R.id.gerenciar_titulo)).setText(titulo);
        ((TextView) dialog.findViewById(R.id.gerenciar_desc)).setText(desc);
        ((TextView) dialog.findViewById(R.id.gerenciar_warning)).setText(warn);
    }
}
