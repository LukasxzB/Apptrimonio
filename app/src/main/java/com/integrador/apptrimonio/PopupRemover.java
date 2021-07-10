package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.integrador.apptrimonio.Utils.UserInterface;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;

public class PopupRemover {

    private final Dialog dialog;
    private final LottieAnimationView[] checks = new LottieAnimationView[3];
    private final boolean[] ativos = new boolean[3];

    private final String idObjeto;

    private final Utils utils;
    private final VolleyUtils volleyUtils;
    private final Context context;

    private final UserInterface userCallback;

    public PopupRemover(Context context, String idObjeto, UserInterface userCallback) {
        this.context = context;
        this.idObjeto = idObjeto;
        this.userCallback = userCallback;

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.popup_remover);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        utils = new Utils(context);
        volleyUtils = new VolleyUtils(context);

        //listener do botão fechar
        ImageView fechar = dialog.findViewById(R.id.remover_fechar);
        fechar.setOnClickListener(v -> fecharPopup());

        //listener do botão de reportar
        Button remover = dialog.findViewById(R.id.remover_botao);
        remover.setOnClickListener(v -> remover());

        //listeners dos motivos
        dialog.findViewById(R.id.remover_1).setOnClickListener(v -> check(0));
        dialog.findViewById(R.id.remover_2).setOnClickListener(v -> check(1));
        dialog.findViewById(R.id.remover_3).setOnClickListener(v -> check(2));

        checks[0] = dialog.findViewById(R.id.remover_1_check);
        checks[1] = dialog.findViewById(R.id.remover_2_check);
        checks[2] = dialog.findViewById(R.id.remover_3_check);

    }

    public void abrirPopup() {
        dialog.show();
    }

    public void fecharPopup() {
        dialog.dismiss();
    }

    private void remover() {
        String motivos = getMotivos();
        if (motivos.length() <= 0) {
            Toast.makeText(context, context.getResources().getString(R.string.remErrorCheck), Toast.LENGTH_LONG).show();
        } else { //caso tiver motivos

            utils.abrirPopUpCarregando();

            VolleyInterface callback = new VolleyInterface() {
                @Override
                public void onResponse(String response) {
                    Toast.makeText(context, context.getResources().getString(R.string.remSuccess), Toast.LENGTH_LONG).show();
                    utils.fecharPopUpCarregando();
                    userCallback.callback(true);
                    fecharPopup();
                }

                @Override
                public void onError(String erro) {
                    
                    if (erro.equalsIgnoreCase("Email not verified.")) {
                        Toast.makeText(context, context.getResources().getString(R.string.emailRequired), Toast.LENGTH_LONG).show();
                    } else if (erro.equalsIgnoreCase("Object not found!")) {
                        Toast.makeText(context, context.getResources().getString(R.string.objNotFound), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(context, context.getResources().getString(R.string.remError), Toast.LENGTH_LONG).show();
                    }

                    utils.fecharPopUpCarregando();
                    fecharPopup();
                }
            };

            volleyUtils.removerObjeto(callback, idObjeto, motivos);
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
