package com.integrador.apptrimonio.Utils;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.integrador.apptrimonio.FragmentoCadastro;
import com.integrador.apptrimonio.fragmento_login;


public class AutenticadorAdaptador extends FragmentStateAdapter {

    private final Fragment[] mFragments = new Fragment[]{
            new FragmentoCadastro(),
            new fragmento_login(),
    };

    public AutenticadorAdaptador(FragmentActivity fa) {
        super(fa);
    }

    @Override
    public int getItemCount() {
        return mFragments.length;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments[position];
    }
}
