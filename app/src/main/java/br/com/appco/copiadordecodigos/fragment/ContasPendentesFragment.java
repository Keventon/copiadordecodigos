package br.com.appco.copiadordecodigos.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.AdicionarContaActivity;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPendentesBinding;

public class ContasPendentesFragment extends Fragment {

    FragmentContasPendentesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentContasPendentesBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingAddConta.setOnClickListener(view1 ->  {
            startActivity(new Intent(requireContext(), AdicionarContaActivity.class));
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}