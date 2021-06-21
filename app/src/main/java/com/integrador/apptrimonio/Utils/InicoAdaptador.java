package com.integrador.apptrimonio.Utils;

import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.integrador.apptrimonio.FragmentoCamera;
import com.integrador.apptrimonio.FragmentoTelaInicial;

public class InicoAdaptador extends FragmentStateAdapter {

    public Fragment[] mFragments;

    public InicoAdaptador(FragmentActivity fa, View.OnClickListener clickListener, Context context) {
        super(fa);
        mFragments = new Fragment[]{
                new FragmentoTelaInicial(clickListener),
                new FragmentoCamera(context),
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
