package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ProgressBar;

import com.integrador.apptrimonio.Utils.ActivityBase;
import com.integrador.apptrimonio.Utils.User;
import com.skydoves.progressview.ProgressView;

public class Perfil extends ActivityBase {

    private int xp, xpNecessario, xpAtual, level;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);

        User user = User.getInstance();
        xp = user.getXp();
        xpAtual = user.getXpNextLevelXP();
        xpNecessario = user.getNextLevelXP();

        //widgets
        ProgressView xpProgress = findViewById(R.id.perfil_progress);
        xpProgress.setLabelText(xpAtual+"/"+xpNecessario);
        xpProgress.setProgress((float) (xpAtual * 100 / xpNecessario));
    }
}