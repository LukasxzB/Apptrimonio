package com.integrador.apptrimonio;

import android.content.res.Resources;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.integrador.apptrimonio.Utils.ActivityBase;

public class TextoInformativo extends ActivityBase {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texto_informativo);

        WebView webView = findViewById(R.id.textoinfo_webview);

        //titulo
        TextView titulo = findViewById(R.id.textoinfo_titulo);

        //botao voltar
        ImageView voltar = findViewById(R.id.textoinfo_voltar);
        voltar.setOnClickListener(v -> finish());

        String lingua = Resources.getSystem().getConfiguration().locale.getLanguage().trim();
        if (!lingua.equalsIgnoreCase("pt") && !lingua.equalsIgnoreCase("es")) {
            lingua = "en";
        }

        //pega o parametro
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String code = bundle.getString("code");

            //caso for faq
            if (code.equalsIgnoreCase("faq")) {
                titulo.setText(getResources().getString(R.string.apphelp));
                webView.loadUrl("file:///android_asset/faq_" + lingua + ".html");
            } else if (code.equalsIgnoreCase("text1")) { //caso for o texto 1
                titulo.setText(getResources().getString(R.string.text1t));
                webView.loadUrl("file:///android_asset/text1_" + lingua + ".html");
            } else if (code.equalsIgnoreCase("text2")) { //caso for o texto 2
                titulo.setText(getResources().getString(R.string.text2t));
                webView.loadUrl("file:///android_asset/text2_" + lingua + ".html");
            } else if (code.equalsIgnoreCase("text3")) { //caso for o texto 3
                titulo.setText(getResources().getString(R.string.text3t));
                webView.loadUrl("file:///android_asset/text3_" + lingua + ".html");
            } else if (code.equalsIgnoreCase("text4")) { //caso for o texto 4
                titulo.setText(getResources().getString(R.string.text4t));
                webView.loadUrl("file:///android_asset/text4_" + lingua + ".html");
            }

        }
    }
}