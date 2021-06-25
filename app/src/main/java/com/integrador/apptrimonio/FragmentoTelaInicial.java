package com.integrador.apptrimonio;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import java.util.Random;

public class FragmentoTelaInicial extends Fragment {

    private final View.OnClickListener aoClicarBotaoMenu;

    public FragmentoTelaInicial(View.OnClickListener clickListener) {
        aoClicarBotaoMenu = clickListener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_tela_inicial, container, false);

        ImageView background = view.findViewById(R.id.inicio_background);
        @SuppressLint("UseCompatLoadingForDrawables") final Drawable[] backgrounds = {getResources().getDrawable(R.drawable.backgroundi), getResources().getDrawable(R.drawable.backgroundii),
                getResources().getDrawable(R.drawable.backgroundiii), getResources().getDrawable(R.drawable.backgroundiv), getResources().getDrawable(R.drawable.backgroundv)};
        int backgroundAleatorio = new Random().nextInt(backgrounds.length);
        background.setImageDrawable(backgrounds[backgroundAleatorio]);
        ImageView swipeUp = view.findViewById(R.id.inicio_swipe_gif);
        ImageView botaoMenuIcon = view.findViewById(R.id.inicio_menu_icon);
        botaoMenuIcon.setOnClickListener(aoClicarBotaoMenu);

        Glide.with(this).load(R.drawable.swipeup).into(swipeUp);

        //muda a cor dos textos para dar contraste com o background
        TextView titulo, desc;
        titulo = view.findViewById(R.id.inicio_titulo);
        desc = view.findViewById(R.id.inicio_desc);

        if (backgroundAleatorio == 1 || backgroundAleatorio == 2 || backgroundAleatorio == 3) {
            titulo.setTextColor(getResources().getColor(R.color.branco));
            desc.setTextColor(getResources().getColor(R.color.branco));
            botaoMenuIcon.setImageDrawable(getResources().getDrawable(R.drawable.menu_iconbranco));
        }

        return view;
    }
}