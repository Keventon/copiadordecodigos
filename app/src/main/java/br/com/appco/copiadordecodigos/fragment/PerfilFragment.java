package br.com.appco.copiadordecodigos.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.ConfiguracoesActivity;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPendentesBinding;
import br.com.appco.copiadordecodigos.databinding.FragmentSobreBinding;

public class PerfilFragment extends Fragment {

    private FragmentSobreBinding binding;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSobreBinding.inflate(inflater, container, false);

        binding.cardConfiguracoes.setOnClickListener(view -> startActivity(new Intent(context, ConfiguracoesActivity.class)));

        return binding.getRoot();
    }

    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}