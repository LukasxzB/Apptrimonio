package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.integrador.apptrimonio.Objeto;
import com.integrador.apptrimonio.PopupCodigoInvalido;
import com.integrador.apptrimonio.R;

import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;

public class GerenciarObjetoAdicionadoItemAdapter extends RecyclerView.Adapter<GerenciarObjetoAdicionadoItemAdapter.ViewHolder> {

    private final ArrayList<GerenciarObjetoAdicionadoItem> listaObjetosAdicionados;
    private final Context context;
    private final Utils utils;
    private final VolleyUtils volleyUtils;
    private final PopupCodigoInvalido popupCodigoInvalido;

    public GerenciarObjetoAdicionadoItemAdapter(ArrayList<GerenciarObjetoAdicionadoItem> listaObjetosAdicionados, Context context) {
        this.listaObjetosAdicionados = listaObjetosAdicionados;
        this.context = context;
        utils = new Utils(context);
        volleyUtils = new VolleyUtils(context);
        popupCodigoInvalido = new PopupCodigoInvalido(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.item_gerenciarobjetos_objetoadicionado, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String nomeTxt = listaObjetosAdicionados.get(position).getNome();
        holder.nome.setText(nomeTxt.equals("") ? context.getResources().getString(R.string.naoInfo) : nomeTxt);
        holder.titulo.setText(MessageFormat.format("{0} #{1}", context.getResources().getString(R.string.object), listaObjetosAdicionados.get(position).getNumero()));
        holder.ver.setOnClickListener(v -> abrirTelaObjeto(position));
        holder.qrcode.setOnClickListener(v -> utils.abrirPopUpQRCode(listaObjetosAdicionados.get(position).getIdObjeto(), ""));
        holder.linha.setVisibility(listaObjetosAdicionados.get(position).isUltimo() ? View.GONE : View.VISIBLE);
    }

    @Override
    public int getItemCount() {
        return listaObjetosAdicionados.size();
    }

    private void abrirTelaObjeto(final int position) {
        utils.abrirPopUpCarregando();

        VolleyInterface callback = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                utils.fecharPopUpCarregando();
                try {
                    //recebe o objeto da response e abre a tela do objeto
                    JSONObject objeto = new JSONObject(response);

                    String nome = objeto.has("nome") ? objeto.getString("nome").trim() : "";
                    String descricao = objeto.has("descricao") ? objeto.getString("descricao").trim() : "";
                    String imagem = objeto.has("imagem") ? objeto.getString("imagem") : "";
                    String lingua = objeto.has("lingua") ? objeto.getString("lingua") : "";
                    String categoria = objeto.has("categoria") ? objeto.getString("categoria") : "";
                    String descricaoImagem = objeto.has("descricaoImagem") ? objeto.getString("descricaoImagem") : "";
                    String local = objeto.has("local") ? objeto.getString("local").trim() : "";
                    double valor = objeto.has("valor") ? objeto.getDouble("valor") : 0.0;
                    String valorSentimental = objeto.has("valorSentimental") ? objeto.getString("valorSentimental") : "";
                    User.getInstance().adicionarObjetoEscaneado(listaObjetosAdicionados.get(position).getIdObjeto());

                    Intent intent = new Intent(context, Objeto.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("codigo", listaObjetosAdicionados.get(position).getIdObjeto());
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
                    context.startActivity(intent);

                } catch (Exception e) {
                    //caso der erro mostra o popup de erro
                    Log.e("ERROR", "" + e.getMessage());
                    popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.error, false, context.getResources().getString(R.string.errCode), context.getResources().getString(R.string.errCodeDesc), context.getResources().getString(R.string.tryAgainLater), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                }
            }

            @Override
            public void onError(String erro) {
                utils.fecharPopUpCarregando();

                //verifica se foi erro de internet
                if (erro.equalsIgnoreCase("network")) {
                    popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.internet, false, context.getResources().getString(R.string.intCode), context.getResources().getString(R.string.intCodeDesc), context.getResources().getString(R.string.tryAgainLater), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                } else {
                    //caso não for internet
                    try {
                        JSONObject mensagemErro = new JSONObject(erro);
                        String status = mensagemErro.getString("status");

                        if (status.equalsIgnoreCase("Object not found!")) {
                            popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.error, false, context.getResources().getString(R.string.invCode), context.getResources().getString(R.string.invCodeDesc), context.getResources().getString(R.string.tryAgain), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                        } else if (status.equalsIgnoreCase("desaprovado")) {
                            popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.error, false, context.getResources().getString(R.string.disCode), context.getResources().getString(R.string.disCodeDesc), context.getResources().getString(R.string.tryAgainLater), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                        } else if (status.equalsIgnoreCase("andamento")) {
                            popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.analyzing, true, context.getResources().getString(R.string.andCode), context.getResources().getString(R.string.andCodeDesc), context.getResources().getString(R.string.tryAgainLater), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                        } else if (status.equalsIgnoreCase("excluido")) {

                            //infelizmente tive que colocar % nas letras pq esqueci e agora ja ta pronto fazer oq né
                            String motivo = mensagemErro.getString("motivo");
                            motivo = motivo.replaceAll("a", "%a%").replaceAll("b", "%b%").replaceAll("c", "%c%");
                            motivo = motivo.replaceAll("%a%", context.getResources().getString(R.string.repDoesNotExists)).replaceAll("%b%", context.getResources().getString(R.string.repSpam)).replaceAll("%c%", context.getResources().getString(R.string.other));

                            popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.error, false, context.getResources().getString(R.string.remCode), context.getResources().getString(R.string.remCodeDesc) + motivo, context.getResources().getString(R.string.tryAgainLater), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                        } else {
                            popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.error, false, context.getResources().getString(R.string.errCode), context.getResources().getString(R.string.errCodeDesc), context.getResources().getString(R.string.tryAgain), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                        }

                    } catch (Exception e) {
                        Log.e("ERROR", e.getMessage());
                        popupCodigoInvalido.abrirPopupCodigoInvalido(R.raw.error, false, context.getResources().getString(R.string.errCode), context.getResources().getString(R.string.errCodeDesc), context.getResources().getString(R.string.tryAgain), v -> popupCodigoInvalido.fecharPopupCodigoInvalido());
                    }
                }
            }
        };

        volleyUtils.requisitarObjeto(callback, listaObjetosAdicionados.get(position).getIdObjeto(), null);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView titulo, nome;
        private final ImageView ver, qrcode, linha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            titulo = itemView.findViewById(R.id.item_gerenciarobjetos_objetoadicionado_titulo);
            nome = itemView.findViewById(R.id.item_gerenciarobjetos_objetoadicionado_nome);
            qrcode = itemView.findViewById(R.id.item_gerenciarobjetos_objetoadicionado_qrcode);
            ver = itemView.findViewById(R.id.item_gerenciarobjetos_objetoadicionado_ver);
            linha = itemView.findViewById(R.id.item_gerenciarobjetos_linha);

        }
    }


}
