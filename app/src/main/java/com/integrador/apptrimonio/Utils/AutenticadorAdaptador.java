package com.integrador.apptrimonio.Utils;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.integrador.apptrimonio.FragmentoCadastro;
import com.integrador.apptrimonio.FragmentoLogin;


public class AutenticadorAdaptador extends FragmentStateAdapter {

    private final Fragment[] mFragments;

    public AutenticadorAdaptador(FragmentActivity fa, ViewPager2 vp) {
        super(fa);
        mFragments = new Fragment[]{
                new FragmentoCadastro(vp),
                new FragmentoLogin(vp),
        };
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
