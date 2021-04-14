package com.integrador.apptrimonio.Utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.integrador.apptrimonio.R;

import java.util.List;

public class BoasVindasAdaptador extends RecyclerView.Adapter<BoasVindasAdaptador.BoasVindasViewHolder> {

    private final List<BoasVindasItem> itens;

    public BoasVindasAdaptador(List<BoasVindasItem> itens) {
        this.itens = itens;
    }

    static class BoasVindasViewHolder extends RecyclerView.ViewHolder {

        private final TextView tituloText;
        private final TextView descricaoText;
        private final ImageView imagemBoasVindas;

        BoasVindasViewHolder(@NonNull View itemView) {
            super(itemView);
            tituloText = itemView.findViewById(R.id.boasvindas_titulo);
            descricaoText = itemView.findViewById(R.id.boasvindas_descricao);
            imagemBoasVindas = itemView.findViewById(R.id.boasvindas_imagem);
        }

        void setBoasVindasData(BoasVindasItem item) {
            tituloText.setText(item.getTitulo());
            descricaoText.setText(item.getDescricao());
            imagemBoasVindas.setImageResource(item.getImagem());
        }
    }

    @NonNull
    @Override
    public BoasVindasViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BoasVindasViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.boasvindas_item_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BoasVindasViewHolder holder, int position) {
        holder.setBoasVindasData(itens.get(position));
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }


}
