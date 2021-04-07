package com.integrador.apptrimonio.Utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.integrador.apptrimonio.FragmentoCadastro;
import com.integrador.apptrimonio.FragmentoCamera;
import com.integrador.apptrimonio.FragmentoLogin;
import com.integrador.apptrimonio.FragmentoTelaInicial;

public class InicoAdaptador extends FragmentStateAdapter {

    public Fragment[] mFragments;

    public InicoAdaptador(FragmentActivity fa, ViewPager2 vp, View.OnClickListener clickListener) {
        super(fa);
        mFragments = new Fragment[]{
                new FragmentoTelaInicial(vp, clickListener),
                new FragmentoCamera(vp),
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
