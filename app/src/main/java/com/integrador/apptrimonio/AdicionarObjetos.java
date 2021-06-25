package com.integrador.apptrimonio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.common.BitMatrix;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdicionarObjetos extends ActivityBase {

    private boolean inseriuImagem = false;
    private int selecionarFoto = 1;
    private Uri uri;
    private RelativeLayout imagemBackground;
    private EditText nome, categoria, valor, local, descricao, sentimental, descricaoImagem;
    private Button adicionar;
    private TextView data;

    private ImageView imagem;

    private final ImageView[] linguas = new ImageView[3];

    private int linguaSelecionada = 0; //0 = ingles, 1 portugues 2 espanhol

    private TextView nomeTop, descricaoTop, categoriaTop, dataTop, valorTop, localTop, imgDescTop, senValorTop;

    private Date dataCompra = null;
    private DatePickerDialog datePickerDialog;
    private VolleyUtils volleyUtils;
    private Bitmap imagemBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_objetos);

        volleyUtils = new VolleyUtils(this);

        //seletor de datas
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                //pega a data que o usuário selecionou, formata e coloca no textview
                dataCompra = new Date();
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.YEAR, year);
                dataCompra = calendar.getTime();
                String dataTxt = android.text.format.DateFormat.getDateFormat(AdicionarObjetos.this).format(dataCompra.getTime());
                data.setText(dataTxt);
            }
        };

        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, ano, mes, dia);

        //botao voltar
        ImageView voltar = findViewById(R.id.adicionarObjetos_voltar);
        voltar.setOnClickListener(v -> finish());

        //muda a imagem padrao do objeto por causa da lingua
        imagem = findViewById(R.id.adicionarObjetos_imagem);
        String lingua = getResources().getConfiguration().locale.getLanguage().trim();
        imagem.setImageDrawable(lingua.equalsIgnoreCase("pt") ? ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_ptg, null) : lingua.equalsIgnoreCase("es") ? ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_esp, null) : ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_ing, null));

        //ao clicar na imagem pra escolher a imagem
        imagem.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, selecionarFoto);
        });

        //variaveis de top
        nomeTop = findViewById(R.id.adicionarObjetos_input_nome_top);
        descricaoTop = findViewById(R.id.adicionarObjetos_input_descricao_top);
        categoriaTop = findViewById(R.id.adicionarObjetos_input_categoria_top);
        dataTop = findViewById(R.id.adicionarObjetos_input_data_top);
        valorTop = findViewById(R.id.adicionarObjetos_input_valor_top);
        localTop = findViewById(R.id.adicionarObjetos_input_local_top);
        imgDescTop = findViewById(R.id.adicionarObjetos_input_descricaoImg_top);
        senValorTop = findViewById(R.id.adicionarObjetos_input_valorSentimental_top);

        //variaveis de inputs
        imagemBackground = findViewById(R.id.adicionarObjetos_imagem_fundo);
        nome = findViewById(R.id.adicionarObjetos_input_nome);
        categoria = findViewById(R.id.adicionarObjetos_input_categoria);
        data = findViewById(R.id.adicionarObjetos_input_data);
        valor = findViewById(R.id.adicionarObjetos_valor);
        local = findViewById(R.id.adicionarObjetos_local);
        descricao = findViewById(R.id.adicionarObjetos_input_descricao);
        adicionar = findViewById(R.id.adicionarObjetos_botao);
        sentimental = findViewById(R.id.adicionarObjetos_valorSentimental);
        descricaoImagem = findViewById(R.id.adicionarObjetos_descricaoImg);
        linguas[1] = findViewById(R.id.adicionarObjetos_lingua_pt);
        linguas[0] = findViewById(R.id.adicionarObjetos_lingua_en);
        linguas[2] = findViewById(R.id.adicionarObjetos_lingua_es);

        linguas[1].setOnClickListener(v -> mudarLingua(1));
        linguas[0].setOnClickListener(v -> mudarLingua(0));
        linguas[2].setOnClickListener(v -> mudarLingua(2));

        linguaSelecionada = Locale.getDefault().getLanguage().equalsIgnoreCase("pt") ? 1 : Locale.getDefault().getLanguage().equalsIgnoreCase("es") ? 2 : 0;
        mudarLingua(linguaSelecionada);

        //ao clicar em data
        data.setOnClickListener(v -> selecionarData());
        dataTop.setOnClickListener(v -> selecionarData());

        //botao adicionar
        adicionar.setOnClickListener(v -> adicionarObjetos());
    }

    private void mudarLingua(int lingua) {

        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        ColorMatrix matrix2 = new ColorMatrix();
        matrix2.setSaturation(1);
        ColorMatrixColorFilter filter2 = new ColorMatrixColorFilter(matrix2);

        linguaSelecionada = lingua;

        for (int i = 0; i < linguas.length; i++) {
            if (i == linguaSelecionada) {
                linguas[i].setColorFilter(filter2);
            } else {
                linguas[i].setColorFilter(filter);
            }
        }

    }

    private void adicionarObjetos() {

        //valores do input
        String vNome = nome.getText().toString().trim();
        String vCategoria = categoria.getText().toString().trim();
        String vValor = valor.getText().toString().trim();
        String vLocal = local.getText().toString().trim();
        String vDescricao = descricao.getText().toString().trim();
        String vLingua = linguaSelecionada == 1 ? "pt" : linguaSelecionada == 2 ? "es" : "en";
        String vImgDesc = descricaoImagem.getText().toString().trim();
        String vValorSen = sentimental.getText().toString().trim();

        boolean tudoOk = true;

        //verifica se todos os valores existem, caso não exista muda a cor pra vermelho e mostra um snackbar
        if (vNome.equals("") || vCategoria.equals("") || vValor.equals("") || !inseriuImagem || vLocal.equals("") || vImgDesc.equals("")) {
            tudoOk = false;

            //mostra snackbar
            if (vNome.equals("")) {
                Utils.makeSnackbar(getResources().getString(R.string.needName), findViewById(R.id.activity_adicionarObjetos));
            } else if (vCategoria.equals("")) {
                Utils.makeSnackbar(getResources().getString(R.string.needCategory), findViewById(R.id.activity_adicionarObjetos));
            } else if (vValor.equals("")) {
                Utils.makeSnackbar(getResources().getString(R.string.needValue), findViewById(R.id.activity_adicionarObjetos));
            } else if (!inseriuImagem) {
                Utils.makeSnackbar(getResources().getString(R.string.needImage), findViewById(R.id.activity_adicionarObjetos));
            } else if (vLocal.equals("")) {
                Utils.makeSnackbar(getResources().getString(R.string.needPlace), findViewById(R.id.activity_adicionarObjetos));
            } else if (vImgDesc.equals("")) {
                Utils.makeSnackbar(getResources().getString(R.string.needDescImg), findViewById(R.id.activity_adicionarObjetos));
            }
        }

        //muda cor
        imagemBackground.setBackgroundDrawable(ResourcesCompat.getDrawable(getResources(), inseriuImagem ? R.drawable.addimg_borda_verde : R.drawable.addimg_borda_vermelha, null));
        descricaoTop.setTextColor(getResources().getColor(R.color.verde4));
        nomeTop.setTextColor(getResources().getColor(vNome.equals("") ? R.color.vermelho : R.color.verde4));
        categoriaTop.setTextColor(getResources().getColor(vCategoria.equals("") ? R.color.vermelho : R.color.verde4));
        dataTop.setTextColor(getResources().getColor(R.color.verde4));
        valorTop.setTextColor(getResources().getColor(vValor.equals("") ? R.color.vermelho : R.color.verde4));
        localTop.setTextColor(getResources().getColor(vLocal.equals("") ? R.color.vermelho : R.color.verde4));
        imgDescTop.setTextColor(getResources().getColor(vImgDesc.equals("") ? R.color.vermelho : R.color.verde4));
        senValorTop.setTextColor(getResources().getColor(R.color.verde4));


        if (tudoOk) {
            adicionarVolley(vNome, vCategoria, vDescricao, Double.parseDouble(vValor), vLocal, vImgDesc, vValorSen);
        }
    }

    private void adicionarVolley(String nome, String categoria, String descricao, double valor, String local, String imgDesc, String senValor) {
        VolleyInterface volleyInterface = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                Utils.makeSnackbar("ok!", findViewById(R.id.activity_adicionarObjetos));
            }

            @Override
            public void onError(VolleyError error) {
                Utils.makeSnackbar("erro!", findViewById(R.id.activity_adicionarObjetos));
            }
        };

        volleyUtils.adicionarObjeto(volleyInterface, nome, categoria, descricao, dataCompra, valor, local, imgDesc, senValor, comprimirImagem(imagemBitmap, 1280f, true), linguaSelecionada == 1 ? "pt" : linguaSelecionada == 2 ? "es" : "en");

    }

    private void abrirPopupAndamento() {

    }

    private void abrirObjeto() {

    }

    private void selecionarData() {
        datePickerDialog.show();
        ;
    }

    private Bitmap comprimirImagem(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selecionarFoto && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uri = data.getData();
            try {
                imagemBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                inseriuImagem = true;
                Glide.with(this).load(uri).transform(new CircleCrop()).into(imagem);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR", e.getMessage());
                Utils.makeSnackbar(getResources().getString(R.string.selectImageError), findViewById(R.id.activity_adicionarObjetos));
            }
        }
    }
}