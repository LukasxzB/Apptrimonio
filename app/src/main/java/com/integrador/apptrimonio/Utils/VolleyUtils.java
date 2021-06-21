package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

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

public class VolleyUtils {

    private final RequestQueue queue;

    private final String url = "https://us-central1-apptrimonio-9844d.cloudfunctions.net/";

    public VolleyUtils(Context context) {
        this.queue = Volley.newRequestQueue(context);
    }

    private void fazerRequest(VolleyInterface volleyInterface, String url, FirebaseUser user, final Map<String, String> params) {
        //verifica se há usuário logado
        if (user != null) { //caso houver usuário logado
            user.getIdToken(false).addOnCompleteListener(task -> { //pega o idToken do usuário
                if (task.isSuccessful()) { //caso deu certo

                    String idToken = Objects.requireNonNull(task.getResult()).getToken(); //idToken do usuário

                    //faz o request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, url, volleyInterface::onResponse, volleyInterface::onError) {
                        @Override
                        protected Map<String, String> getParams() {
                            params.put("token", idToken);
                            params.put("lingua", Utils.getLanguage());
                            return params;
                        }
                    };
                    queue.add(stringRequest);
                } else { //caso deu erro ao pegar o token
                    volleyInterface.onResponse("ERROR");
                }
            }).addOnFailureListener(e -> volleyInterface.onResponse("ERROR"));
        } else { //caso não houver usuário logado
            volleyInterface.onResponse("NO USER");
        }
    }

    private void fazerRequest(VolleyInterface volleyInterface, String url, final Map<String, String> params) {
        //faz o request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, volleyInterface::onResponse, volleyInterface::onError) {
            @Override
            protected Map<String, String> getParams() {
                params.put("lingua", Utils.getLanguage());
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void enviarEmailConfirmacao(VolleyInterface volleyInterface) { //enviar e-mail para confirmar a conta, LINGUA 0 = EN, 1 = PT, 2 = ES

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fazerRequest(volleyInterface, url + "enviarEmailConfirmacao", user, new HashMap<>());

    }

    public void receberEmails(VolleyInterface volleyInterface, boolean status) { //caso usuário queira receber e-mails

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, String> params = new HashMap<>();
        params.put("status", String.valueOf(status));
        fazerRequest(volleyInterface, url + "receberEmails", user, params);

    }

    public void requisitarObjeto(VolleyInterface volleyInterface, String idObjeto, FirebaseUser user) { //ao escanear um objeto (user é utilizado para adicionar xp)

        Map<String, String> params = new HashMap<>();
        params.put("idObjeto", idObjeto);

        //seleciona qual request vai fazer, caso tiver ou não tiver usuário
        if (user != null) {
            fazerRequest(volleyInterface, url + "requisitarObjeto", user, params);
        } else {
            fazerRequest(volleyInterface, url + "requisitarObjeto", params);
        }
    }

    public void verificarConta(VolleyInterface volleyInterface) { //ao entrar no aplicativo, verifica se é gerenciador

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        fazerRequest(volleyInterface, url + "verificarConta", user, new HashMap<>());
    }

    public void adicionarObjeto(VolleyInterface volleyInterface, String nome, String categoria, String descricao, Date dataCompra, double valor, String local, String descricaoImagem, String valorSentimental, Bitmap imagem) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String imagemFinal = imagem == null ? null : Utils.getStringImage(imagem);

        Map<String, String> params = new HashMap<>();
        params.put("imagem", imagemFinal);
        params.put("nome", nome);
        params.put("categoria", categoria);
        params.put("descricao", descricao);
        params.put("dataCompra", Long.toString(dataCompra.getTime()));
        params.put("valor", Double.toString(valor));
        params.put("local", local);
        params.put("descricaoImagem", descricaoImagem);
        params.put("valorSentimental", valorSentimental);

        fazerRequest(volleyInterface, url + "adicionarObjeto", user, params);
    }

    public void editarObjeto(VolleyInterface volleyInterface, String idObjeto, String descricao, Bitmap imagem, String categoria, Date dataCompra, String descricaoImagem, String local, String nome, double valor, String valorSentimental) {
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

        fazerRequest(volleyInterface, url + "editarObjeto", user, params);
    }

    public void removerObjeto(VolleyInterface volleyInterface, String idAndamento, String idObjeto, String motivos) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> params = new HashMap<>();
        params.put("idAndamento", idAndamento);
        params.put("idObjeto", idObjeto);
        params.put("motivo", motivos);
        fazerRequest(volleyInterface, url + "removerObjeto", user, params);
    }

    public void objetosAndamento(VolleyInterface volleyInterface){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        fazerRequest(volleyInterface, url + "objetosAndamento", user, new HashMap<>());
    }

    public void aprovacaoObjeto(VolleyInterface volleyInterface, boolean status, String idAndamento, String descricao, String motivo, Bitmap imagem, String categoria, Date dataCompra, String descricaoImagem, String local, String nome, double valor, String valorSentimental){
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

        fazerRequest(volleyInterface, url + "aprovacaoObjeto", user, params);
    }

    public void reportarObjeto(VolleyInterface volleyInterface, String idObjeto, String motivo){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Map<String, String> params = new HashMap<>();
        params.put("idObjeto", idObjeto);
        params.put("motivo", motivo);

        fazerRequest(volleyInterface, url + "reportarObjeto", user, params);
    }

}
