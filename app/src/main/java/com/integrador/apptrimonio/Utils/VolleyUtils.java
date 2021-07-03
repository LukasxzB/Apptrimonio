package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VolleyUtils {

    private final RequestQueue queue;

    private final String urlFirebase = "https://us-central1-apptrimonio-9844d.cloudfunctions.net/";

    public VolleyUtils(Context context) {
        this.queue = Volley.newRequestQueue(context);
    }

    private void fazerRequest(VolleyInterface volleyInterface, String url, FirebaseUser user, final Map<String, String> params, String lingua) {
        lingua = lingua == null ? Utils.getLanguage() : lingua;
        //verifica se há usuário logado
        if (user != null) { //caso houver usuário logado
            String finalLingua = lingua;
            user.getIdToken(false).addOnCompleteListener(task -> { //pega o idToken do usuário
                if (task.isSuccessful()) { //caso deu certo

                    String idToken = Objects.requireNonNull(task.getResult()).getToken(); //idToken do usuário

                    //faz o request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlFirebase + url, volleyInterface::onResponse, volleyInterface::onError) {
                        @Override
                        protected Map<String, String> getParams() {
                            params.put("token", idToken);
                            params.put("lingua", finalLingua);
                            return params;
                        }

                        @Override
                        public Map<String, String> getHeaders() {
                            Map<String, String> headers = new HashMap<>();
                            headers.put("Content-Type", "application/x-www-form-urlencoded");
                            return headers;
                        }
                    };
                    stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                            (int) TimeUnit.SECONDS.toMillis(30), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                    queue.add(stringRequest);
                } else { //caso deu erro ao pegar o token
                    volleyInterface.onResponse("ERROR");
                }
            }).addOnFailureListener(e -> volleyInterface.onResponse("ERROR"));
        } else { //caso não houver usuário logado
            volleyInterface.onResponse("NO USER");
        }
    }

    private void fazerRequest(VolleyInterface volleyInterface, String url, final Map<String, String> params, String lingua) {
        lingua = lingua == null ? Utils.getLanguage() : lingua;
        //faz o request
        String finalLingua = lingua;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlFirebase + url, volleyInterface::onResponse, volleyInterface::onError) {
            @Override
            protected Map<String, String> getParams() {
                params.put("lingua", finalLingua);
                return params;
            }


            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/x-www-form-urlencoded");
                return headers;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                (int) TimeUnit.SECONDS.toMillis(30), 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(stringRequest);
    }

    public void enviarEmailConfirmacao(VolleyInterface volleyInterface) { //enviar e-mail para confirmar a conta, LINGUA 0 = EN, 1 = PT, 2 = ES

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fazerRequest(volleyInterface, "enviarEmailConfirmacao", user, new HashMap<>(), null);

    }

    public void receberEmails(VolleyInterface volleyInterface, boolean status) { //caso usuário queira receber e-mails

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, String> params = new HashMap<>();
        params.put("status", String.valueOf(status));
        fazerRequest(volleyInterface, "receberEmails", user, params, null);

    }

    public void requisitarObjeto(VolleyInterface volleyInterface, String idObjeto, FirebaseUser user) { //ao escanear um objeto (user é utilizado para adicionar xp)

        Map<String, String> params = new HashMap<>();
        params.put("idObjeto", idObjeto);

        //seleciona qual request vai fazer, caso tiver ou não tiver usuário
        if (user != null) {
            fazerRequest(volleyInterface, "requisitarObjeto", user, params, null);
        } else {
            fazerRequest(volleyInterface, "requisitarObjeto", params, null);
        }
    }

    public void verificarConta(VolleyInterface volleyInterface) { //ao entrar no aplicativo, verifica se é gerenciador

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fazerRequest(volleyInterface, "verificarConta", user, new HashMap<>(), null);
    }

    public void adicionarObjeto(VolleyInterface volleyInterface, String nome, String categoria, String descricao, Date dataCompra, double valor, String local, String descricaoImagem, String valorSentimental, Bitmap imagem, String lingua) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String imagemFinal = imagem == null ? "" : Utils.getStringImage(imagem);

        Map<String, String> params = new HashMap<>();
        params.put("imagem", imagemFinal);
        params.put("nome", nome);
        params.put("categoria", categoria);
        params.put("descricao", descricao);
        params.put("dataCompra", dataCompra == null ? "0" : Long.toString(dataCompra.getTime()));
        params.put("valor", Double.toString(valor));
        params.put("local", local);
        params.put("descricaoImagem", descricaoImagem);
        params.put("valorSentimental", valorSentimental);

        fazerRequest(volleyInterface, "adicionarObjeto", user, params, lingua);
    }

    public void editarObjeto(VolleyInterface volleyInterface, String idObjeto, String descricao, Bitmap imagem, String categoria, Date dataCompra, String descricaoImagem, String local, String nome, double valor, String valorSentimental, String lingua) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String imagemFinal = imagem == null ? null : Utils.getStringImage(imagem);

        Map<String, String> params = new HashMap<>();
        params.put("idObjeto", idObjeto);
        params.put("imagem", imagemFinal);
        params.put("nome", nome);
        params.put("descricao", descricao);
        params.put("categoria", categoria);
        params.put("dataCompra", Long.toString(dataCompra.getTime()));
        params.put("valor", Double.toString(valor));
        params.put("local", local);
        params.put("descricaoImagem", descricaoImagem);
        params.put("valorSentimental", valorSentimental);

        fazerRequest(volleyInterface, "editarObjeto", user, params, lingua);
    }

    public void removerObjeto(VolleyInterface volleyInterface, String idAndamento, String idObjeto, String motivos) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> params = new HashMap<>();
        params.put("idAndamento", idAndamento);
        params.put("idObjeto", idObjeto);
        params.put("motivo", motivos);
        fazerRequest(volleyInterface, "removerObjeto", user, params, null);
    }

    public void removerObjeto(VolleyInterface volleyInterface, String idObjeto, String motivos) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> params = new HashMap<>();
        params.put("idObjeto", idObjeto);
        params.put("motivo", motivos);
        fazerRequest(volleyInterface, "removerObjeto", user, params, null);
    }

    public void objetosAndamento(VolleyInterface volleyInterface) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fazerRequest(volleyInterface, "objetosAndamento", user, new HashMap<>(), null);
    }

    public void aprovacaoObjeto(VolleyInterface volleyInterface, boolean status, String idAndamento, String descricao, String motivo, Bitmap imagem, String categoria, Date dataCompra, String descricaoImagem, String local, String nome, double valor, String valorSentimental, String lingua) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String imagemFinal = imagem == null ? null : Utils.getStringImage(imagem);

        JSONObject object = new JSONObject();
        try {
            object.put("imagem", imagemFinal);
            object.put("categoria", categoria);
            object.put("compra", Long.toString(dataCompra.getTime()));
            object.put("descricaoImagem", descricaoImagem);
            object.put("local", local);
            object.put("descricao", descricao);
            object.put("nome", nome);
            object.put("valor", Double.toString(valor));
            object.put("valorSentimental", valorSentimental);

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

        Map<String, String> params = new HashMap<>();
        params.put("status", String.valueOf(status));
        params.put("idAndamento", idAndamento);
        params.put("motivo", motivo);
        params.put("valoresAprovar", object.toString());

        fazerRequest(volleyInterface, "aprovacaoObjeto", user, params, lingua);
    }

    public void reportarObjeto(VolleyInterface volleyInterface, String idObjeto, String motivo) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> params = new HashMap<>();
        params.put("idObjeto", idObjeto);
        params.put("motivo", motivo);

        fazerRequest(volleyInterface, "reportarObjeto", user, params, null);
    }

}
