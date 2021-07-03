package com.integrador.apptrimonio;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.InicoAdaptador;
import com.integrador.apptrimonio.Utils.ObjetosAndamento;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VerificadorPermissoes;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

public class MainActivity extends ActivityBase {

    //gadgets
    private ViewPager2 viewPager2;
    private SlidingRootNav menuLateral;
    private Button menulateralBotaoEntrar;

    private Utils utils;
    private VolleyUtils volleyUtils;

    private Dialog popupObjetosAndamento;
    private TextView popupObjetosAndamentoDesc;
    private ObjetosAndamento objetosAndamento;

    @Override
    protected void onStart() {
        super.onStart();
        atualizarBotoes();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        utils = new Utils(this);
        utils.verificarConta(login -> {
        });
        volleyUtils = new VolleyUtils(this);
        objetosAndamento = ObjetosAndamento.getInstance();

        //objeto andamento
        popupObjetosAndamento = new Dialog(this);
        popupObjetosAndamento.setContentView(R.layout.popup_objetosandamento);
        popupObjetosAndamento.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupObjetosAndamento.setCancelable(false);
        ((ImageView) popupObjetosAndamento.findViewById(R.id.objetosAndamento_fechar)).setOnClickListener(v -> fecharPopupAndamento());
        ((Button) popupObjetosAndamento.findViewById(R.id.objetosAndamento_cancelar)).setOnClickListener(v -> fecharPopupAndamento());
        ((Button) popupObjetosAndamento.findViewById(R.id.objetosAndamento_proximo)).setOnClickListener(v -> proximoObjeto());
        popupObjetosAndamentoDesc = popupObjetosAndamento.findViewById(R.id.objetosAndamento_desc);

        //adiciona o menu lateral
        menuLateral = new SlidingRootNavBuilder(this)
                .withMenuLayout(R.layout.menulateral)
                .withRootViewScale(0.45f)
                .addDragListener(progress -> {
                    if (progress == 1 && viewPager2.getCurrentItem() == 1) { //caso o usuário abrir o menu enquanto a câmera estiver aberta, a camera fecha
                        viewPager2.setCurrentItem(0, true);
                    }
                })
                .inject();

        //view do menu lateral
        View menuView = menuLateral.getLayout().getRootView();

        //botão de entrar ou sair e listener
        menulateralBotaoEntrar = menuView.findViewById(R.id.menulateral_entrar);
        atualizarBotoes();

        //perfil
        //menu lateral
        ConstraintLayout perfil = menuView.findViewById(R.id.menulateral_perfil0);
        perfil.setOnClickListener(v -> abrirPerfil());

        //textos informativos
        ConstraintLayout texto1 = menuView.findViewById(R.id.menulateral_textoinfo10);
        texto1.setOnClickListener(v -> abrirTexto(1));
        ConstraintLayout texto2 = menuView.findViewById(R.id.menulateral_textoinfo20);
        texto2.setOnClickListener(v -> abrirTexto(2));
        ConstraintLayout texto3 = menuView.findViewById(R.id.menulateral_textoinfo30);
        texto3.setOnClickListener(v -> abrirTexto(3));
        ConstraintLayout texto4 = menuView.findViewById(R.id.menulateral_textoinfo40);
        texto4.setOnClickListener(v -> abrirTexto(4));

        //listeners do faq
        ConstraintLayout faq = menuView.findViewById(R.id.menulateral_faq0);
        faq.setOnClickListener(v -> abrirFaq());

        //listeners do suporte
        ConstraintLayout suporte = menuView.findViewById(R.id.menulateral_suporte0);
        suporte.setOnClickListener(v -> abrirSuporte());

        //listeners do botão de adicionar objeto
        ConstraintLayout adicionar = menuView.findViewById(R.id.menulateral_adicionar0);
        adicionar.setOnClickListener(v -> abrirAdicionarObjeto());

        //listeners de escanear
        ConstraintLayout escanear = menuView.findViewById(R.id.menulateral_escanear0);
        escanear.setOnClickListener(v -> abrirCamera());

        //listerens de aprovar
        ConstraintLayout aprovar = menuView.findViewById(R.id.menulateral_aprovar0);
        aprovar.setOnClickListener(v -> abrirAprovarObjeto());

        //cria o listener de clique pra abrir e fechar o menu dentro do fragmento
        View.OnClickListener clickListener = v -> mudarMenu();

        //adiciona o fragmento da tela inicial e camera
        viewPager2 = findViewById(R.id.inicio_viewpager2);
        InicoAdaptador adaptador = new InicoAdaptador(this, clickListener, this);
        viewPager2.setAdapter(adaptador);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() { //callback pra permissao
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == adaptador.getItemCount() - 1) {
                    VerificadorPermissoes.verificarPermissoes(MainActivity.this);
                    fecharMenu();
                }
            }
        });
    }

    private void abrirPerfil() {
        Intent intent = new Intent(MainActivity.this, Perfil.class);
        startActivity(intent);
    }

    private void abrirSuporte() { //abre o aplicativo do email
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("plain/text");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{Utils.emailApptrimonio}); //email que será usado
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.apptrimonioSupport)); //titulo do email
        startActivity(Intent.createChooser(intent, getResources().getString(R.string.support))); //abre o email
    }

    private void abrirFaq() {
        Intent intent = new Intent(MainActivity.this, TextoInformativo.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", "faq");
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void abrirTexto(int num) {
        Intent intent = new Intent(MainActivity.this, TextoInformativo.class);
        Bundle bundle = new Bundle();
        bundle.putString("code", "text" + num);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void abrirAdicionarObjeto() { //abre a tela de adicionar objetos
        if (User.getInstance().isPermissaoAdicionar()) {
            Bundle bundle = new Bundle();
            bundle.putString("acao", "add");
            Intent intent = new Intent(this, GerenciarObjeto.class);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Utils.makeSnackbar(getResources().getString(R.string.addRequired), findViewById(R.id.activity_main));
        }
    }

    private void abrirAprovarObjeto() {
        if (User.getInstance().isPermissaoGerenciador()) {
            utils.abrirPopUpCarregando();
            //faz requesição ao servidor de objetos a serem aprovados
            VolleyInterface volleyInterface = new VolleyInterface() {
                @Override
                public void onResponse(String response) {
                    if (response.equalsIgnoreCase("There are no objects available.")) {
                        Utils.makeSnackbar(getResources().getString(R.string.verNoObjects), findViewById(R.id.activity_main));
                    } else {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            objetosAndamento.setObjetos(jsonArray);

                            //caso houver objetos
                            if (objetosAndamento.getLength() > 0) {
                                abrirPopupAndamento();
                            } else {
                                Utils.makeSnackbar(getResources().getString(R.string.verNoObjects), findViewById(R.id.activity_main));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Log.e("ERROR OBJAND", e.getMessage());
                            Utils.makeSnackbar(getResources().getString(R.string.errTryAgain), findViewById(R.id.activity_main));
                        }
                    }
                    utils.fecharPopUpCarregando();
                }

                @Override
                public void onError(VolleyError error) {
                    String erro = error.getMessage();
                    assert erro != null;
                    if (erro.equalsIgnoreCase("Bad request.") || erro.equalsIgnoreCase("Unauthorized.")) {
                        Utils.makeSnackbar(getResources().getString(R.string.loginRequired), findViewById(R.id.activity_main));
                    } else if (erro.equalsIgnoreCase("Email not verified.")) {
                        Utils.makeSnackbar(getResources().getString(R.string.emailRequired), findViewById(R.id.activity_main));
                    } else {
                        Utils.makeSnackbar(getResources().getString(R.string.errTryAgain), findViewById(R.id.activity_main));
                    }
                    utils.fecharPopUpCarregando();
                }
            };

            volleyUtils.objetosAndamento(volleyInterface);

        } else {
            Utils.makeSnackbar(getResources().getString(R.string.managerRequired), findViewById(R.id.activity_main));
        }
    }

    private void abrirCamera() {
        fecharMenu();
        viewPager2.setCurrentItem(1, true);
    }

    private void mudarMenu() { //abre ou fecha o menu
        if (menuLateral.isMenuOpened()) { //caso estiver aberto fecha
            menuLateral.closeMenu(true);
        } else { //caso estiver fechado abre
            menuLateral.openMenu(true);
        }
    }

    private void fecharMenu() { //fecha o menu caso estiver aberto (usado ao abrir a camera, evitando que ela seja aberta com o menu aberto)
        if (menuLateral.isMenuOpened()) {
            menuLateral.closeMenu(true);
        }
    }

    private void abrirPopupAndamento() {

        int quantidade = objetosAndamento.getLength();
        popupObjetosAndamentoDesc.setText(getResources().getString(R.string.verObjects).replace("%s", String.valueOf(quantidade)));

        popupObjetosAndamento.show();
    }

    private void fecharPopupAndamento() {
        popupObjetosAndamento.dismiss();
    }

    private void proximoObjeto() {
        try {

            Bundle bundle = new Bundle();
            JSONObject objeto = objetosAndamento.getNextObject();
            String tipo = objeto.getString("tipo");
            String idAndamento = objeto.getString("idAndamento");
            String idObjeto = objeto.getString("idObjeto");
            bundle.putString("idAndamento", idAndamento);
            bundle.putString("codigo", idObjeto);

            //verificar o tipo de objeto em andamento (add, edit ou report) e adicionar no bundle
            if (tipo.equalsIgnoreCase("add")) {

                //valores do objeto a ser verificado
                String imagem = objeto.getString("_imagem");
                String lingua = objeto.getString("_lingua");
                String categoria = objeto.getString("categoria");
                String descricao = objeto.getString("descricao");
                String descricaoImagem = objeto.getString("descricaoImagem");
                String local = objeto.getString("local");
                String nome = objeto.getString("nome");
                double valor = objeto.getDouble("valor");
                String valorSentimental = objeto.getString("valorSentimental");

                //adiciona no bundle os valores originais
                bundle.putString("acao", "verAdd");
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
                    JSONObject dataCompraObj = objeto.has("compra") ? new JSONObject(objeto.getString("compra")) : null;
                    assert dataCompraObj != null;
                    long compraLong = Long.parseLong(dataCompraObj.getString("_seconds"));
                    bundle.putLong("compra", compraLong * 1000);
                } catch (Exception e) {
                    Log.d("ERROR", e.getMessage());
                }

            } else if (tipo.equalsIgnoreCase("edit")) {

                //setar variaveis a serem editados (não originais)
                String editImagem = objeto.getString("editImagem");
                String editLingua = objeto.getString("_lingua");
                String editCategoria = objeto.getString("editCategoria");
                String editDescricaoImagem = objeto.getString("editDescricaoImagem");
                String editLocal = objeto.getString("editLocal");
                String editNome = objeto.getString("editNome");
                double editValor = objeto.getDouble("editValor");
                String editValorSentimental = objeto.getString("editValorSentimental");
                String editDescricao = objeto.getString("editDescricao");

                //adiciona no bundle os valores a serem editados (não originais)
                bundle.putString("acao", "verAdd");
                bundle.putString("editImagem", editImagem);
                bundle.putString("editNome", editNome);
                bundle.putString("editLingua", editLingua);
                bundle.putString("editCategoria", editCategoria);
                bundle.putString("editDescricaoImagem", editDescricaoImagem);
                bundle.putString("editDescricao", editDescricao);
                bundle.putString("editLocal", editLocal);
                bundle.putDouble("editValor", editValor);
                bundle.putString("editValorSentimental", editValorSentimental);

                //verifica se tem data de compra
                try {
                    JSONObject dataCompraObj = objeto.has("editCompra") ? new JSONObject(objeto.getString("editCompra")) : null;
                    assert dataCompraObj != null;
                    long compraLong = Long.parseLong(dataCompraObj.getString("_seconds"));
                    bundle.putLong("editCompra", compraLong * 1000);
                } catch (Exception e) {
                    Log.d("ERROR", e.getMessage());
                }

                //requisitar o objeto original


                //adicionar no bundle

                //verifica se tem data de compra em ambos


            } else if (tipo.equalsIgnoreCase("report")) {

                //setar variaveis a serem editadas

                //requisitar o objeto original

                //adicionar no bundle

                //verifica se tem data de compra em ambos

            }

            //enviar dados pra tela de gerenciar objeto
            Intent intent = new Intent(this, GerenciarObjeto.class);
            intent.putExtras(bundle);
            startActivity(intent);

            //ao voltar remover objeto da lista de objetos em andamento
            objetosAndamento.removerObjeto();

            //ao voltar atualizar textview
            int novaQuantidade = objetosAndamento.getLength();
            popupObjetosAndamentoDesc.setText(getResources().getString(R.string.verObjects).replace("%s", String.valueOf(novaQuantidade)));

            //caso tiver 0 objetos restantes fechar popup
            if (novaQuantidade == 0) {
                fecharPopupAndamento();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("ERROR objand", e.getMessage());
            fecharPopupAndamento();
            Utils.makeSnackbar(getResources().getString(R.string.errTryAgain), findViewById(R.id.activity_main));
        }
    }

    private void atualizarBotoes() {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) { //caso não houver usuário logado
            menulateralBotaoEntrar.setText(getResources().getString(R.string.login)); //muda o nome pra entrar
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_verde_escuro)); //muda a cor pra verde
            menulateralBotaoEntrar.setOnClickListener(v -> {
                startActivity(new Intent(MainActivity.this, Autenticar.class)); //abre a tela de autenticar
            });
        } else { //caso houver
            menulateralBotaoEntrar.setText(getResources().getString(R.string.logoff)); //muda o nome pra sair
            menulateralBotaoEntrar.setBackground(getResources().getDrawable(R.drawable.button_vermelho_escuro)); //muda a cor pra vermelha
            menulateralBotaoEntrar.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut(); //faz logout do firebase
                utils.verificarConta(login -> {
                });
                Utils.makeSnackbar(getResources().getString(R.string.madeLogoff), findViewById(R.id.activity_main));
                atualizarBotoes();
            });
        }
    }
}