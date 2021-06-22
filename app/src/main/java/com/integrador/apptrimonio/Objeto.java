package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TypefaceSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.Utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class Objeto extends ActivityBase {

    private Utils utils;
    private String idObjeto;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objeto);

        utils = new Utils(this);

        //variáveis do objeto
        Bundle bundle = getIntent().getExtras();
        String imagem = bundle.getString("imagem");
        String descricao = !bundle.getString("descricao").equals("") || bundle.getString("descricao") == null ? bundle.getString("descricao") : getResources().getString(R.string.naoInfo);
        String codigo = !bundle.getString("codigo").equals("") || bundle.getString("codigo") == null ? bundle.getString("codigo") : getResources().getString(R.string.naoInfo);
        String nome = !bundle.getString("nome").equals("") || bundle.getString("nome") == null ? bundle.getString("nome") : getResources().getString(R.string.naoInfo);
        String lingua = !bundle.getString("lingua").equals("") || bundle.getString("lingua") == null ? bundle.getString("lingua") : getResources().getString(R.string.naoInfo);
        String categoria = !bundle.getString("categoria").equals("") || bundle.getString("categoria") == null ? bundle.getString("categoria") : getResources().getString(R.string.naoInfo);
        String descricaoImagem = !bundle.getString("descricaoImagem").equals("") || bundle.getString("descricaoImagem") == null ? bundle.getString("descricaoImagem") : getResources().getString(R.string.naoInfo);
        String local = !bundle.getString("local").equals("") || bundle.getString("local") == null ? bundle.getString("local") : getResources().getString(R.string.naoInfo);
        String valor = bundle.getDouble("valor") != 0 ? "R$" + bundle.getDouble("valor") : getResources().getString(R.string.naoInfo);
        String valorSentimental = !bundle.getString("valorSentimental").equals("") || bundle.getString("valorSentimental") == null ? bundle.getString("valorSentimental") : getResources().getString(R.string.naoInfo);

        idObjeto = codigo;

        String dataCompra = getResources().getString(R.string.naoInfo);
        String dataPublicacao = getResources().getString(R.string.naoInfo);

        DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        df.setTimeZone(TimeZone.getDefault());

        //caso tiver data de compra
        if (bundle.getLong("compra") != 0.0) {
            Date dateDataCompra = new Date(bundle.getLong("compra"));
            dataCompra = df.format(dateDataCompra);
        }

        //caso tiver data de publicacao
        if (bundle.getLong("dataPublicacao") != 0.0) {
            Date dateDataPublicacao = new Date(bundle.getLong("dataPublicacao"));
            dataPublicacao = df.format(dateDataPublicacao);
        }

        //variáveis
        ImageView voltar = findViewById(R.id.objeto_voltar);
        voltar.setOnClickListener(v -> finish());
        ImageView qrCode = findViewById(R.id.objeto_qrcode);
        qrCode.setOnClickListener(v -> abrirQrCode(codigo, descricaoImagem));
        ImageView reportar = findViewById(R.id.objeto_reportar);
        reportar.setOnClickListener(v -> reportarObjeto());
        ImageView editar = findViewById(R.id.objeto_editar);
        editar.setOnClickListener(v -> editarObjeto());
        ImageView excluir = findViewById(R.id.objeto_remover);
        excluir.setOnClickListener(v -> removerObjeto());

        //define as variáveis na tela
        setObjeto(imagem, nome, lingua, categoria, descricaoImagem, local, valor, valorSentimental, dataCompra, dataPublicacao, descricao);

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setObjeto(String imagem, String nome, String lingua, String categoria, String descricaoImagem, String local, String valor, String valorSentimental, String dataCompra, String dataPublicacao, String descricao) {
        //define as variáveis do objeto na tela
        ((TextView) findViewById(R.id.objeto_titulo)).setText(nome);
        ((TextView) findViewById(R.id.objeto_descricao)).setText(descricao);
        ((TextView) findViewById(R.id.objeto_valor)).setText(valor);
        ((TextView) findViewById(R.id.objeto_nome)).setText(nome);

        ((TextView) findViewById(R.id.objeto_categoria)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.category) + ": </b>" + categoria));
        ((TextView) findViewById(R.id.objeto_descricaoImagem)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.imgDesc) + ": </b>" + descricaoImagem));
        ((TextView) findViewById(R.id.objeto_local)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.place) + ": </b>" + local));
        ((TextView) findViewById(R.id.objeto_valorSentimental)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.senValue) + ": </b>" + valorSentimental));
        ((TextView) findViewById(R.id.objeto_data)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.date) + ": </b>" + dataCompra));
        ((TextView) findViewById(R.id.objeto_dataPublicacao)).setText(Html.fromHtml("<b>" + getResources().getString(R.string.pubDate) + ": </b>" + dataPublicacao));

        ImageView imageView = findViewById(R.id.objeto_imagem);
        //define a imagem
        if (imagem.equals("")) {
            imageView.setImageDrawable(getResources().getDrawable(R.drawable.semimagem));
        } else {
            Bitmap bitmap = Utils.getBitmapImage(imagem);
            imageView.setImageBitmap(bitmap);
        }

        //define a lingua
        ImageView linguaView = findViewById(R.id.objeto_lingua);
        if (lingua.equals("pt")) {
            linguaView.setImageDrawable(getResources().getDrawable(R.drawable.flag_pt));
        } else if (lingua.equals("es")) {
            linguaView.setImageDrawable(getResources().getDrawable(R.drawable.flag_es));
        }

    }

    private void abrirQrCode(String codigo, String descricaoImagem) { //ao clicar no icone de qrcode
        utils.abrirPopUpQRCode(codigo, descricaoImagem);
    }

    private void removerObjeto() { //ao clicar no icone de remover
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //caso não tiver usuário logado
            Toast.makeText(this, getResources().getString(R.string.loginRequired), Toast.LENGTH_LONG).show();
        } else if (!User.getInstance().isPermissaoGerenciador()) { //caso o usuário não possua permissão de gerenciador
            Toast.makeText(this, getResources().getString(R.string.remObjError), Toast.LENGTH_LONG).show();
        } else { //caso possua
            Toast.makeText(this, "Em desenvolvimento", Toast.LENGTH_SHORT).show();
        }
    }

    private void editarObjeto() { //ao clicar no icone de editar
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //caso não tiver usuário logado
            Toast.makeText(this, getResources().getString(R.string.loginRequired), Toast.LENGTH_LONG).show();
        } else if (!User.getInstance().isPermissaoGerenciador()) { //caso o usuário não possua permissão de editar objeto
            Toast.makeText(this, getResources().getString(R.string.editObjError), Toast.LENGTH_LONG).show();
        } else { //caso possua
            Toast.makeText(this, "Em desenvolvimento", Toast.LENGTH_SHORT).show();
        }
    }

    private void reportarObjeto() { //ao clicar no icone de reportar
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //caso não tiver usuário logado
            Toast.makeText(this, getResources().getString(R.string.loginRequired), Toast.LENGTH_LONG).show();
        } else { //caso tenha
            new PopupReport(this, idObjeto).abrirPopup();
        }
    }
}