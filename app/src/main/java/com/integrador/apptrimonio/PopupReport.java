package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;


public class PopupReport {

    private final Dialog dialog;
    private final LottieAnimationView[] checks = new LottieAnimationView[13];
    private final boolean[] ativos = new boolean[13];

    private final String idObjeto;

    private final Utils utils;
    private final VolleyUtils volleyUtils;
    private final Context context;

    public PopupReport(Context context, String idObjeto) {
        this.context = context;
        this.idObjeto = idObjeto;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_reportar);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        utils = new Utils(context);
        volleyUtils = new VolleyUtils(context);

        //listener do botão fechar
        ImageView fechar = dialog.findViewById(R.id.reportar_fechar);
        fechar.setOnClickListener(v -> fecharPopup());

        //listener do botão de reportar
        Button reportar = dialog.findViewById(R.id.reportar_botao);
        reportar.setOnClickListener(v -> reportar());

        //listeners dos motivos
        dialog.findViewById(R.id.reportar_1).setOnClickListener(v -> check(0));
        dialog.findViewById(R.id.reportar_2).setOnClickListener(v -> check(1));
        dialog.findViewById(R.id.reportar_3).setOnClickListener(v -> check(2));
        dialog.findViewById(R.id.reportar_4).setOnClickListener(v -> check(3));
        dialog.findViewById(R.id.reportar_5).setOnClickListener(v -> check(4));
        dialog.findViewById(R.id.reportar_6).setOnClickListener(v -> check(5));
        dialog.findViewById(R.id.reportar_7).setOnClickListener(v -> check(6));
        dialog.findViewById(R.id.reportar_8).setOnClickListener(v -> check(7));
        dialog.findViewById(R.id.reportar_9).setOnClickListener(v -> check(8));
        dialog.findViewById(R.id.reportar_10).setOnClickListener(v -> check(9));
        dialog.findViewById(R.id.reportar_11).setOnClickListener(v -> check(10));
        dialog.findViewById(R.id.reportar_12).setOnClickListener(v -> check(11));
        dialog.findViewById(R.id.reportar_13).setOnClickListener(v -> check(12));

        checks[0] = dialog.findViewById(R.id.reportar_1_check);
        checks[1] = dialog.findViewById(R.id.reportar_2_check);
        checks[2] = dialog.findViewById(R.id.reportar_3_check);
        checks[3] = dialog.findViewById(R.id.reportar_4_check);
        checks[4] = dialog.findViewById(R.id.reportar_5_check);
        checks[5] = dialog.findViewById(R.id.reportar_6_check);
        checks[6] = dialog.findViewById(R.id.reportar_7_check);
        checks[7] = dialog.findViewById(R.id.reportar_8_check);
        checks[8] = dialog.findViewById(R.id.reportar_9_check);
        checks[9] = dialog.findViewById(R.id.reportar_10_check);
        checks[10] = dialog.findViewById(R.id.reportar_11_check);
        checks[11] = dialog.findViewById(R.id.reportar_12_check);
        checks[12] = dialog.findViewById(R.id.reportar_13_check);

    }

    public void abrirPopup() {
        dialog.show();
    }

    public void fecharPopup() {
        dialog.dismiss();
    }

    private void reportar() {
        String motivos = getMotivos();
        if (motivos.length() <= 0) {
            Toast.makeText(context, context.getResources().getString(R.string.reportErrorCheck), Toast.LENGTH_LONG).show();
        } else { //caso tiver motivos

            utils.abrirPopUpCarregando();

            VolleyInterface callback = new VolleyInterface() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, context.getResources().getString(R.string.reportSuccess), Toast.LENGTH_LONG).show();
                    utils.fecharPopUpCarregando();
                    fecharPopup();
                }

                @Override
                public void onError(String erro) {

                    if (erro.equalsIgnoreCase("Email not verified.")) {
                        Toast.makeText(context, context.getResources().getString(R.string.emailRequired), Toast.LENGTH_LONG).show();
                    } else if (erro.equalsIgnoreCase("Object not found!")) {
                        Toast.makeText(context, context.getResources().getString(R.string.objNotFound), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.reportError), Toast.LENGTH_LONG).show();
                    }

                    utils.fecharPopUpCarregando();
                    fecharPopup();
                }
            };

            volleyUtils.reportarObjeto(callback, idObjeto, motivos);
        }
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

}
