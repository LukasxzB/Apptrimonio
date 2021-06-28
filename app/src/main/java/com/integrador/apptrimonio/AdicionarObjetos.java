package com.integrador.apptrimonio;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.Cache;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AdicionarObjetos extends ActivityBase {

    private boolean inseriuImagem = false;
    private final int selecionarFoto = 1;
    private RelativeLayout imagemBackground;
    private EditText nome, categoria, valor, local, descricao, sentimental, descricaoImagem;
    private TextView data;

    private ImageView imagem;

    private final ImageView[] linguas = new ImageView[3];

    private int linguaSelecionada = 0; //0 = ingles, 1 portugues 2 espanhol

    private TextView nomeTop, descricaoTop, categoriaTop, dataTop, valorTop, localTop, imgDescTop, senValorTop;

    private Date dataCompra = null;
    private DatePickerDialog datePickerDialog;
    private VolleyUtils volleyUtils;
    private Bitmap imagemBitmap, imagemComprimida;

    private Utils utils;

    private Dialog popupErro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adicionar_objetos);

        volleyUtils = new VolleyUtils(this);
        utils = new Utils(this);

        //popup
        popupErro = new Dialog(this);
        popupErro.setContentView(R.layout.popup_codigoinvalido);
        popupErro.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupErro.setCancelable(true);

        //seletor de datas
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            //pega a data que o usuário selecionou, formata e coloca no textview
            dataCompra = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            dataCompra = calendar.getTime();
            String dataTxt = android.text.format.DateFormat.getDateFormat(AdicionarObjetos.this).format(dataCompra.getTime());
            data.setText(dataTxt);
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
        Button adicionar = findViewById(R.id.adicionarObjetos_botao);
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
            } else {
                Utils.makeSnackbar(getResources().getString(R.string.needDescImg), findViewById(R.id.activity_adicionarObjetos));
            }
        }

        //muda cor
        imagemBackground.setBackground(ResourcesCompat.getDrawable(getResources(), inseriuImagem ? R.drawable.addimg_borda_verde : R.drawable.addimg_borda_vermelha, null));
        descricaoTop.setTextColor(getResources().getColor(R.color.verde4));
        nomeTop.setTextColor(getResources().getColor(vNome.equals("") ? R.color.vermelho : R.color.verde4));
        categoriaTop.setTextColor(getResources().getColor(vCategoria.equals("") ? R.color.vermelho : R.color.verde4));
        dataTop.setTextColor(getResources().getColor(R.color.verde4));
        valorTop.setTextColor(getResources().getColor(vValor.equals("") ? R.color.vermelho : R.color.verde4));
        localTop.setTextColor(getResources().getColor(vLocal.equals("") ? R.color.vermelho : R.color.verde4));
        imgDescTop.setTextColor(getResources().getColor(vImgDesc.equals("") ? R.color.vermelho : R.color.verde4));
        senValorTop.setTextColor(getResources().getColor(R.color.verde4));


        if (tudoOk) {
            adicionarVolley(vNome, vCategoria, vDescricao, Double.parseDouble(vValor), vLocal, vImgDesc, vValorSen, vLingua);
        }
    }

    private void adicionarVolley(String nome, String categoria, String descricao, double valor, String local, String imgDesc, String senValor, String lingua) {
        utils.abrirPopUpCarregando();
        imagemComprimida = comprimirImagem(imagemBitmap);
        VolleyInterface volleyInterface = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject objeto = new JSONObject(response);
                    String idObjeto = objeto.getString("idObjeto");
                    User.getInstance().adicionarObjetoAdicionado(idObjeto, nome);
                    String status = objeto.getString("aprovacao");

                    if (status.equalsIgnoreCase("aprovado")) {
                        abrirTelaObjeto(nome, categoria, descricao, valor, local, imgDesc, senValor, idObjeto, lingua, imagemComprimida);
                    } else {
                        abrirPopupErro(getResources().getString(R.string.andCode), getResources().getString(R.string.andCodeDesc), getResources().getString(R.string.objAndDesc), true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                    Toast.makeText(AdicionarObjetos.this, getResources().getString(R.string.addObjError), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(VolleyError error) {
                String mensagemErro = error.getMessage();

                if (error instanceof NetworkError || error instanceof TimeoutError) {
                    abrirPopupErro(getResources().getString(R.string.intCode), getResources().getString(R.string.intCodeDesc), getResources().getString(R.string.tryAgainLater), false);
                } else {
                    assert mensagemErro != null;
                    if (mensagemErro.equalsIgnoreCase("Bad request.")) {
                        abrirPopupErro(getResources().getString(R.string.intCode), getResources().getString(R.string.intCodeDesc), getResources().getString(R.string.tryAgain), false);
                    } else if (mensagemErro.equalsIgnoreCase("Unauthorized.")) {
                        abrirPopupErro(getResources().getString(R.string.permRequired), getResources().getString(R.string.addRequired), getResources().getString(R.string.tryAgainLater), false);
                    } else if (mensagemErro.equalsIgnoreCase("Email not verified.")) {
                        abrirPopupErro(getResources().getString(R.string.verified), getResources().getString(R.string.emailRequired), getResources().getString(R.string.tryAgainLater), false);
                    } else {
                        abrirPopupErro(getResources().getString(R.string.errCode), getResources().getString(R.string.errCodeDesc), getResources().getString(R.string.tryAgainLater), false);
                    }
                }
            }
        };

        volleyUtils.adicionarObjeto(volleyInterface, nome, categoria, descricao, dataCompra, valor, local, imgDesc, senValor, imagemComprimida, linguaSelecionada == 1 ? "pt" : linguaSelecionada == 2 ? "es" : "en");

    }

    private void abrirTelaObjeto(String nome, String categoria, String descricao, double valor, String local, String imgDesc, String senValor, String codigo, String lingua, Bitmap imagem) {

        Cache.getInstance().addBitmapToMemoryCache(codigo, imagem);

        try {
            Intent intent = new Intent(this, Objeto.class);
            Bundle bundle = new Bundle();
            bundle.putString("codigo", codigo);
            bundle.putString("imagem", codigo);
            bundle.putString("nome", nome);
            bundle.putString("lingua", lingua);
            bundle.putString("categoria", categoria);
            bundle.putString("descricaoImagem", imgDesc);
            bundle.putString("descricao", descricao);
            bundle.putString("local", local);
            bundle.putDouble("valor", valor);
            bundle.putString("valorSentimental", senValor);


            utils.fecharPopUpCarregando();
            intent.putExtras(bundle);
            startActivity(intent);
        } catch (Exception e) {
            utils.fecharPopUpCarregando();
            e.printStackTrace();
            Log.e("ERROR", e.getMessage());
        }

        finish();

    }

    private void abrirPopupErro(String titulo, String descricao, String tenteNovamente, boolean finish) {

        utils.fecharPopUpCarregando();

        //muda as variáveis do dialog
        TextView tituloText, descricaoText, tenteNovamenteText;
        tituloText = popupErro.findViewById(R.id.codigoinvalido_titulo);
        descricaoText = popupErro.findViewById(R.id.codigoinvalido_desc);
        tenteNovamenteText = popupErro.findViewById(R.id.codigoinvalido_tentenovamente);
        tituloText.setText(titulo);
        descricaoText.setText(descricao);
        tenteNovamenteText.setText(tenteNovamente);

        popupErro.findViewById(R.id.codigoinvalido_fechar).setOnClickListener(v -> fecharPopupErro(finish));
        popupErro.show();

    }

    private void fecharPopupErro(boolean finish) {
        popupErro.dismiss();
        if (finish) {
            finish();
        }
    }

    private void selecionarData() {
        datePickerDialog.show();
    }

    private Bitmap comprimirImagem(Bitmap realImage) {
        float ratio = Math.min(
                (float) 1280.0 / realImage.getWidth(),
                (float) 1280.0 / realImage.getHeight());
        int width = Math.round(ratio * realImage.getWidth());
        int height = Math.round(ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, true);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        newBitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
        return BitmapFactory.decodeStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selecionarFoto && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
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