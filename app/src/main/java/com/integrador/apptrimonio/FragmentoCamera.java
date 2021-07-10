package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.zxing.ResultPoint;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.CompoundBarcodeView;

import org.json.JSONObject;

import java.util.List;

public class FragmentoCamera extends Fragment {

    private final Context context;
    private CompoundBarcodeView scannerView;
    private boolean carregandoCodigo = false;

    private final VolleyUtils volleyUtils;
    private final Dialog popupCarregando;
    private final PopupCodigoInvalido popupCodigoInvalido;

    public FragmentoCamera(Context context) {
        this.context = context;
        popupCarregando = new Dialog(context);
        popupCarregando.setContentView(R.layout.popup_carregando);
        popupCarregando.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupCarregando.setCancelable(false);

        volleyUtils = new VolleyUtils(context);
        popupCodigoInvalido = new PopupCodigoInvalido(context);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_fragmento_camera, container, false);

        scannerView = view.findViewById(R.id.camera_scanner); //pega o scanner view
        scannerView.decodeContinuous(scannerCallback); //define o callback
        scannerView.getChildAt(2).setVisibility(View.INVISIBLE); //remove o texto orginal

        ImageView swipeUp = view.findViewById(R.id.camera_swipe);
        swipeUp.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            if (fragmentManager.getBackStackEntryCount() > 0) {
                fragmentManager.popBackStack();
            }
        });
        Glide.with(this).load(R.drawable.swipeup).into(swipeUp);
        swipeUp.setRotationX(180f);

        return view;
    }

    private final BarcodeCallback scannerCallback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (!carregandoCodigo) { //boolean pra evitar que o código seja escaneado várias vezes ao mesmo tempo
                carregandoCodigo = true; //irá rodar apenas uma vez, depois de concluir a pesquisa irá rodar novamente

                String resultado = result.getText();
                String codigo = resultado;

                boolean valido = true;

                if (!resultado.contains("#$") || !resultado.contains("$#") || !resultado.contains("apptrimonio-")) { //caso não conter #$ $# e apptrimonio
                    valido = false;
                }

                try {
                    codigo = resultado.substring(resultado.indexOf("#$"), resultado.lastIndexOf("$#")).replace("#$apptrimonio-", "");
                } catch (Exception e) {
                    valido = false;
                }

                //caso o código não for válido abre o popup de código não válido
                if (!valido) {
                    abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.invCode), getResources().getString(R.string.invCodeDesc), getResources().getString(R.string.tryAgain));
                } else { //caso for válido requisita o objeto
                    requisitarObjeto(codigo);
                }
            }
        }

        @Override
        public void possibleResultPoints(List<ResultPoint> resultPoints) {
        }
    };

    private void abrirPopupCarregando() {
        popupCarregando.show();
    }

    private void fecharPopupCarregando() {
        popupCarregando.dismiss();
    }

    private void requisitarObjeto(String codigo) {
        abrirPopupCarregando();

        VolleyInterface callback = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                fecharPopupCarregando();
                try {
                    //recebe o objeto da response e abre a tela do objeto
                    JSONObject objeto = new JSONObject(response);
                    abrirTelaObjeto(objeto, codigo);
                } catch (Exception e) {
                    //caso der erro mostra o popup de erro
                    Log.e("ERROR", "" + e.getMessage());
                    abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.errCode), getResources().getString(R.string.errCodeDesc), getResources().getString(R.string.tryAgainLater));
                }
            }

            @Override
            public void onError(String erro) {
                fecharPopupCarregando();

                //verifica se foi erro de internet
                if (erro.equalsIgnoreCase("network")) {
                    abrirPopupCodigoInvalido(R.raw.internet, false, getResources().getString(R.string.intCode), getResources().getString(R.string.intCodeDesc), getResources().getString(R.string.tryAgainLater));
                } else {
                    //caso não for internet
                    try {
                        JSONObject mensagemErro = new JSONObject(erro);
                        String status = mensagemErro.getString("status");

                        if (status.equalsIgnoreCase("Object not found!")) {
                            abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.invCode), getResources().getString(R.string.invCodeDesc), getResources().getString(R.string.tryAgain));
                        } else if (status.equalsIgnoreCase("desaprovado")) {
                            abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.disCode), getResources().getString(R.string.disCodeDesc), getResources().getString(R.string.tryAgainLater));
                        } else if (status.equalsIgnoreCase("andamento")) {
                            abrirPopupCodigoInvalido(R.raw.analyzing, true, getResources().getString(R.string.andCode), getResources().getString(R.string.andCodeDesc), getResources().getString(R.string.tryAgainLater));
                        } else if (status.equalsIgnoreCase("excluido")) {

                            //infelizmente tive que colocar % nas letras pq esqueci e agora ja ta pronto fazer oq né
                            String motivo = mensagemErro.getString("motivo");
                            motivo = motivo.replaceAll("a", "%a%").replaceAll("b", "%b%").replaceAll("c", "%c%");
                            motivo = motivo.replaceAll("%a%", getResources().getString(R.string.repDoesNotExists)).replaceAll("%b%", getResources().getString(R.string.repSpam)).replaceAll("%c%", getResources().getString(R.string.other));

                            abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.remCode), getResources().getString(R.string.remCodeDesc) + motivo, getResources().getString(R.string.tryAgainLater));
                        } else {
                            abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.errCode), getResources().getString(R.string.errCodeDesc), getResources().getString(R.string.tryAgain));
                        }

                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                        abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.errCode), getResources().getString(R.string.errCodeDesc), getResources().getString(R.string.tryAgain));
                    }
                }
            }
        };

        volleyUtils.requisitarObjeto(callback, codigo, FirebaseAuth.getInstance().getCurrentUser());
    }

    private void abrirTelaObjeto(JSONObject objeto, String codigo) {
        try {
            String nome = objeto.has("nome") ? objeto.getString("nome").trim() : "";
            String descricao = objeto.has("descricao") ? objeto.getString("descricao").trim() : "";
            String imagem = objeto.has("imagem") ? objeto.getString("imagem") : "";
            String lingua = objeto.has("lingua") ? objeto.getString("lingua") : "";
            String categoria = objeto.has("categoria") ? objeto.getString("categoria") : "";
            String descricaoImagem = objeto.has("descricaoImagem") ? objeto.getString("descricaoImagem") : "";
            String local = objeto.has("local") ? objeto.getString("local").trim() : "";
            double valor = objeto.has("valor") ? objeto.getDouble("valor") : 0.0;
            String valorSentimental = objeto.has("valorSentimental") ? objeto.getString("valorSentimental") : "";
            User.getInstance().adicionarObjetoEscaneado(codigo);

            Intent intent = new Intent(context, Objeto.class);
            Bundle bundle = new Bundle();
            bundle.putString("codigo", codigo);
            bundle.putString("imagem", imagem);
            bundle.putString("nome", nome);
            bundle.putString("lingua", lingua);
            bundle.putString("categoria", categoria);
            bundle.putString("descricaoImagem", descricaoImagem);
            bundle.putString("descricao", descricao);
            bundle.putString("local", local);
            bundle.putDouble("valor", valor);
            bundle.putString("valorSentimental", valorSentimental);

            //verifica se tem data de compra
            try {
                JSONObject dataCompra = objeto.has("compra") ? new JSONObject(objeto.getString("compra")) : null;
                assert dataCompra != null;
                long compraLong = Long.parseLong(dataCompra.getString("_seconds"));
                bundle.putLong("compra", compraLong * 1000);
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }

            //verifica se tem data de publicação
            try {
                JSONObject dataPub = objeto.has("_aprovadoEm") ? new JSONObject(objeto.getString("_aprovadoEm")) : null;
                assert dataPub != null;
                long publicacaoLong = Long.parseLong(dataPub.getString("_seconds"));
                bundle.putLong("dataPublicacao", publicacaoLong * 1000);
            } catch (Exception e) {
                Log.d("ERROR", e.getMessage());
            }

            intent.putExtras(bundle);
            startActivity(intent);
            carregandoCodigo = false;

        } catch (Exception e) {
            Log.e("ERROR", e.getMessage());
            abrirPopupCodigoInvalido(R.raw.error, false, getResources().getString(R.string.errCode), getResources().getString(R.string.errCodeDesc), getResources().getString(R.string.tryAgainLater));
        }
    }

    private void abrirPopupCodigoInvalido(int rawRes, boolean loop, String titulo, String descricao, String tenteNovamente) {

        View.OnClickListener listener = v -> fecharPopupErro();
        popupCodigoInvalido.abrirPopupCodigoInvalido(rawRes, loop, titulo, descricao, tenteNovamente, listener);
    }

    private void fecharPopupErro() {
        carregandoCodigo = false;
        popupCodigoInvalido.fecharPopupCodigoInvalido();
    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.resume();
    }

    @Override
    public void onPause() {
        super.onPause();
        scannerView.pause();
    }
}