package com.integrador.apptrimonio.Utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Locale;

public class Utils {

    private final Dialog popupCarregando;
    private final Dialog popupQRCode;
    private final Context context;

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
        if (bitmap == null) {
            return "";
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
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

    public void abrirPopUpQRCode(String codigo, String descricaoImagem) {

        //define o qr code
        ImageView viewQRCode = popupQRCode.findViewById(R.id.qrcode_qrcode);
        Bitmap qrcode = QRCode.generateQRCode(context.getResources().getString(R.string.qrcodePlaceholder).replaceAll("%CODIGO%", codigo), context);
        if (qrcode == null) {
            viewQRCode.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.semimagem, null));
        } else {
            viewQRCode.setImageBitmap(qrcode);
        }

        //ao clicar em download
        Button download = popupQRCode.findViewById(R.id.qrcode_download);
        download.setOnClickListener(v -> salvarQRCode(codigo, qrcode, descricaoImagem));

        //ao fechar
        ImageView fechar = popupQRCode.findViewById(R.id.qrcode_fechar);
        fechar.setOnClickListener(v -> fecharPopUpQRCode());

        popupQRCode.show();

    }

    private void salvarQRCode(String codigo, Bitmap bitmap, String descricaoImagem) {
        try {

            MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, codigo + ".jpg", descricaoImagem);
            Toast.makeText(context, context.getResources().getString(R.string.downloadSuccess), Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            Toast.makeText(context, context.getResources().getString(R.string.downloadError), Toast.LENGTH_LONG).show();
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

                boolean gerenciador, editar, adicionar, receberEmails;
                int xp;
                JSONArray objetosAdicionados, objetosVerificados;
                JSONArray codigos;

                if (response.equals("NO USER")) { //caso não tiver usuário logado
                    salvarDados(false, false, false, 0, new JSONArray(), new JSONArray(), new JSONArray(), false);
                } else if (!response.equals("ERROR")) { //caso não recebeu "ERROR" do método então recebeu um JSON do servidor
                    try {
                        JSONObject objeto = new JSONObject(response);
                        gerenciador = objeto.getBoolean("gerenciador");
                        editar = objeto.getBoolean("editar");
                        adicionar = objeto.getBoolean("adicionar");
                        xp = objeto.getInt("xp");
                        codigos = objeto.getJSONArray("codigos");
                        receberEmails = objeto.getBoolean("receberEmails");
                        objetosAdicionados = objeto.getJSONArray("objetosAdicionados");
                        objetosVerificados = objeto.getJSONArray("objetosVerificados");
                        salvarDados(gerenciador, editar, adicionar, xp, objetosAdicionados, objetosVerificados, codigos, receberEmails);
                    } catch (Exception e) { //caso não recebeu um JSON do servidor por algum motivo
                        e.printStackTrace();
                        salvarDados(false, false, false, 0, new JSONArray(), new JSONArray(), new JSONArray(), false);
                    }
                }

                fecharPopUpCarregando();
                callback.callback(true);
            }

            @Override
            public void onError(String erro) { //salva o gerenciador e estudante como false e fecha o popup
                fecharPopUpCarregando();
                callback.callback(false);
            }
        };

        //faz a requisitação
        new VolleyUtils(this.context).verificarConta(volleyInterface);
    }

    //salva os dados recebidos do servidor
    private void salvarDados(boolean gerenciador, boolean editar, boolean adicionar, int xp, JSONArray objetosAdicionados, JSONArray objetosVerificados, JSONArray codigos, boolean receberEmails) {

        User user = User.getInstance();
        user.setPermissaoGerenciador(gerenciador);
        user.setPermissaoAdicionar(adicionar);
        user.setPermissaoEditar(editar);
        user.setXp(xp);
        user.setObjetosAdicionados(objetosAdicionados);
        user.setObjetosVerificados(objetosVerificados);
        user.setCodigos(codigos);
        user.setEmail(FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : null);
        user.setReceberEmails(receberEmails);

    }

    public static void makeSnackbar(String mensagem, View view) {

        boolean email = view.getContext().getResources().getString(R.string.emailRequired).equalsIgnoreCase(mensagem);

        Snackbar snackbar = Snackbar.make(view, mensagem, email ? Snackbar.LENGTH_INDEFINITE : Snackbar.LENGTH_LONG);
        ((TextView) snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text)).setMaxLines(5);

        //caso precisar de confirmação de email adicionar botão
        if (email) {
            snackbar.setAction(R.string.confirm, v -> {
                snackbar.dismiss();
                VolleyInterface volleyInterface = new VolleyInterface() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(view.getContext(), view.getContext().getResources().getString(R.string.confirmSuccess), Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(String error) {
                        Toast.makeText(view.getContext(), view.getContext().getResources().getString(R.string.confirmError), Toast.LENGTH_LONG).show();
                    }
                };

                new VolleyUtils(view.getContext()).enviarEmailConfirmacao(volleyInterface);
            });
        }

        snackbar.show();
    }

    public static Bitmap comprimirImagem(Bitmap realImage) {

        if (realImage == null) {
            return null;
        }

        float ratio = Math.min(
                (float) 1280.0 / realImage.getWidth(),
                (float) 1280.0 / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    public Drawable getDrawable(int drawable) {
        return ResourcesCompat.getDrawable(context.getResources(), drawable, null);
    }

}
