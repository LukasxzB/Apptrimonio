package com.integrador.apptrimonio;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.AutenticadorAdaptador;
import com.integrador.apptrimonio.Utils.UserInterface;
import com.integrador.apptrimonio.Utils.Utils;

import java.util.Objects;

public class Autenticar extends ActivityBase {

    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth auth;

    private Utils utils;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticar);

        utils = new Utils(this);

        ViewPager2 viewPager = findViewById(R.id.autenticar_viewpager2);
        viewPager.setAdapter(new AutenticadorAdaptador(this, viewPager, this));

        Button botaoAutenticarGoogle = findViewById(R.id.autenticar_botao_google);

        //autenticação com o google
        auth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        botaoAutenticarGoogle.setOnClickListener(v -> loginGoogle());
    }

    private void loginGoogle() { //ao clicar em fazer login com o google
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void loginFirebaseGoogle(String idToken) {
        AuthCredential credencial = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credencial).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                //login com sucesso
                Log.d("LOGIN", "signInWithEmail:success");
                Toast.makeText(Autenticar.this, getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                UserInterface callback = login -> loginCallback();
                utils.verificarConta(callback);
            } else {
                Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                Utils.makeSnackbar(getResources().getString(R.string.loginFail) + Objects.requireNonNull(task.getException()).getMessage(), findViewById(R.id.activity_autenticar));
            }
        });
    }

    private void loginCallback() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //resultado do login com o google
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                Log.d("LOGIN", "Login feito com o Google: " + account.getId());
                loginFirebaseGoogle(account.getIdToken());
            } catch (ApiException e) {

                Log.w("ERRO", "Login com o Google falhou: " + e);
            }
        }
    }
}