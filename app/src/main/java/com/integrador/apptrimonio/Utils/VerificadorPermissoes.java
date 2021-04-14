package com.integrador.apptrimonio.Utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.integrador.apptrimonio.R;

import java.util.ArrayList;
import java.util.List;

public class VerificadorPermissoes {

    private static final String[] permissoesNecessarias = {"android.permission.CAMERA"};

    public static void verificarPermissoes(Activity activity) { //verifica as permissões caso foram aceitas e pede pra aceitar

        List<String> permissoesNaoAceitas = new ArrayList<>();

        for (String permissoesNecessaria : permissoesNecessarias) { //loop em todas as permissoes necessarias
            int perm = ContextCompat.checkSelfPermission(activity, permissoesNecessaria); //pega o ID da permissao
            if (perm != PackageManager.PERMISSION_GRANTED) { //caso a permissao nao estiver concedida
                permissoesNaoAceitas.add(permissoesNecessaria); //adiciona na lista de permissoes nao aceitas
            }
        }

        //verifica caso há alguma permissão que não foi aceita ainda e abre o popup
        if (!permissoesNaoAceitas.isEmpty()) {
            abrirPopup(permissoesNaoAceitas, activity);
        }
    }

    private static void abrirPopup(List<String> perms, Activity activity) {
        Dialog dialog = new Dialog(activity);
        dialog.setContentView(R.layout.popup_ativarpermissao);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.show();

        Button aceitarPerm = dialog.findViewById(R.id.ativarpermissao_botao);
        aceitarPerm.setOnClickListener(v -> {
            ActivityCompat.requestPermissions(activity, perms.toArray(new String[0]), 1);
            dialog.dismiss();
        });
    }
}

