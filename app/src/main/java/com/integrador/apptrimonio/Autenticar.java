package com.integrador.apptrimonio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.integrador.apptrimonio.Utils.AutenticadorAdaptador;

import java.util.ArrayList;

public class Autenticar extends AppCompatActivity {

    private ViewPager2 viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_autenticar);

        viewPager = findViewById(R.id.autenticar_viewpager2);
        viewPager.setAdapter(new AutenticadorAdaptador(this, viewPager));
    }

}