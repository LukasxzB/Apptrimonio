package com.integrador.apptrimonio;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Random;

public class FragmentoTelaInicial extends Fragment {

    private ImageView background, swipeUp;
    private ViewPager2 viewPager2;

    public FragmentoTelaInicial(ViewPager2 vp) {
        viewPager2 = vp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fragmento_tela_inicial, container, false);

        background = view.findViewById(R.id.inicio_background);
        @SuppressLint("UseCompatLoadingForDrawables") final Drawable[] backgrounds = {getResources().getDrawable(R.drawable.backgroundi), getResources().getDrawable(R.drawable.backgroundii),
                getResources().getDrawable(R.drawable.backgroundiii), getResources().getDrawable(R.drawable.backgroundiv), getResources().getDrawable(R.drawable.backgroundv)};
        int backgroundAleatorio = new Random().nextInt(backgrounds.length);
        background.setImageDrawable(backgrounds[backgroundAleatorio]);
        swipeUp = view.findViewById(R.id.inicio_swipe_gif);

        Glide.with(this).load(R.drawable.swipeup).into(swipeUp);

        //muda a cor dos textos para dar contraste com o background
        TextView titulo,desc,swipe;
        titulo = view.findViewById(R.id.inicio_titulo);
        desc = view.findViewById(R.id.inicio_desc);
        swipe = view.findViewById(R.id.inicio_swipeup_texto);

        if(backgroundAleatorio == 1 || backgroundAleatorio == 2){
            titulo.setTextColor(getResources().getColor(R.color.branco));
            desc.setTextColor(getResources().getColor(R.color.branco));
            swipe.setTextColor(getResources().getColor(R.color.branco));
        }

        return view;
    }
}