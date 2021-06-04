package com.integrador.apptrimonio.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;

import com.android.volley.VolleyError;
import com.bumptech.glide.util.Util;
import com.integrador.apptrimonio.R;

import org.json.JSONArray;
import org.json.JSONObject;

public class Utils {

    private Dialog popupCarregando;
    private Context context;
    private SharedPreferences sharedPreferences;

    public Utils(Context context){

        //define o popup carregando
        this.context = context;
        sharedPreferences = context.getSharedPreferences("apptrimonio", Context.MODE_PRIVATE);
        popupCarregando = new Dialog(context);
        popupCarregando.setContentView(R.layout.popup_carregando);
        popupCarregando.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupCarregando.setCancelable(false);

    }

    private void abrirPopUpCarregando(){ //abre o popup
        popupCarregando.show();
    }

    private void fecharPopUpCarregando(){ //fecha o popup
        popupCarregando.dismiss();
    }

    public void verificarConta(){

        //abre o popup carregando
        abrirPopUpCarregando();

        //faz o request pro servidor
        VolleyInterface volleyInterface = new VolleyInterface() { //callback
            @Override
            public void onResponse(String response) { //salva os dados do usuário

                boolean gerenciador, editar, adicionar;
                int xp = 0, objetosAdicionados = 0, objetosVerificados = 0;
                JSONArray codigos = new JSONArray();

                if(response != "ERROR"){ //caso não recebeu "ERROR" do método então recebeu um JSON do servidor
                    try {
                        JSONObject objeto = new JSONObject(response);
                        gerenciador = objeto.getBoolean("gerenciador");
                        editar = objeto.getBoolean("editar");
                        adicionar = objeto.getBoolean("adicionar");
                        xp = objeto.getInt("xp");
                        codigos = objeto.getJSONArray("codigos");
                        salvarDados(gerenciador, editar, adicionar, xp, objetosAdicionados, objetosVerificados, codigos);
                    }catch (Exception e){ //caso não recebeu um JSON do servidor por algum motivo
                        e.printStackTrace();
                        salvarDados(false, false, false, 0, 0, 0, new JSONArray());
                    }
                }

                fecharPopUpCarregando();
            }

            @Override
            public void onError(VolleyError error) { //salva o gerenciador e estudante como false e fecha o popup
                fecharPopUpCarregando();
            }
        };

        //faz a verificação da conta

        new VolleyUtils(this.context).verificarConta(volleyInterface);
    }

    //salva os dados recebidos do servidor
    private void salvarDados(boolean gerenciador, boolean editar, boolean adicionar, int xp, int objetosAdicionados, int objetosVerificados, JSONArray codigos){

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("gerenciador", gerenciador); //salva o gerenciador
        editor.putBoolean("editar", editar); //salva o editar
        editor.putBoolean("adicionar", adicionar); //salva o adicionar
        editor.putInt("xp", xp); //salva o xp
        editor.putInt("objetosAdicionados", objetosAdicionados); //salva o objetosAdicionados
        editor.putInt("objetosVerificados", objetosVerificados); //salva os objetosVerificados
        editor.putString("codigos", codigos.toString()); //salva os codigos
        editor.apply();

    }
}
