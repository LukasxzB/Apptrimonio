package com.integrador.apptrimonio;

import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.GerenciarObjetoAdicionadoItem;
import com.integrador.apptrimonio.Utils.GerenciarObjetoAdicionadoItemAdapter;
import com.integrador.apptrimonio.Utils.PerfilConquistaItem;
import com.integrador.apptrimonio.Utils.PerfilConquistaItemAdapter;
import com.integrador.apptrimonio.Utils.User;
import com.integrador.apptrimonio.Utils.Utils;
import com.integrador.apptrimonio.Utils.VolleyInterface;
import com.integrador.apptrimonio.Utils.VolleyUtils;
import com.skydoves.progressview.ProgressView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Perfil extends ActivityBase {

    private boolean receberEmails;

    private Utils utils;
    private VolleyUtils volleyUtils;

    private LottieAnimationView mudarEmailLottie;

    private ArrayList<GerenciarObjetoAdicionadoItem> itensObjetosAdicionados;
    private ArrayList<PerfilConquistaItem> itensPerfilConquista;
    private ConstraintLayout layoutItensObjetosAdicionados;
    private RecyclerView listViewObjetosAdicionados, listViewConquistas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        utils = new Utils(this);
        volleyUtils = new VolleyUtils(this);
        itensObjetosAdicionados = new ArrayList<>();
        itensPerfilConquista = new ArrayList<>();
        layoutItensObjetosAdicionados = findViewById(R.id.perfil_recyclerview_constraint);
        listViewObjetosAdicionados = findViewById(R.id.perfil_recyclerview);
        listViewConquistas = findViewById(R.id.perfil_recyclerview_conquistas);

        //define os itens adicionados
        setupItensObjetosAdicionados();

        //define as conquistas
        setupItensConquistas();

        User user = User.getInstance();
        int xpAtual = user.getXpNextLevelXP();
        int xpNecessario = user.getNextLevelXP();

        receberEmails = user.isReceberEmails();

        //widgets
        ProgressView xpProgress = findViewById(R.id.perfil_progress);
        xpProgress.setLabelText(xpAtual + "/" + xpNecessario);
        xpProgress.setProgress((float) (xpAtual * 100 / xpNecessario));
        findViewById(R.id.perfil_voltar).setOnClickListener(v -> finish());

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
        ConstraintLayout receberEmailsConstraint = findViewById(R.id.perfil_constraint_receberemails);
        mudarEmailLottie = findViewById(R.id.perfil_receberemails_botao);

        if (receberEmails) {
            mudarEmailLottie.setSpeed(1);
            mudarEmailLottie.playAnimation();
        }

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

    private void setupItensConquistas() {
        //array de nomes, descrições, imagens e desbloqueado
        String[] nomes = {
                getString(R.string.beginner), //iniciante
                getString(R.string.verified), //verificado
                getString(R.string.editor), //editor
                getString(R.string.adder), //adicionador
                getString(R.string.manager), //gerenciador
                getString(R.string.apptriloveri), //apptrilover i
                getString(R.string.apptriloverii), //apptrilover ii
                getString(R.string.apptriloveriii), //apptrilover iii
                getString(R.string.apptriloveriv), //apptrilover iv
                getString(R.string.apptriloverv), //apptrilover v
                getString(R.string.apptrilovervi), //apptrilover vi
                getString(R.string.exploreri), //explorador i
                getString(R.string.explorerii), //explorador ii
                getString(R.string.exploreriii), //explorador iii
                getString(R.string.exploreriv), //explorador iv
                getString(R.string.explorerv), //explorador v
                getString(R.string.explorervi), //explorador vi
                getString(R.string.helperi), //ajudante i
                getString(R.string.helperii), //ajudante ii
                getString(R.string.helperiii), //ajudante iii
                getString(R.string.helperiv), //ajudante iv
                getString(R.string.judgei), //juiz i
                getString(R.string.judgeii), //juiz ii
                getString(R.string.judgeiii), //juiz iii
                getString(R.string.judgeiv), //juiz iv
                getString(R.string.judgev), //juiz v
                getString(R.string.judgevi) //juiz vi
        };

        String[] descs = {
                getString(R.string.begginerDesc), //inciante
                getString(R.string.verifiedDesc), //verificado
                getString(R.string.editorDesc), //editor
                getString(R.string.adderDesc), //adicionador
                getString(R.string.managerDesc), //gerenciador
                getString(R.string.apptriloverDesc).replaceAll("%LEVEL%", "5"), //apptrilover i
                getString(R.string.apptriloverDesc).replaceAll("%LEVEL%", "10"), //apptrilover ii
                getString(R.string.apptriloverDesc).replaceAll("%LEVEL%", "20"), //apptrilover iii
                getString(R.string.apptriloverDesc).replaceAll("%LEVEL%", "30"), //apptrilover iv
                getString(R.string.apptriloverDesc).replaceAll("%LEVEL%", "50"), //apptrilover v
                getString(R.string.apptriloverDesc).replaceAll("%LEVEL%", "100"), //apptrilover vi
                getString(R.string.explorer1Desc), //explorador i
                getString(R.string.explorer2Desc).replaceAll("%N%", "10"), //explorador ii
                getString(R.string.explorer2Desc).replaceAll("%N%", "25"), //explorador iii
                getString(R.string.explorer2Desc).replaceAll("%N%", "50"), //explorador iv
                getString(R.string.explorer2Desc).replaceAll("%N%", "100"), //explorador v
                getString(R.string.explorer2Desc).replaceAll("%N%", "500"), //explorador vi
                getString(R.string.helper1Desc), //ajudante i
                getString(R.string.helper2Desc).replaceAll("%N%", "10"), //ajudante ii
                getString(R.string.helper2Desc).replaceAll("%N%", "50"), //ajudante iii
                getString(R.string.helper2Desc).replaceAll("%N%", "100"), //ajudante iv
                getString(R.string.judge1Desc), //juiz i
                getString(R.string.judge2SDesc).replaceAll("%N%", "10"), //juiz ii
                getString(R.string.judge2SDesc).replaceAll("%N%", "25"), //juiz iii
                getString(R.string.judge2SDesc).replaceAll("%N%", "50"), //juiz iv
                getString(R.string.judge2SDesc).replaceAll("%N%", "100"), //juiz v
                getString(R.string.judge2SDesc).replaceAll("%N%", "500") //juiz vi
        };

        Drawable[] imgs = {
                utils.getDrawable(R.drawable.badge_novato), //iniciante
                utils.getDrawable(R.drawable.badge_verificado), //verificado
                utils.getDrawable(R.drawable.badge_editor), //editor
                utils.getDrawable(R.drawable.badge_adicionador), //adicionador
                utils.getDrawable(R.drawable.badge_gerenciadoricon), //gerenciador
                utils.getDrawable(R.drawable.badge_apptrilover), //apptrilover i
                utils.getDrawable(R.drawable.badge_apptrilover), //apptrilover ii
                utils.getDrawable(R.drawable.badge_apptrilover), //apptrilover iii
                utils.getDrawable(R.drawable.badge_apptrilover), //apptrilvoer iv
                utils.getDrawable(R.drawable.badge_apptrilover), //apptrilvoer v
                utils.getDrawable(R.drawable.badge_apptrilover), //apptrilover vi
                utils.getDrawable(R.drawable.badge_explorador), //explorador i
                utils.getDrawable(R.drawable.badge_explorador), //explorador ii
                utils.getDrawable(R.drawable.badge_explorador), //explorador iii
                utils.getDrawable(R.drawable.badge_explorador), //explorador iv
                utils.getDrawable(R.drawable.badge_explorador), //explorador v
                utils.getDrawable(R.drawable.badge_explorador), //explorador vi
                utils.getDrawable(R.drawable.badge_ajudante), //ajudante i
                utils.getDrawable(R.drawable.badge_ajudante), //ajudante ii
                utils.getDrawable(R.drawable.badge_ajudante), //ajudante iii
                utils.getDrawable(R.drawable.badge_ajudante), //ajudante iv
                utils.getDrawable(R.drawable.badge_juiz), //juiz i
                utils.getDrawable(R.drawable.badge_juiz), //juiz ii
                utils.getDrawable(R.drawable.badge_juiz), //juiz iii
                utils.getDrawable(R.drawable.badge_juiz), //juiz iv
                utils.getDrawable(R.drawable.badge_juiz), //juiz v
                utils.getDrawable(R.drawable.badge_juiz) //juiz vi
        };

        String[] cores = {
                "#bfffec", //iniciante
                "#ccff99", //verificado
                "#c4f9ff", //editor
                "#b0e0e6", //adicionador
                "#ffb394", //gerenciador
                "#EDB8C8", //apptrilover i
                "#EDB7CC", //apptrilover ii
                "#EDB7CE", //apptrilover iii
                "#E3B8D0", //apptrilover iv
                "#E0B5D0", //apptrilover v
                "#DAB1D1", //apptrilover vi
                "#ABD4C2", //explorador i
                "#AAD4BC", //explorador ii
                "#ACD2B9", //explorador iii
                "#B3D6B8", //explorador iv
                "#BBD8B9", //explorador v
                "#C2DCB9", //explorador vi
                "#F5EDBE", //ajudante i
                "#F4E8BE", //ajudante ii
                "#F2E4B7", //ajudante iii
                "#F1DEB3", //ajudante iv
                "#ECB6AA", //juiz i
                "#EDB7AD", //juiz ii
                "#EEB6B5", //juiz iii
                "#EEB6B7", //juiz iv
                "#EEB7BC", //juiz v
                "#EDB7C4" //juiz vi
        };

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        int level = User.getInstance().getLevel();
        boolean emailVerificado = User.getInstance().isEmailVerificado();
        boolean editor = User.getInstance().isPermissaoEditar();
        boolean addder = User.getInstance().isPermissaoAdicionar();
        boolean gerenciador = User.getInstance().isPermissaoGerenciador();
        int objetosAdicionados = User.getInstance().getObjetosAdicionados().length();
        int objetosVerificados = User.getInstance().getObjetosVerificados().length();
        int objetosEscaneados = User.getInstance().getCodigos().length();

        boolean[] desbl = {
                user != null, //iniciante
                emailVerificado, //verificado
                editor, //editor
                addder, //adicionador
                gerenciador, //gerenciador
                level >= 5, //apptrilover i
                level >= 10, //apptrilover ii
                level >= 20, //apptrilover iii
                level >= 30, //apptrilover iv
                level >= 50, //apptrilover v
                level >= 100, //apptrilover vi
                objetosEscaneados >= 1, //explorador i
                objetosEscaneados >= 10, //explorador ii
                objetosEscaneados >= 25, //explorador iii
                objetosEscaneados >= 50, //explorador iv
                objetosEscaneados >= 100, //explorador v
                objetosEscaneados >= 500, //explorador vi
                objetosAdicionados >= 1, //ajudante i
                objetosAdicionados >= 10, //ajudante ii
                objetosAdicionados >= 50, //ajudante iii
                objetosAdicionados >= 100, //ajudante iv
                objetosVerificados >= 1, //juiz i
                objetosVerificados >= 10, //juiz ii
                objetosVerificados >= 25, //juiz iii
                objetosVerificados >= 50, //juiz iv
                objetosVerificados >= 100, //juiz v
                objetosVerificados >= 500 //juiz vi
        };

        for (int i = 0; i < nomes.length; i++) {
            Drawable fundoItem = utils.getDrawable(R.drawable.background_branco);
            fundoItem.mutate().setColorFilter(Color.parseColor(cores[i]), PorterDuff.Mode.MULTIPLY);
            PerfilConquistaItem item = new PerfilConquistaItem(nomes[i], descs[i], fundoItem, imgs[i], desbl[i]);
            itensPerfilConquista.add(item);
        }

        PerfilConquistaItemAdapter adapter = new PerfilConquistaItemAdapter(itensPerfilConquista, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        listViewConquistas.setLayoutManager(gridLayoutManager);
        listViewConquistas.setAdapter(adapter);
        listViewConquistas.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                int gap = 10;
                int position = parent.getChildLayoutPosition(view);
                outRect.left = position % 2 == 0 ? 0 : gap;
                outRect.right = position % 2 == 0 ? gap : 0;
                outRect.bottom = gap;
                outRect.top = gap;
            }
        });

    }

    private void setupItensObjetosAdicionados() {
        try {
            JSONArray itens = User.getInstance().getObjetosAdicionados();

            for (int i = 0; i < itens.length(); i++) {
                JSONObject objeto = itens.getJSONObject(i);
                itensObjetosAdicionados.add(new GerenciarObjetoAdicionadoItem(objeto.getString("id"), objeto.getString("nome"), (i + 1), itens.length() - 1 == i));
            }

            layoutItensObjetosAdicionados.setVisibility(itensObjetosAdicionados.size() > 0 ? View.VISIBLE : View.GONE);

            GerenciarObjetoAdicionadoItemAdapter adapter = new GerenciarObjetoAdicionadoItemAdapter(itensObjetosAdicionados, Perfil.this);
            listViewObjetosAdicionados.setAdapter(adapter);
            listViewObjetosAdicionados.setLayoutManager(new LinearLayoutManager(this));

        } catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.profileAddedObjError), Toast.LENGTH_SHORT).show();
            Log.e("ERR perfilobjadd", e.getMessage());
            e.printStackTrace();
            layoutItensObjetosAdicionados.setVisibility(View.GONE);
        }
    }

    private void aoClicarBadge(String mensagem) {
        Utils.makeSnackbar(mensagem, findViewById(R.id.activity_perfil));
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