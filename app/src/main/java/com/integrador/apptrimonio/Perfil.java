package com.integrador.apptrimonio;

import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.SharedPreferences;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.VolleyError;
import com.google.firebase.auth.FirebaseAuth;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.skydoves.progressview.ProgressView;

import org.json.JSONObject;

public class Perfil extends ActivityBase {

    private int xp, xpNecessario, xpAtual, level;
    private boolean receberEmails, acessibilidade;
    private SharedPreferences sharedPreferences;

    private Utils utils;
    private VolleyUtils volleyUtils;

    private LottieAnimationView acessibilidadeLottie, mudarEmailLottie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        utils = new Utils(this);
        volleyUtils = new VolleyUtils(this);

        User user = User.getInstance();
        xp = user.getXp();
        xpAtual = user.getXpNextLevelXP();
        xpNecessario = user.getNextLevelXP();

        sharedPreferences = getSharedPreferences(getResources().getString(R.string.app_name), MODE_PRIVATE);
        acessibilidade = sharedPreferences.getBoolean("acessibilidade", false);
        receberEmails = user.isReceberEmails();

        //widgets
        ProgressView xpProgress = findViewById(R.id.perfil_progress);
        xpProgress.setLabelText(xpAtual + "/" + xpNecessario);
        xpProgress.setProgress((float) (xpAtual * 100 / xpNecessario));

        //badges
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);

        ImageView verificado = findViewById(R.id.perfil_badge_verificado);
        ImageView editor = findViewById(R.id.perfil_badge_editar);
        ImageView adicionador = findViewById(R.id.perfil_badge_adicionar);
        ImageView gerenciador = findViewById(R.id.perfil_badge_gerenciador);
        verificado.setOnClickListener(v -> aoClicarBadge(getResources().getString(R.string.badgeVerified)));
        editor.setOnClickListener(v -> aoClicarBadge(getResources().getString(R.string.badgeEdit)));
        adicionador.setOnClickListener(v -> aoClicarBadge(getResources().getString(R.string.badgeAdd)));
        gerenciador.setOnClickListener(v -> aoClicarBadge(getResources().getString(R.string.badgeManager)));

        if (!user.isEmailVerificado()) {
            verificado.setColorFilter(filter);
        }
        if (!user.isPermissaoEditar()) {
            editor.setColorFilter(filter);
        }
        if (!user.isPermissaoAdicionar()) {
            adicionador.setColorFilter(filter);
        }
        if (!user.isPermissaoGerenciador()) {
            gerenciador.setColorFilter(filter);
        }

        //variaveis
        TextView email = findViewById(R.id.perfil_email);
        TextView level = findViewById(R.id.perfil_nivel);
        TextView xp = findViewById(R.id.perfil_xp);
        TextView escaneados = findViewById(R.id.perfil_escaneados);
        TextView adicionados = findViewById(R.id.perfil_adicionados);
        TextView verificados = findViewById(R.id.perfil_verificados);
        ConstraintLayout acessibilidadeConstraint = findViewById(R.id.perfil_constraint_acessibilidade);
        ConstraintLayout receberEmailsConstraint = findViewById(R.id.perfil_constraint_receberemails);
        acessibilidadeLottie = findViewById(R.id.perfil_acessibilidade_botao);
        mudarEmailLottie = findViewById(R.id.perfil_receberemails_botao);

        if (acessibilidade) {
            acessibilidadeLottie.setSpeed(1);
            acessibilidadeLottie.playAnimation();
        }
        if (receberEmails) {
            mudarEmailLottie.setSpeed(1);
            mudarEmailLottie.playAnimation();
        }

        acessibilidadeConstraint.setOnClickListener(v -> mudarAcessibilidade());
        receberEmailsConstraint.setOnClickListener(v -> mudarReceberEmails());

        String emailTxt = user.getEmail() == null ? getResources().getString(R.string.naoInfo) : user.getEmail();
        String levelTxt = getResources().getString(R.string.level) + " " + user.getLevel();
        String xpTxt = user.getXp() + " " + getResources().getString(R.string.xp);
        String escaneadosTxt = user.getCodigos().length() + " " + getResources().getString(R.string.objects);
        String adicionadosTxt = user.getObjetosAdicionados().length() + " " + getResources().getString(R.string.objects);
        String verificadosTxt = user.getObjetosVerificados().length() + " " + getResources().getString(R.string.objects);

        //seta as variáveis
        email.setText(emailTxt);
        level.setText(levelTxt);
        xp.setText(xpTxt);
        escaneados.setText(escaneadosTxt);
        adicionados.setText(adicionadosTxt);
        verificados.setText(verificadosTxt);
    }

    private void aoClicarBadge(String mensagem) {
        Utils.makeSnackbar(mensagem, findViewById(R.id.activity_perfil));
    }

    private void mudarAcessibilidade() {
        acessibilidade = !acessibilidade;
        sharedPreferences.edit().putBoolean("acessbilidade", acessibilidade).apply();
        acessibilidadeLottie.setSpeed(acessibilidade ? 1 : -1);
        acessibilidadeLottie.playAnimation();
        Utils.makeSnackbar(acessibilidade ? getResources().getString(R.string.acessibilityOn) : getResources().getString(R.string.acessibilityOff), findViewById(R.id.activity_perfil));
    }

    private void mudarReceberEmails() {
        //envia request e espera o ok

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Utils.makeSnackbar(getResources().getString(R.string.loginRequired), findViewById(R.id.activity_perfil));
        } else {
            utils.abrirPopUpCarregando();

            VolleyInterface callback = new VolleyInterface() {
                @Override
                public void onResponse(String response) {

                    try {
                        JSONObject object = new JSONObject(response);
                        receberEmails = object.getBoolean("status");

                        User.getInstance().setReceberEmails(receberEmails);
                        mudarEmailLottie.setSpeed(receberEmails ? 1 : -1);
                        mudarEmailLottie.playAnimation();
                        Utils.makeSnackbar(receberEmails ? getResources().getString(R.string.receiveEmailsOn) : getResources().getString(R.string.receiveEmailsOff), findViewById(R.id.activity_perfil));

                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e("ERROR", e.getMessage());
                        Utils.makeSnackbar(receberEmails ? getResources().getString(R.string.receiveEmailsOnError) : getResources().getString(R.string.receiveEmailsOffError), findViewById(R.id.activity_perfil));
                    }
                    utils.fecharPopUpCarregando();
                }

                @Override
                public void onError(String error) {
                    utils.fecharPopUpCarregando();
                    Utils.makeSnackbar(receberEmails ? getResources().getString(R.string.receiveEmailsOnError) : getResources().getString(R.string.receiveEmailsOffError), findViewById(R.id.activity_perfil));
                }
            };

            volleyUtils.receberEmails(callback, !receberEmails);
        }

    }
}