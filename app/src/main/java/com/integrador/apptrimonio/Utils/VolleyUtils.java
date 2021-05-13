package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.net.Uri;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class VolleyUtils {

    public static void adicionarObjeto() { //adiciona o objeto

    }

    public static void editarObjeto() { //edita um objeto

    }

    public static void removerObjeto() { //remove um objeto

    }

    public static void requisitarGerenciador() { //ao clicar em tornar-se gerenciador

    }

    public static void requisitarObjeto(Context context, VolleyInterface volleyInterface, String id) { //ao escanear um objeto
        //envia solicitação pro servidor verificar se existe o objeto
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://us-central1-apptrimonio-9844d.cloudfunctions.net/requisitarObjeto";

        //adiciona query
        Uri.Builder builder = Uri.parse(url).buildUpon();
        builder.appendQueryParameter("idObjeto", id);
        url = builder.build().toString();

        //faz o request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, volleyInterface::onResponse, volleyInterface::onError);
        queue.add(stringRequest);
    }

    public static void verificarConta(Context context, VolleyInterface volleyInterface) { //ao entrar no aplicativo, verifica se é gerenciador

        //verifica se há usuário logado
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser();
        if (usuario != null) { //caso houver usuário logado
            usuario.getIdToken(true).addOnCompleteListener(task -> { //pega o idToken do usuário
                if (task.isSuccessful()) { //caso deu certo
                    String idToken = Objects.requireNonNull(task.getResult()).getToken();

                    //envia solicitação pro servidor verificar se é gerenciador ou não
                    RequestQueue queue = Volley.newRequestQueue(context);
                    String url = "https://us-central1-apptrimonio-9844d.cloudfunctions.net/verificarConta";

                    //adiciona query
                    Uri.Builder builder = Uri.parse(url).buildUpon();
                    builder.appendQueryParameter("token", idToken);
                    url = builder.build().toString();

                    //faz o request
                    StringRequest stringRequest = new StringRequest(Request.Method.GET, url, volleyInterface::onResponse, volleyInterface::onError);
                    queue.add(stringRequest);
                } else { //caso deu erro
                    volleyInterface.onResponse("false");
                }
            }).addOnFailureListener(e -> {
                volleyInterface.onResponse("false");
            });
        } else { //caso não houver usuário logado
            volleyInterface.onResponse("false");
        }
    }
}
