package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class VolleyUtils {

    private RequestQueue queue;

    private final String urlRequisitarObjeto = "https://us-central1-apptrimonio-9844d.cloudfunctions.net/requisitarObjeto";
    private final String urlVerificarConta = "https://us-central1-apptrimonio-9844d.cloudfunctions.net/verificarConta";

    public VolleyUtils(Context context){
        this.queue = Volley.newRequestQueue(context);
    }

    private String getStringImage(Bitmap bitmap) { //transforma o bitmap em string
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    private Bitmap getBitmapImage(String image){ //transforma a string em bitmap
        try{
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        }catch (Exception e){
            Log.e("BITMAP ERROR", e.getMessage());
            return null;
        }
    }

    public void adicionarObjeto(Context contexto, VolleyInterface volleyInterface, Bitmap imagem) { //adiciona o objeto
        String stringImage = getStringImage(imagem); //transforma a imagem em string

        //envia a solicitação pro servidor
        RequestQueue queue = Volley.newRequestQueue(contexto);
    }

    public void editarObjeto() { //edita um objeto

    }

    public void removerObjeto() { //remove um objeto

    }

    public void requisitarGerenciador() { //ao clicar em tornar-se gerenciador

    }

    public void requisitarObjeto(VolleyInterface volleyInterface, String id) { //ao escanear um objeto

        //faz o request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, urlRequisitarObjeto, volleyInterface::onResponse, volleyInterface::onError){
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idObjeto", id);
                return params;
            }
        };
        queue.add(stringRequest);
    }

    public void verificarConta(VolleyInterface volleyInterface) { //ao entrar no aplicativo, verifica se é gerenciador

        //verifica se há usuário logado
        FirebaseUser usuario = FirebaseAuth.getInstance().getCurrentUser(); //pega o usuário
        if (usuario != null) { //caso houver usuário logado
            usuario.getIdToken(true).addOnCompleteListener(task -> { //pega o idToken do usuário
                if (task.isSuccessful()) { //caso deu certo

                    String idToken = Objects.requireNonNull(task.getResult()).getToken(); //idToken do usuário

                    //faz o request
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, urlVerificarConta, volleyInterface::onResponse, volleyInterface::onError){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("token", idToken);
                            return params;
                        }
                    };
                    queue.add(stringRequest);

                } else { //caso deu erro ao pegar o token
                    volleyInterface.onResponse("ERROR");
                }
            }).addOnFailureListener(e -> volleyInterface.onResponse("ERROR"));
        } else { //caso não houver usuário logado
            volleyInterface.onResponse("ERROR");
        }
    }
}
