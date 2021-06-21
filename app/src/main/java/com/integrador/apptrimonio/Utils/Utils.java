package com.integrador.apptrimonio.Utils;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.util.Util;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Locale;

public class Utils {

    private Dialog popupCarregando, popupQRCode;
    private Context context;

    //variáveis do apptrimônio
    public static String emailApptrimonio = "apptrimonio@gmail.com";

    public Utils(Context context) {

        //define o popup carregando
        this.context = context;
        popupCarregando = new Dialog(context);
        popupCarregando.setContentView(R.layout.popup_carregando);
        popupCarregando.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupCarregando.setCancelable(false);

        popupQRCode = new Dialog(context);
        popupQRCode.setContentView(R.layout.popup_qrcode);
        popupQRCode.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupQRCode.setCancelable(true);

    }

    public static String getStringImage(Bitmap bitmap) { //transforma o bitmap em string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public static Bitmap getBitmapImage(String image) { //transforma a string em bitmap
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch (Exception e) {
            Log.e("BITMAP ERROR", e.getMessage());
            return null;
        }
    }

    public static String getLanguage() { //retorna a lingua, 0 INGLES, 1 PORTUGUES, 2 ESPANHOL
        String lingua = Locale.getDefault().getLanguage();

        if (lingua.equalsIgnoreCase("pt")) {
            lingua = "1";
        } else if (lingua.equalsIgnoreCase("2")) {
            lingua = "2";
        } else {
            lingua = "0";
        }
        return lingua;
    }

    public void abrirPopUpCarregando() { //abre o popup
        popupCarregando.show();
    }

    public void fecharPopUpCarregando() { //fecha o popup
        popupCarregando.dismiss();
    }

    public void abrirPopUpQRCode(String codigo, View view) {

        //define o qr code
        ImageView viewQRCode = popupQRCode.findViewById(R.id.qrcode_qrcode);
        Bitmap qrcode = QRCode.generateQRCode(context.getResources().getString(R.string.qrcodePlaceholder).replaceAll("%CODIGO%", codigo), context);
        if (qrcode == null) {
            viewQRCode.setImageDrawable(context.getResources().getDrawable(R.drawable.semimagem));
        } else {
            viewQRCode.setImageBitmap(qrcode);
        }

        //ao clicar em download
        Button download = popupQRCode.findViewById(R.id.qrcode_download);
        download.setOnClickListener(v -> salvarQRCode(codigo, qrcode, view));

        //ao fechar
        ImageView fechar = popupQRCode.findViewById(R.id.qrcode_fechar);
        fechar.setOnClickListener(v -> fecharPopUpQRCode());

        popupQRCode.show();

    }

    private void salvarQRCode(String codigo, Bitmap bitmap, View view) {
        try {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, bytes);
            File f = new File(Environment.getExternalStorageDirectory()
                    + File.separator + codigo + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            fo.close();
            Utils.abrirSnackbar(view, context.getResources().getString(R.string.downloadSuccess));

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            Utils.abrirSnackbar(view, context.getResources().getString(R.string.downloadError));
        }
        fecharPopUpQRCode();
    }

    private void fecharPopUpQRCode() {
        popupQRCode.dismiss();
    }

    public void verificarConta(UserInterface callback) {

        //abre o popup carregando
        abrirPopUpCarregando();

        //faz o request pro servidor
        VolleyInterface volleyInterface = new VolleyInterface() { //callback
            @Override
            public void onResponse(String response) { //salva os dados do usuário

                boolean gerenciador, editar, adicionar;
                int xp;
                JSONArray objetosAdicionados = new JSONArray(), objetosVerificados = new JSONArray();
                JSONArray codigos = new JSONArray();

                if(response.equals("NO USER")){ //caso não tiver usuário logado

                    salvarDados(false, false, false, 0, new JSONArray(), new JSONArray(), new JSONArray());

                }else if (!response.equals("ERROR")) { //caso não recebeu "ERROR" do método então recebeu um JSON do servidor
                    try {
                        JSONObject objeto = new JSONObject(response);
                        gerenciador = objeto.getBoolean("gerenciador");
                        editar = objeto.getBoolean("editar");
                        adicionar = objeto.getBoolean("adicionar");
                        xp = objeto.getInt("xp");
                        codigos = objeto.getJSONArray("codigos");
                        salvarDados(gerenciador, editar, adicionar, xp, objetosAdicionados, objetosVerificados, codigos);
                    } catch (Exception e) { //caso não recebeu um JSON do servidor por algum motivo
                        e.printStackTrace();
                        salvarDados(false, false, false, 0, new JSONArray(), new JSONArray(), new JSONArray());
                    }
                }

                fecharPopUpCarregando();
                callback.callbackLogin(true);
            }

            @Override
            public void onError(VolleyError error) { //salva o gerenciador e estudante como false e fecha o popup
                fecharPopUpCarregando();
                callback.callbackLogin(false);
            }
        };

        //faz a requisitação
        new VolleyUtils(this.context).verificarConta(volleyInterface);
    }

    //salva os dados recebidos do servidor
    private void salvarDados(boolean gerenciador, boolean editar, boolean adicionar, int xp, JSONArray objetosAdicionados, JSONArray objetosVerificados, JSONArray codigos) {

        User user = User.getInstance();
        user.setPermissaoGerenciador(gerenciador);
        user.setPermissaoAdicionar(adicionar);
        user.setPermissaoEditar(editar);
        user.setXp(xp);
        user.setObjetosAdicionados(objetosAdicionados);
        user.setObjetosVerificados(objetosVerificados);
        user.setCodigos(codigos);

    }

    public static void abrirSnackbar(View view, String mensagem) {
        Snackbar.make(view, mensagem, 4000).show();
    }

}
