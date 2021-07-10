package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.integrador.apptrimonio.PopupConquista;
import com.integrador.apptrimonio.R;

import java.util.ArrayList;

public class PerfilConquistaItemAdapter extends RecyclerView.Adapter<PerfilConquistaItemAdapter.ViewHolder> {

    private final ArrayList<PerfilConquistaItem> listPerfilConquistaItem;
    private final Context context;
    private final PopupConquista popupConquista;

    public PerfilConquistaItemAdapter(ArrayList<PerfilConquistaItem> listPerfilConquistaItem, Context context) {
        this.listPerfilConquistaItem = listPerfilConquistaItem;
        this.context = context;
        this.popupConquista = new PopupConquista(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_perfil_conquista, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PerfilConquistaItem item = listPerfilConquistaItem.get(position);
        String nome = item.getNome();
        String desc = item.getDescricao();
        Drawable imagem = item.getImagem();
        Drawable fundo = item.getBackground();
        boolean desbloqueado = item.isDesbloqueado();

        holder.nome.setText(nome);
        holder.imagem.setImageDrawable(imagem);
        holder.background.setBackground(fundo);
        holder.view.setOnClickListener(v -> popupConquista.abrirPopupConquista(nome, desc, imagem));

        //caso não estiver desbloqueado fica totalmente cinza
        if (!desbloqueado) {
            holder.imagem.setColorFilter(Color.parseColor("#B7B7B7"), PorterDuff.Mode.MULTIPLY);
            holder.imagem.setAlpha(0.5F);
            holder.background.getBackground().setColorFilter(Color.parseColor("#B7B7B7"), PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public int getItemCount() {
        return listPerfilConquistaItem.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView nome;
        private final ImageView imagem;
        private final ConstraintLayout background;
        private final View view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.item_perfil_conquista_titulo);
            imagem = itemView.findViewById(R.id.item_perfil_conquista_imagem);
            background = itemView.findViewById(R.id.item_perfil_conquista_background);
            view = itemView;
        }
    }
}
