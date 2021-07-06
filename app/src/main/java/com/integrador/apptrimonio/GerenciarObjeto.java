package com.integrador.apptrimonio;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;

import org.json.JSONObject;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class GerenciarObjeto extends AppCompatActivity {

    private ImageView botaoVoltar;
    private TextView tituloTela, warnTela;
    private Button botaoTela;

    private Bundle bundle;
    private Utils utils;
    private VolleyUtils volleyUtils;
    private PopupGerenciar popupGerenciar;

    private ImageView imagemView;
    private RelativeLayout imagemBackgroundView;

    private final ImageView[] linguas = new ImageView[3];
    private int linguaSelecionada = 0; //0 = ingles, 1 portugues 2 espanhol
    private int selecionarImagem = 28747;

    private TextView nomeTop, descricaoTop, categoriaTop, dataTop, valorTop, localTop, descricaoImagemTop, valorSentimentalTop, dataView;
    private EditText nomeView, descricaoView, categoriaView, valorView, localView, descricaoImagemView, valorSentimentalView;
    private TextView nomeDesc, descricaoDesc, categoriaDesc, dataDesc, valorDesc, localDesc, descricaoImagemDesc, valorSentimentalDesc;
    private TextView motivoReporteView;
    private LinearLayout layoutBotoesReport, layoutBotoesAddEdit;
    private Button aprovarReportBotao, desaprovarReportBotao, removerReportBotao, aprovarAddEditBotao, desaprovarAddEditBotao;

    private DatePickerDialog datePickerDialog;
    private Date dataCompraV;
    private Bitmap imagemBitmap;
    private String idObjeto;
    private String acao;

    private String imagemOriginal, descricaoOriginal, codigoOriginal, nomeOriginal, linguaOriginal, categoriaOriginal, descricaoImagemOriginal, localOriginal, valorOriginal, valorSentimentalOriginal;
    private Date dataCompraOriginal, dataCompraEditada;
    private String imagemEditada, descricaoEditada, codigoEditado, nomeEditado, linguaEditada, categoriaEditada, descricaoImagemEditada, localEditado, valorEditado, valorSentimentalEditado;

    private String idAndamento;

    private Bitmap imagemOriginalBitmap, imagemEditadaBitmap;

    private PopupCodigoInvalido popupCodigoInvalido;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gerenciar_objeto);

        bundle = getIntent().getExtras();
        utils = new Utils(this);
        volleyUtils = new VolleyUtils(this);
        popupGerenciar = new PopupGerenciar(this);
        popupCodigoInvalido = new PopupCodigoInvalido(this);

        //setup gadgets da tela
        setupGadgets();

        //setup date picker log
        setupDatePickerLog();

        //seleciona a ação que irá executar
        acao = bundle.getString("acao", "add");
        if (acao.equalsIgnoreCase("add")) {
            mudarTelaAdicionar();
        } else if (acao.equalsIgnoreCase("edit")) {
            mudarTelaEditar();
        } else if (acao.equalsIgnoreCase("verReport") || acao.equalsIgnoreCase("verEdit") || acao.equalsIgnoreCase("verAdd")) {
            mudarTelaVerificar(acao);
        }
    }

    private void setupGadgets() {
        botaoVoltar = findViewById(R.id.gerenciarObjeto_voltar); //botao de voltar
        botaoVoltar.setOnClickListener(v -> finish());
        tituloTela = findViewById(R.id.gerenciarObjeto_titulo); //titulo
        warnTela = findViewById(R.id.gerenciarObjeto_warn); //warn
        botaoTela = findViewById(R.id.gerenciarObjeto_botao); //botao

        imagemBackgroundView = findViewById(R.id.gerenciarObjeto_imagem_fundo); //imagem
        imagemView = findViewById(R.id.gerenciarObjeto_imagem);

        linguas[0] = findViewById(R.id.gerenciarObjeto_lingua_en); //linguas
        linguas[1] = findViewById(R.id.gerenciarObjeto_lingua_pt);
        linguas[2] = findViewById(R.id.gerenciarObjeto_lingua_es);
        linguas[0].setOnClickListener(v -> mudarLingua(0)); //listeners da lingua
        linguas[1].setOnClickListener(v -> mudarLingua(1));
        linguas[2].setOnClickListener(v -> mudarLingua(2));

        nomeTop = findViewById(R.id.gerenciarObjeto_input_nome_top);
        descricaoTop = findViewById(R.id.gerenciarObjeto_input_descricao_top);
        categoriaTop = findViewById(R.id.gerenciarObjeto_input_categoria_top);
        dataTop = findViewById(R.id.gerenciarObjeto_input_data_top);
        valorTop = findViewById(R.id.gerenciarObjeto_input_valor_top);
        localTop = findViewById(R.id.gerenciarObjeto_input_local_top);
        descricaoImagemTop = findViewById(R.id.gerenciarObjeto_input_descricaoImg_top);
        valorSentimentalTop = findViewById(R.id.gerenciarObjeto_input_valorSentimental_top);
        dataView = findViewById(R.id.gerenciarObjeto_input_data);
        nomeView = findViewById(R.id.gerenciarObjeto_input_nome);
        descricaoView = findViewById(R.id.gerenciarObjeto_input_descricao);
        categoriaView = findViewById(R.id.gerenciarObjeto_input_categoria);
        valorView = findViewById(R.id.gerenciarObjeto_input_valor);
        localView = findViewById(R.id.gerenciarObjeto_input_local);
        descricaoImagemView = findViewById(R.id.gerenciarObjeto_input_descricaoImg);
        valorSentimentalView = findViewById(R.id.gerenciarObjeto_input_valorSentimental);
        nomeDesc = findViewById(R.id.gerenciarObjeto_input_nome_desc);
        descricaoDesc = findViewById(R.id.gerenciarObjeto_input_descricao_desc);
        categoriaDesc = findViewById(R.id.gerenciarObjeto_input_categoria_desc);
        dataDesc = findViewById(R.id.gerenciarObjeto_input_data_desc);
        valorDesc = findViewById(R.id.gerenciarObjeto_input_valor_desc);
        localDesc = findViewById(R.id.gerenciarObjeto_input_local_desc);
        descricaoImagemDesc = findViewById(R.id.gerenciarObjeto_input_descricaoImg_desc);
        valorSentimentalDesc = findViewById(R.id.gerenciarObjeto_input_valorSentimental_desc);
        motivoReporteView = findViewById(R.id.gerenciarObjeto_report_desc);
        layoutBotoesReport = findViewById(R.id.gerenciarObjeto_botao_report);
        layoutBotoesAddEdit = findViewById(R.id.gerenciarObjeto_botao_addedit);
        aprovarReportBotao = findViewById(R.id.gerenciarObjeto_botao_report_aprovar);
        removerReportBotao = findViewById(R.id.gerenciarObjeto_botao_report_remover);
        desaprovarReportBotao = findViewById(R.id.gerenciarObjeto_botao_report_desaprovar);
        aprovarAddEditBotao = findViewById(R.id.gerenciarObjeto_botao_addedit_aprovar);
        desaprovarAddEditBotao = findViewById(R.id.gerenciarObjeto_botao_addedit_desaprovar);

        //muda a imagem padrao do objeto por causa da lingua
        String lingua = getResources().getConfiguration().locale.getLanguage().trim();
        imagemView.setImageDrawable(lingua.equalsIgnoreCase("pt") ? ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_ptg, null) : lingua.equalsIgnoreCase("es") ? ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_esp, null) : ResourcesCompat.getDrawable(getResources(), R.drawable.addimg_ing, null));

        //ao clicar na imagem pra escolher a imagem
        imagemView.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, selecionarImagem);
        });

        //ao clicar pra escolher a data
        dataView.setOnClickListener(v -> datePickerDialog.show());
    }

    private void setupDatePickerLog() {
        DatePickerDialog.OnDateSetListener dateSetListener = (view, year, month, dayOfMonth) -> {
            //pega a data que o usuário selecionou, formata e coloca no textview
            dataCompraV = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.YEAR, year);
            dataCompraV = calendar.getTime();
            String dataTxt = android.text.format.DateFormat.getDateFormat(this).format(dataCompraV.getTime());
            dataView.setText(dataTxt);
        };

        Calendar calendar = Calendar.getInstance();
        int ano = calendar.get(Calendar.YEAR);
        int mes = calendar.get(Calendar.MONTH);
        int dia = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialog = new DatePickerDialog(this, dateSetListener, ano, mes, dia);
    }

    private void setupValoresOriginais(int visibility) {
        nomeDesc.setVisibility(visibility);
        descricaoDesc.setVisibility(visibility);
        categoriaDesc.setVisibility(visibility);
        dataDesc.setVisibility(visibility);
        valorDesc.setVisibility(visibility);
        localDesc.setVisibility(visibility);
        descricaoImagemDesc.setVisibility(visibility);
        valorSentimentalDesc.setVisibility(visibility);
    }

    private void setupValoresOriginaisValores() {
        imagemOriginal = bundle.getString("imagem", "");
        descricaoOriginal = bundle.getString("descricao", "").trim();
        codigoOriginal = bundle.getString("codigo", "").trim();
        nomeOriginal = bundle.getString("nome", "").trim();
        linguaOriginal = bundle.getString("lingua", "").trim();
        categoriaOriginal = bundle.getString("categoria", "").trim();
        descricaoImagemOriginal = bundle.getString("descricaoImagem", "").trim();
        localOriginal = bundle.getString("local", "").equals("") || bundle.getString("local") != null ? bundle.getString("local").trim() : getResources().getString(R.string.naoInfo);
        valorOriginal = String.valueOf(bundle.getDouble("valor", 0));
        valorSentimentalOriginal = bundle.getString("valorSentimental", "").trim();
        idObjeto = codigoOriginal;

        //define a imagem do objeto
        if (!imagemOriginal.equals("")) {
            Glide.with(this).asBitmap().load(imagemOriginal).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                    imagemOriginalBitmap = resource;
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                }
            });
            Glide.with(this).load(imagemOriginal).transform(new CircleCrop()).into(imagemView);
        }

        //define a lingua do objeto
        int linguaFinal = linguaOriginal.equalsIgnoreCase("pt") ? 1 : linguaOriginal.equalsIgnoreCase("es") ? 2 : 1;
        if (!linguaOriginal.equalsIgnoreCase("")) {
            mudarLingua(linguaFinal);
        }

        //define o valor do textview de original
        nomeDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getString("nome", getResources().getString(R.string.naoInfo)).trim()));
        descricaoDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getString("descricao", getResources().getString(R.string.naoInfo)).trim()));
        categoriaDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getString("categoria", getResources().getString(R.string.naoInfo)).trim()));
        valorDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getDouble("valor") != 0 ? bundle.getDouble("valor") : getResources().getString(R.string.naoInfo)).trim());
        localDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getString("local", getResources().getString(R.string.naoInfo)).trim()));
        descricaoImagemDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getString("descricaoImagem", getResources().getString(R.string.naoInfo)).trim()));
        valorSentimentalDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), bundle.getString("valorSentimental", getResources().getString(R.string.naoInfo)).trim()));

        //define o valor dos inputs
        nomeView.setText(nomeOriginal);
        descricaoView.setText(descricaoOriginal);
        categoriaView.setText(categoriaOriginal);
        valorView.setText(valorOriginal);
        localView.setText(localOriginal);
        descricaoImagemView.setText(descricaoImagemOriginal);
        valorSentimentalView.setText(valorSentimentalOriginal);

        //define o valor da data de compra caso houver
        String dataCompraTxt = getResources().getString(R.string.naoInfo);

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        //caso tiver data de compra
        if (bundle.getLong("compra", 0) != 0.0) {
            Date dateDataCompra = new Date(bundle.getLong("compra"));
            dataCompraOriginal = dateDataCompra;
            dataCompraTxt = df.format(dateDataCompra);
        }

        dataView.setText(dataCompraTxt.equalsIgnoreCase(getResources().getString(R.string.naoInfo)) ? "" : dataCompraTxt);
        dataDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), dataCompraTxt));

    }

    private void setupListenersAdicionarEditar(String acao) {
        //ao clicar em adicionar
        botaoTela.setOnClickListener(v -> {
            Bitmap imagemPreenchida = imagemBitmap;
            if (!acao.equalsIgnoreCase("add")) {
                imagemPreenchida = imagemBitmap != null ? imagemBitmap : imagemOriginalBitmap;
            }
            String nomePreenchido = nomeView.getText().toString().trim();
            String descricaoPreenchida = descricaoView.getText().toString().trim();
            String categoriaPreenchida = categoriaView.getText().toString().trim();
            String valorPreenchido = valorView.getText().toString().trim();
            String localPreenchido = localView.getText().toString().trim();
            String descricaoImagemPreenchida = descricaoImagemView.getText().toString().trim();
            String valorSentimentalPreenchido = valorSentimentalView.getText().toString().trim();
            String linguaPreenchida = linguaSelecionada == 1 ? "pt" : linguaSelecionada == 2 ? "es" :
                    "en";

            //verifica se os campos necessários foram preenchidos
            boolean preenchidos = isPreenchido(imagemPreenchida, nomePreenchido, descricaoPreenchida, categoriaPreenchida, valorPreenchido, localPreenchido, descricaoImagemPreenchida, valorSentimentalPreenchido, linguaPreenchida);

            //caso estiver tudo preenchido faz o request
            if (preenchidos) {
                if (acao.equalsIgnoreCase("add")) {
                    requestAdicionar(imagemPreenchida, nomePreenchido, descricaoPreenchida, categoriaPreenchida, valorPreenchido, localPreenchido, descricaoImagemPreenchida, valorSentimentalPreenchido, linguaPreenchida);
                } else {
                    requestEditar(imagemPreenchida, nomePreenchido, descricaoPreenchida, categoriaPreenchida, valorPreenchido, localPreenchido, descricaoImagemPreenchida, valorSentimentalPreenchido, linguaPreenchida);
                }
            }
        });
    }

    private void setupValoresVerEdit() {
        //define os valores do ver edit
        idAndamento = bundle.getString("idAndamento", "");
        idObjeto = bundle.getString("codigo", "");

        imagemEditada = bundle.getString("editImagem", "");
        nomeEditado = bundle.getString("editNome", "");
        linguaEditada = bundle.getString("editLingua", "");
        categoriaEditada = bundle.getString("editCategoria", "");
        descricaoImagemEditada = bundle.getString("editDescricaoImagem", "");
        descricaoEditada = bundle.getString("editDescricao", "");
        localEditado = bundle.getString("editLocal", "");
        valorEditado = bundle.getString("editValor", "");
        valorSentimentalEditado = bundle.getString("editValorSentimental", "");

        //define a imagem do objeto
        if (!imagemEditada.equals("")) {
            Glide.with(this).asBitmap().load(imagemEditada).into(new CustomTarget<Bitmap>() {
                @Override
                public void onResourceReady(@NonNull Bitmap resource, @Nullable @org.jetbrains.annotations.Nullable Transition<? super Bitmap> transition) {
                    imagemEditadaBitmap = resource;
                }

                @Override
                public void onLoadCleared(@Nullable @org.jetbrains.annotations.Nullable Drawable placeholder) {

                }
            });
            Glide.with(this).load(imagemEditada).transform(new CircleCrop()).into(imagemView);
        }

        //define a lingua do objeto
        int linguaFinal = linguaEditada.equalsIgnoreCase("pt") ? 1 : linguaOriginal.equalsIgnoreCase("es") ? 2 : 1;
        if (!linguaEditada.equalsIgnoreCase("")) {
            mudarLingua(linguaFinal);
        }

        //define o valor dos inputs
        nomeView.setText(nomeEditado);
        descricaoView.setText(descricaoEditada);
        categoriaView.setText(categoriaEditada);
        valorView.setText(valorEditado);
        localView.setText(localEditado);
        descricaoImagemView.setText(descricaoImagemEditada);
        valorSentimentalView.setText(valorSentimentalEditado);

        //define o valor da data de compra caso houver
        String dataCompraTxt = getResources().getString(R.string.naoInfo);

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        //caso tiver data de compra
        if (bundle.getLong("compra", 0) != 0.0) {
            Date dateDataCompra = new Date(bundle.getLong("compra"));
            dataCompraEditada = dateDataCompra;
            dataCompraTxt = df.format(dateDataCompra);
        }

        dataView.setText(dataCompraTxt.equalsIgnoreCase(getResources().getString(R.string.naoInfo)) ? "" : dataCompraTxt);
        dataDesc.setText(String.format("%s %s", getResources().getString(R.string.originalValue), dataCompraTxt));

    }

    private void setupValoresVerAdd() {
        //define os valores do ver add
        idAndamento = bundle.getString("idAndamento", "");
        idObjeto = bundle.getString("codigo", "");
    }

    private void setupValoresVerReport() {
        //define os valores do ver report
        idAndamento = bundle.getString("idAndamento", "");
        idObjeto = bundle.getString("codigo", "");
    }

    private void setupListenersVerificar() {

        //ao clicar em aprovar em add ou edit
        aprovarAddEditBotao.setOnClickListener(v -> requestVerAddEdit(true, ""));

        //ao clicar em desaprovar em add ou edit
        desaprovarAddEditBotao.setOnClickListener(v -> {
            if (acao.equalsIgnoreCase("verAdd")) { //caso clicar em desaprovar adição irá abrir o popup para selecionar o motivo
                abrirPopupMotivo("add");
            } else {
                requestVerAddEdit(false, ""); //caso não for apenas envia a request
            }
        });

        //ao clicar em aprovar em report
        aprovarReportBotao.setOnClickListener(v -> requestVerReport(true));

        //ao clicar em desaprovar em report
        desaprovarReportBotao.setOnClickListener(v -> requestVerReport(false));

        //ao clicar em remover em report
        removerReportBotao.setOnClickListener(v -> {
            abrirPopupMotivo("rem");
        });
    }

    private void requestVerAddEdit(boolean status, String motivo) {

        //valores preenchidos
        Bitmap imagemPreenchida = imagemBitmap != null ? imagemBitmap : imagemEditadaBitmap != null ? imagemEditadaBitmap : imagemOriginalBitmap;
        String nomePreenchido = nomeView.getText().toString().trim();
        String descricaoPreenchida = descricaoView.getText().toString().trim();
        String categoriaPreenchida = categoriaView.getText().toString().trim();
        String valorPreenchido = valorView.getText().toString().trim();
        String localPreenchido = localView.getText().toString().trim();
        String descricaoImagemPreenchida = descricaoImagemView.getText().toString().trim();
        String valorSentimentalPreenchido = valorSentimentalView.getText().toString().trim();
        String linguaPreenchida = linguaSelecionada == 1 ? "pt" : linguaSelecionada == 2 ? "es" :
                "en";

        //verifica se os campos necessários foram preenchidos
        boolean preenchidos = !status || isPreenchido(imagemPreenchida, nomePreenchido, descricaoPreenchida, categoriaPreenchida, valorPreenchido, localPreenchido, descricaoImagemPreenchida, valorSentimentalPreenchido, linguaPreenchida);

        //adicionar callback do volley
        VolleyInterface volleyInterface = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                utils.fecharPopUpCarregando();
                Toast.makeText(GerenciarObjeto.this, getResources().getString(R.string.verSuccess), Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onError(String erro) {
                utils.fecharPopUpCarregando();

                if (erro.equalsIgnoreCase("network")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.intCode), getResources().getString(R.string.intCodeDesc), getResources().getString(R.string.tryAgainLater), false);
                } else if (erro.equalsIgnoreCase("Unauthorized.")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.permRequired), getResources().getString(R.string.managerRequired), getResources().getString(R.string.tryAgainLater), true);
                } else if (erro.equalsIgnoreCase("Email not verified.")) {
                    Utils.makeSnackbar(getResources().getString(R.string.emailRequired), findViewById(R.id.activity_gerenciarObjeto));
                } else if (erro.equalsIgnoreCase("Object not found!")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.errCode), getResources().getString(R.string.objNotFound), getResources().getString(R.string.tryAgainLater), true);
                } else if (erro.equalsIgnoreCase("The object has been deleted.")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.objRem), getResources().getString(R.string.objRem), getResources().getString(R.string.tryAgainLater), true);
                } else {
                    Utils.makeSnackbar(getResources().getString(R.string.errTryAgain), findViewById(R.id.activity_gerenciarObjeto));
                }

            }
        };

        //fazer chamada
        if (preenchidos) {
            utils.abrirPopUpCarregando();
            Bitmap imagemFinal = imagemPreenchida == imagemOriginalBitmap ? null : imagemPreenchida;
            String nomeFinal = nomePreenchido.equals(nomeOriginal) ? "" : nomePreenchido;
            String descricaoFinal = descricaoPreenchida.equals(descricaoOriginal) ? "" : descricaoPreenchida;
            String categoriaFinal = categoriaPreenchida.equals(categoriaOriginal) ? "" : categoriaPreenchida;
            String valorFinal = valorPreenchido.equals(valorOriginal) ? "0" : valorPreenchido;
            String localFinal = localPreenchido.equals(localOriginal) ? "" : localPreenchido;
            String descricaoImagemFinal = descricaoImagemPreenchida.equals(descricaoImagemOriginal) ? "" : descricaoImagemPreenchida;
            String valorSentimentalFinal = valorSentimentalPreenchido.equals(valorSentimentalOriginal) ? "" : valorSentimentalPreenchido;
            String linguaFinal = linguaPreenchida.equals(linguaOriginal) ? "" : nomePreenchido;
            Date dataCompraFinal = dataCompraV == dataCompraOriginal ? null : dataCompraV;

            volleyUtils.aprovacaoObjeto(volleyInterface, status, idAndamento, descricaoFinal, motivo, imagemFinal, categoriaFinal, dataCompraFinal, descricaoImagemFinal, localFinal, nomeFinal, Double.parseDouble(valorFinal), valorSentimentalFinal, linguaFinal);
        }

    }

    private void requestVerReport(boolean status) {

    }

    private void requestVerReportRemove(String motivos) {

    }

    private void requestEditar(Bitmap imagemPreenchida, String nomePreenchido, String descricaoPreenchida, String categoriaPreenchida, String valorPreenchido, String localPreenchido, String descricaoImagemPreenchida, String valorSentimentalPreenchido, String linguaPreenchida) {
        Bitmap imagemFinal = imagemPreenchida == imagemOriginalBitmap ? null : imagemPreenchida;
        String nomeFinal = nomePreenchido.equals(nomeOriginal) ? "" : nomePreenchido;
        String descricaoFinal = descricaoPreenchida.equals(descricaoOriginal) ? "" : descricaoPreenchida;
        String categoriaFinal = categoriaPreenchida.equals(categoriaOriginal) ? "" : categoriaPreenchida;
        String valorFinal = valorPreenchido.equals(valorOriginal) ? "0" : valorPreenchido;
        String localFinal = localPreenchido.equals(localOriginal) ? "" : localPreenchido;
        String descricaoImagemFinal = descricaoImagemPreenchida.equals(descricaoImagemOriginal) ? "" : descricaoImagemPreenchida;
        String valorSentimentalFinal = valorSentimentalPreenchido.equals(valorSentimentalOriginal) ? "" : valorSentimentalPreenchido;
        String linguaFinal = linguaPreenchida.equals(linguaOriginal) ? "" : nomePreenchido;
        Date dataCompraFinal = dataCompraV == dataCompraOriginal ? null : dataCompraV;

        utils.abrirPopUpCarregando();
        Bitmap imagemComprimida = Utils.comprimirImagem(imagemFinal);

        VolleyInterface volleyInterface = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                utils.fecharPopUpCarregando();
                try {
                    JSONObject objeto = new JSONObject(response);
                    String status = objeto.getString("status");

                    if (status.equalsIgnoreCase("aprovado")) {
                        Toast.makeText(GerenciarObjeto.this, getResources().getString(R.string.editSuccess), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        abrirPopupErro(R.raw.error, false, getResources().getString(R.string.andCode), getResources().getString(R.string.andCodeDesc), getResources().getString(R.string.objAndDesc), true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                    Toast.makeText(GerenciarObjeto.this, getResources().getString(R.string.editObjError), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(String erro) {
                utils.fecharPopUpCarregando();

                if (erro.equalsIgnoreCase("network")) {
                    abrirPopupErro(R.raw.internet, false, getResources().getString(R.string.intCode), getResources().getString(R.string.intCodeDesc), getResources().getString(R.string.tryAgainLater), false);
                } else if (erro.equalsIgnoreCase("Unauthorized.")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.permRequired), getResources().getString(R.string.editRequired), getResources().getString(R.string.tryAgainLater), true);
                } else if (erro.equalsIgnoreCase("Email not verified.")) {
                    Utils.makeSnackbar(getResources().getString(R.string.emailRequired), findViewById(R.id.activity_gerenciarObjeto));
                } else if (erro.equalsIgnoreCase("Object not found!")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.errCode), getResources().getString(R.string.objNotFound), getResources().getString(R.string.tryAgainLater), true);
                } else {
                    Utils.makeSnackbar(getResources().getString(R.string.errTryAgain), findViewById(R.id.activity_gerenciarObjeto));
                }

            }
        };

        volleyUtils.editarObjeto(volleyInterface, idObjeto, descricaoFinal, imagemComprimida, categoriaFinal, dataCompraFinal, descricaoImagemFinal, localFinal, nomeFinal, Double.parseDouble(valorFinal), valorSentimentalFinal, linguaFinal);
    }

    private void requestAdicionar(Bitmap imagemPreenchida, String nomePreenchido, String descricaoPreenchida, String categoriaPreenchida, String valorPreenchido, String localPreenchido, String descricaoImagemPreenchida, String valorSentimentalPreenchido, String linguaPreenchida) {
        utils.abrirPopUpCarregando();
        Bitmap imagemComprimida = Utils.comprimirImagem(imagemPreenchida);
        VolleyInterface volleyInterface = new VolleyInterface() {
            @Override
            public void onResponse(String response) {
                utils.fecharPopUpCarregando();
                try {
                    JSONObject objeto = new JSONObject(response);
                    String idObjeto = objeto.getString("idObjeto");
                    User.getInstance().adicionarObjetoAdicionado(idObjeto, nomePreenchido);
                    String status = objeto.getString("aprovacao");

                    if (status.equalsIgnoreCase("aprovado")) {
                        String imagem = objeto.getString("imagem");
                        abrirTelaObjeto(nomePreenchido, categoriaPreenchida, descricaoPreenchida, Double.parseDouble(valorPreenchido), localPreenchido, descricaoImagemPreenchida, valorSentimentalPreenchido, idObjeto, linguaPreenchida, imagem);
                    } else {
                        abrirPopupErro(R.raw.analyzing, false, getResources().getString(R.string.andCode), getResources().getString(R.string.andCodeDesc), getResources().getString(R.string.objAndDesc), true);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("ERROR", e.getMessage());
                    Toast.makeText(GerenciarObjeto.this, getResources().getString(R.string.addObjError), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onError(String erro) {
                utils.fecharPopUpCarregando();

                if (erro.equalsIgnoreCase("network")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.intCode), getResources().getString(R.string.intCodeDesc), getResources().getString(R.string.tryAgainLater), false);
                } else if (erro.equalsIgnoreCase("Unauthorized.")) {
                    abrirPopupErro(R.raw.error, false, getResources().getString(R.string.permRequired), getResources().getString(R.string.editRequired), getResources().getString(R.string.tryAgainLater), true);
                } else if (erro.equalsIgnoreCase("Email not verified.")) {
                    Utils.makeSnackbar(getResources().getString(R.string.emailRequired), findViewById(R.id.activity_gerenciarObjeto));
                } else {
                    Utils.makeSnackbar(getResources().getString(R.string.errTryAgain), findViewById(R.id.activity_gerenciarObjeto));
                }

            }
        };

        volleyUtils.adicionarObjeto(volleyInterface, nomePreenchido, categoriaPreenchida, descricaoPreenchida, dataCompraV, Double.parseDouble(valorPreenchido), localPreenchido, descricaoImagemPreenchida, valorSentimentalPreenchido, imagemComprimida, linguaPreenchida);
    }

    private void abrirTelaObjeto(String nome, String categoria, String descricao, double valor, String local, String imgDesc, String senValor, String codigo, String lingua, String imagem) {

        try {
            Intent intent = new Intent(this, Objeto.class);
            Bundle bundle = new Bundle();
            bundle.putString("codigo", codigo);
            bundle.putString("imagem", imagem);
            bundle.putString("nome", nome);
            bundle.putString("lingua", lingua);
            bundle.putString("categoria", categoria);
            bundle.putString("descricaoImagem", imgDesc);
            bundle.putString("descricao", descricao);
            bundle.putString("local", local);
            bundle.putDouble("valor", valor);
            bundle.putString("valorSentimental", senValor);
            bundle.putLong("dataPublicacao", Calendar.getInstance().getTime().getTime());
            bundle.putLong("compra", dataCompraV == null ? 0 : dataCompraV.getTime());

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

    private boolean isPreenchido(Bitmap imagemPreenchida, String nomePreenchido, String descricaoPreenchida, String categoriaPreenchida, String valorPreenchido, String localPreenchido, String descricaoImagemPreenchida, String valorSentimentalPreenchido, String linguaPreenchida) {
        //verifica se todos os campos necessários foram preenchidos
        boolean preenchido = imagemPreenchida != null && !nomePreenchido.equals("") && !descricaoPreenchida.equals("") && !categoriaPreenchida.equals("") && !valorPreenchido.equals("") && !localPreenchido.equals("") && !descricaoImagemPreenchida.equals("") && !valorSentimentalPreenchido.equals("") && !linguaPreenchida.equals("");

        //muda as cores
        imagemBackgroundView.setBackground(ResourcesCompat.getDrawable(getResources(), imagemPreenchida != null || imagemOriginal != null ? R.drawable.addimg_borda_verde : R.drawable.addimg_borda_vermelha, null));
        descricaoTop.setTextColor(getResources().getColor(R.color.verde4));
        nomeTop.setTextColor(getResources().getColor(nomePreenchido.equals("") ? R.color.vermelho : R.color.verde4));
        categoriaTop.setTextColor(getResources().getColor(categoriaPreenchida.equals("") ? R.color.vermelho : R.color.verde4));
        dataTop.setTextColor(getResources().getColor(R.color.verde4));
        valorTop.setTextColor(getResources().getColor(valorPreenchido.equals("") ? R.color.vermelho : R.color.verde4));
        localTop.setTextColor(getResources().getColor(localPreenchido.equals("") ? R.color.vermelho : R.color.verde4));
        descricaoImagemTop.setTextColor(getResources().getColor(descricaoImagemPreenchida.equals("") ? R.color.vermelho : R.color.verde4));
        valorSentimentalTop.setTextColor(getResources().getColor(R.color.verde4));

        //mostra snackbar
        if (nomePreenchido.equals("")) {
            Utils.makeSnackbar(getResources().getString(R.string.needName), findViewById(R.id.activity_gerenciarObjeto));
        } else if (categoriaPreenchida.equals("")) {
            Utils.makeSnackbar(getResources().getString(R.string.needCategory), findViewById(R.id.activity_gerenciarObjeto));
        } else if (valorPreenchido.equals("")) {
            Utils.makeSnackbar(getResources().getString(R.string.needValue), findViewById(R.id.activity_gerenciarObjeto));
        } else if (imagemPreenchida == null) {
            Utils.makeSnackbar(getResources().getString(R.string.needImage), findViewById(R.id.activity_gerenciarObjeto));
        } else if (localPreenchido.equals("")) {
            Utils.makeSnackbar(getResources().getString(R.string.needPlace), findViewById(R.id.activity_gerenciarObjeto));
        } else if (descricaoImagemPreenchida.equals("")) {
            Utils.makeSnackbar(getResources().getString(R.string.needDescImg), findViewById(R.id.activity_gerenciarObjeto));
        }

        return preenchido;
    }

    private void mudarTelaAdicionar() {
        //muda o titulo pra adicionar objeto, o warn e o botão
        tituloTela.setText(getResources().getString(R.string.addObj));
        warnTela.setText(getResources().getString(R.string.addWarn));
        botaoTela.setVisibility(View.VISIBLE);
        layoutBotoesAddEdit.setVisibility(View.GONE);
        layoutBotoesReport.setVisibility(View.GONE);
        botaoTela.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.add_button, null));

        //muda a visibilidade do motivo do report para GONE
        motivoReporteView.setVisibility(View.GONE);

        //muda a lingua do objeto para a do dispositivo
        Locale linguaSysLocale = Locale.getDefault();
        mudarLingua(linguaSysLocale.getLanguage().equals(new Locale("pt").getLanguage()) ? 1 : linguaSysLocale.getLanguage().equals(new Locale("es").getLanguage()) ? 2 : 0);

        //define os valores originais (seta para GONE no caso do adicionar)
        setupValoresOriginais(View.GONE);

        //define os listeners
        setupListenersAdicionarEditar("add");
    }

    private void mudarTelaEditar() {
        //muda o titulo pra editar objeto, o warn e os botões
        tituloTela.setText(getResources().getString(R.string.editObj));
        warnTela.setText(getResources().getString(R.string.editWarn));
        botaoTela.setVisibility(View.VISIBLE);
        layoutBotoesAddEdit.setVisibility(View.GONE);
        layoutBotoesReport.setVisibility(View.GONE);
        botaoTela.setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.edit_button, null));

        //muda a visibilidade do motivo do report para GONE
        motivoReporteView.setVisibility(View.GONE);

        //muda a lingua do objeto para a original caso houver
        String linguaTxt = bundle.getString("lingua");
        String linguaFinal = linguaTxt.equalsIgnoreCase("") ? Locale.getDefault().getLanguage() : linguaTxt;
        mudarLingua(linguaFinal.equalsIgnoreCase("pt") ? 1 : linguaFinal.equalsIgnoreCase("es") ? 2 : 0);

        //define os valores originais
        setupValoresOriginais(View.VISIBLE);

        //valores do bundle
        setupValoresOriginaisValores();

        //define os listeners
        setupListenersAdicionarEditar("edit");
    }

    private void mudarTelaVerificar(String acao) {
        //muda o titulo para verificar reporte, verificar adição ou verificar edição, também muda o warn e os botões
        tituloTela.setText(acao.equalsIgnoreCase("verReport") ? getResources().getString(R.string.verReport) : acao.equalsIgnoreCase("verEdit") ?
                getResources().getString(R.string.verEdit) : getResources().getString(R.string.verAdd));
        warnTela.setText(getResources().getString(R.string.verWarn));
        botaoTela.setVisibility(View.GONE);
        layoutBotoesAddEdit.setVisibility(acao.equalsIgnoreCase("verReport") ? View.GONE : View.VISIBLE);
        layoutBotoesReport.setVisibility(acao.equalsIgnoreCase("verReport") ? View.VISIBLE : View.GONE);

        //muda a visibilidade do motivo do report para VISIBLE e o define caso for report
        motivoReporteView.setVisibility(acao.equalsIgnoreCase("verReport") ? View.VISIBLE : View.GONE);

        //muda a lingua do objeto para a original caso houver
        String linguaTxt = bundle.getString("lingua");
        String linguaFinal = linguaTxt.equalsIgnoreCase("") ? Locale.getDefault().getLanguage() : linguaTxt;
        mudarLingua(linguaFinal.equalsIgnoreCase("pt") ? 1 : linguaFinal.equalsIgnoreCase("es") ? 2 : 0);

        //define os valores originais
        setupValoresOriginais(View.VISIBLE);

        //valores do bundle
        setupValoresOriginaisValores();

        //valores do verificar
        if (acao.equalsIgnoreCase("verAdd")) {
            setupValoresVerAdd();
        } else if (acao.equalsIgnoreCase("verReport")) {
            setupValoresVerReport();
        } else {
            setupValoresVerEdit();
        }

        //define os listeners
        setupListenersVerificar();
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

    private void abrirPopupErro(int rawRes, boolean loop, String titulo, String descricao, String tenteNovamente, boolean finish) {

        View.OnClickListener listener = v -> fecharPopupErro(finish);
        popupCodigoInvalido.abrirPopupCodigoInvalido(rawRes, loop, titulo, descricao, tenteNovamente, listener);
    }

    private void fecharPopupErro(boolean finish) {
        popupCodigoInvalido.fecharPopupCodigoInvalido();
        if (finish) {
            finish();
        }
    }

    private void abrirPopupMotivo(String acao) {
        //a ação pode ser adicionar ou remover
        View.OnClickListener addListener = v -> {
            if (popupGerenciar.isDone()) {
                fecharPopupMotivo();
                String motivos = popupGerenciar.getMotivos();
                requestVerAddEdit(false, motivos);
            }
        };

        View.OnClickListener remListener = v -> {
            if (popupGerenciar.isDone()) {
                fecharPopupMotivo();
                String motivos = popupGerenciar.getMotivos();
                requestVerReportRemove(motivos);
            }
        };

        popupGerenciar.setButtonListener(acao.equalsIgnoreCase("add") ? addListener : remListener);
        popupGerenciar.setText(acao.equalsIgnoreCase("add") ? getResources().getString(R.string.disapproveObj) : getResources().getString(R.string.remObj),
                acao.equalsIgnoreCase("add") ? getResources().getString(R.string.verDesc) : getResources().getString(R.string.remDesc),
                acao.equalsIgnoreCase("add") ? getResources().getString(R.string.disapproveObjWarn) : getResources().getString(R.string.remWarning));
        popupGerenciar.abrirPopup();

    }

    private void fecharPopupMotivo() {
        popupGerenciar.fecharPopup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == selecionarImagem && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                imagemBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Glide.with(this).load(uri).transform(new CircleCrop()).into(imagemView);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("ERROR", e.getMessage());
                Utils.makeSnackbar(getResources().getString(R.string.selectImageError), findViewById(R.id.activity_gerenciarObjeto));
            }
        }
    }
}