package com.integrador.apptrimonio;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.firebase.auth.FirebaseAuth;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class FragmentoLogin extends Fragment {

    private final ViewPager2 viewPager2;

    private EditText inputEmail, inputSenha;
    private TextView inputEmailTop;

    private FirebaseAuth mAuth;

    public FragmentoLogin(ViewPager2 vp) {
        viewPager2 = vp;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fragmento_login, container, false);

        inputEmail = view.findViewById(R.id.login_input_email);
        inputSenha = view.findViewById(R.id.login_input_senha);
        inputEmailTop = view.findViewById(R.id.login_input_email_top);
        Button entrar = view.findViewById(R.id.login_botao);
        TextView cadastrese = view.findViewById(R.id.login_nao_possui);
        TextView esqueceuSenha = view.findViewById(R.id.login_esqueceu_senha);

        //ao clicar em entrar
        entrar.setOnClickListener(v -> fazerLogin());

        //ao clicar em nao possui uma conta
        cadastrese.setOnClickListener(v -> viewPager2.setCurrentItem(0, true));

        //ao clicar em esqueceu a senha
        esqueceuSenha.setOnClickListener(v -> {
            Dialog dialog = new Dialog(getContext());
            dialog.setContentView(R.layout.popup_esqueceusenha);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(true);
            dialog.show();

            EditText dialogEmailInput = dialog.findViewById(R.id.esqueceusenha_input_email);
            TextView dialogEmailInputTop = dialog.findViewById(R.id.esqueceusenha_input_emailtop);
            Button dialogBotao = dialog.findViewById(R.id.esqueceusenha_botao);
            ImageView dialogFechar = dialog.findViewById(R.id.esqueceusenha_fechar);

            //ao clicar em enviar email
            dialogBotao.setOnClickListener(v1 -> {
                String dialogEmail = dialogEmailInput.getText().toString().trim();
                boolean dialogEmailValido = EmailValidator.getInstance().isValid(dialogEmail);

                if(!dialogEmailValido){
                    dialogEmailInputTop.setTextColor(getResources().getColor(R.color.vermelho));
                }else{
                    Toast.makeText(getContext(), getResources().getString(R.string.sendEmailWait), Toast.LENGTH_SHORT).show();
                    dialogEmailInputTop.setTextColor(getResources().getColor(R.color.verde4));
                    FirebaseAuth.getInstance().sendPasswordResetEmail(dialogEmail).addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            Toast.makeText(getContext(), getResources().getString(R.string.sendEmailSuccess), Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        }else{
                            Toast.makeText(getContext(), getResources().getString(R.string.sendEmailFail) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });

            //ao clicar em fechar
            dialogFechar.setOnClickListener(v12 -> dialog.dismiss());
        });

        return view;
    }

    private void fazerLogin() {
        String email = inputEmail.getText().toString().trim(); //email
        String senha = inputSenha.getText().toString().trim(); //senha

        boolean emailValido = EmailValidator.getInstance().isValid(email); //caso o email for valido
        boolean senhaValida = senha.length() >= 0 && senha != null; //caso a senha tiver mais de 8 caracteres

        if (!emailValido) {
            inputEmailTop.setTextColor(getResources().getColor(R.color.vermelho));
            Toast.makeText(getContext(), getResources().getString(R.string.invEmail), Toast.LENGTH_SHORT).show();
        } else {
            inputEmailTop.setTextColor(getResources().getColor(R.color.verde4));
        }

        if(!senhaValida){
            inputSenha.setTextColor(getResources().getColor(R.color.vermelho));
            Toast.makeText(getContext(), getResources().getString(R.string.invPass), Toast.LENGTH_SHORT).show();
        }else{
            inputSenha.setTextColor(getResources().getColor(R.color.verde4));
        }

        //faz login
        if(emailValido && senhaValida){
            mAuth.signInWithEmailAndPassword(email, senha)
                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), task -> {
                        if (task.isSuccessful()) {
                            Log.d("LOGIN", "signInWithEmail:success");
                            Toast.makeText(getContext(), getResources().getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                            getActivity().finish();
                        } else {
                            Log.w("LOGIN", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getContext(), getResources().getString(R.string.loginFail) + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}