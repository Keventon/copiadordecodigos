package br.com.appco.copiadordecodigos.fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.adapter.ContaPagaAdapter;
import br.com.appco.copiadordecodigos.adapter.ContaPendenteAdapter;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPagasBinding;
import br.com.appco.copiadordecodigos.listener.RecyclerItemClickListener;
import br.com.appco.copiadordecodigos.model.Conta;

public class ContasPagasFragment extends Fragment {

    private List<Conta> contas = new ArrayList<>();
    private List<Conta> contasFiltradas = new ArrayList<>();
    private ContaPagaAdapter contaPagaAdapter;
    private ContaDAO dao;
    private Context context;

    FragmentContasPagasBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPagasBinding.inflate(inflater, container, false);

        binding.searchViewContasPagas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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

        binding.recycleContaPaga.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        binding.recycleContaPaga,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Conta conta = contas.get(position);
                                detalhesConta(conta);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        return binding.getRoot();
    }

    public void buscarConta(String nome) {

        List<Conta> contaFiltro = new ArrayList<>();
        for (Conta c : contas) {
            if (c.getDescricao().toLowerCase().contains(nome.toLowerCase())) {
                contaFiltro.add(c);
            }
        }

        if (contaFiltro.isEmpty()) {
            Toast.makeText(getContext(), "Nenhuma conta encontrada", Toast.LENGTH_SHORT).show();
        }else {
            contaPagaAdapter.setFilteredList(contaFiltro);
        }
    }

    public void detalhesConta(Conta conta) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetTheme
        );

        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.conta_paga_layout,
                        (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                );

        //Iniciando layouts
        TextView textDescricao = bottomSheetView.findViewById(R.id.textDescricaoContaPaga);
        TextView textValor = bottomSheetView.findViewById(R.id.textValorContaPaga);
        TextView textDataPagamento = bottomSheetView.findViewById(R.id.textDataPagamentoContaPaga);
        TextView textStatus = bottomSheetView.findViewById(R.id.textStatusContaPaga);
        TextView textVoltar = bottomSheetView.findViewById(R.id.texVoltarContaPaga);

        textDescricao.setText(conta.getDescricao());

        String valorString = String.valueOf(conta.getValor()).replace(".", ",");

        textValor.setText("Valor: R$" + valorString);
        textDataPagamento.setText("Pago no dia: " + conta.getDataPagamento());

        textVoltar.setOnClickListener(view ->  {
            bottomSheetDialog.dismiss();
        });

        if (conta.getStatus() == 1) {
            textStatus.setText("Pago");
        }

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void carregarContas() {

        ContaDAO contaDAO = new ContaDAO(getContext());
        contas = contaDAO.listarContasPagas();

        contaPagaAdapter = new ContaPagaAdapter(contas, getContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycleContaPaga.setLayoutManager(layoutManager);
        binding.recycleContaPaga.setHasFixedSize(true);
        binding.recycleContaPaga.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        binding.recycleContaPaga.setAdapter(contaPagaAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        contas.clear();
        carregarContas();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (contas.isEmpty()) {
            carregarContas();
            binding.textContasPagas.setVisibility(View.VISIBLE);
        }else {
            carregarContas();
            binding.textContasPagas.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}