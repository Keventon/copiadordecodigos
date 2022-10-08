package br.com.appco.copiadordecodigos.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.AdicionarContaActivity;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPendentesBinding;
import br.com.appco.copiadordecodigos.model.Conta;

public class ContasPendentesFragment extends Fragment {

    private List<Conta> contas = new ArrayList<>();
    private List<Conta> contasFiltradas = new ArrayList<>();
    private ContaDAO dao;

    FragmentContasPendentesBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPendentesBinding.inflate(inflater, container, false);

        dao = new ContaDAO(getContext());
        contas = dao.listarContas();
        contasFiltradas.addAll(contas);
        ArrayAdapter<Conta> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, contasFiltradas);
        binding.listContas.setAdapter(adapter);

        binding.searchViewContasPendentes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                buscarConta(newText);
                return false;
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingAddConta.setOnClickListener(view1 ->  {
            startActivity(new Intent(requireContext(), AdicionarContaActivity.class));
        });
    }

    public void buscarConta(String nome) {
        contasFiltradas.clear();
        for (Conta c : contas) {
            if (c.getDescricao().toLowerCase().contains(nome.toLowerCase())) {
                contasFiltradas.add(c);
            }
        }

        binding.listContas.invalidateViews();
    }

    @Override
    public void onResume() {
        super.onResume();
        contas = dao.listarContas();
        contasFiltradas.clear();
        contasFiltradas.addAll(contas);
        binding.listContas.invalidateViews();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}