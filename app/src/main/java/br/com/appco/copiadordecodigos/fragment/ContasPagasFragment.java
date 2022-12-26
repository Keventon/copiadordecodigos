package br.com.appco.copiadordecodigos.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.EscolherBoletoActivity;
import br.com.appco.copiadordecodigos.activity.LoginActivity;
import br.com.appco.copiadordecodigos.adapter.ContaPagaAdapter;
import br.com.appco.copiadordecodigos.adapter.ContaPendenteAdapter;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPagasBinding;
import br.com.appco.copiadordecodigos.listener.RecyclerItemClickListener;
import br.com.appco.copiadordecodigos.model.Boleto;
import br.com.appco.copiadordecodigos.model.Conta;

public class ContasPagasFragment extends Fragment {

    private List<Boleto> boletos = new ArrayList<>();
    private List<Conta> contasFiltradas = new ArrayList<>();
    private ContaPagaAdapter contaPagaAdapter;
    private ContaDAO dao;
    private Context context;
    private FirebaseAuth auth = ConfiguracoesFirebase.getFirebaseAutenticacao();
    private DatabaseReference reference = ConfiguracoesFirebase.getFirebase();
    private ProgressDialog progressDialog;

    private List<Boleto> boletosFiltered = new ArrayList<>();

    FragmentContasPagasBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPagasBinding.inflate(inflater, container, false);

        //Configura recycleView
        binding.recycleContaPaga.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recycleContaPaga.setHasFixedSize(true);
        contaPagaAdapter = new ContaPagaAdapter(boletos, context);
        binding.recycleContaPaga.setAdapter(contaPagaAdapter);

        binding.textSairContaPaga.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(getContext(), LoginActivity.class));
        });

        binding.searchViewContasPagas.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (boletos.size() > 0) {
                    boletosFiltered = filterList(boletos, newText);
                    contaPagaAdapter.animateTo(boletosFiltered);
                    binding.recycleContaPaga.scrollToPosition(0);
                }
                return true;
            }
        });

        binding.recycleContaPaga.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        binding.recycleContaPaga,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Boleto boleto = boletosFiltered.get(position);
                                detalhesConta(boleto);
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

        List<Boleto> contaFiltro = new ArrayList<>();
        for (Boleto b : boletos) {
            if (b.getNomeEmpresa().toLowerCase().contains(nome.toLowerCase())) {
                contaFiltro.add(b);
            }
        }

        if (contaFiltro.isEmpty()) {

        }else {
            contaPagaAdapter.setFilteredList(contaFiltro);
        }
    }

    private List<Boleto> filterList(List<Boleto> produtos, String termo) {
        final List<Boleto> filteredList = new ArrayList<>();
        for (Boleto b : boletos) {
            if (b.getNomeEmpresa().toUpperCase(Locale.ROOT).contains(termo.toUpperCase(Locale.ROOT))) {
                filteredList.add(b);
            }
        }
        return filteredList;
    }

    public void detalhesConta(Boleto boleto) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetTheme
        );

        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.conta_paga_layout,
                        (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                );

        //Iniciando layouts
        TextView textNomeEmpresaContaPaga = bottomSheetView.findViewById(R.id.textNomeEmpresaContaPaga);
        TextView textValor = bottomSheetView.findViewById(R.id.textValorContaPaga);
        TextView textDataPagamento = bottomSheetView.findViewById(R.id.textDataPagamentoContaPaga);
        TextView textStatus = bottomSheetView.findViewById(R.id.textStatusContaPaga);
        TextView textVoltar = bottomSheetView.findViewById(R.id.texVoltarContaPaga);

        textNomeEmpresaContaPaga.setText(boleto.getNomeEmpresa());

        DecimalFormat format = new DecimalFormat("0.00");

        textValor.setText("Valor: R$ " + format.format(boleto.getValor()));
        textDataPagamento.setText("Pago no dia: " + boleto.getDataPagamento());

        textVoltar.setOnClickListener(view ->  {
            bottomSheetDialog.dismiss();
        });

        if (boleto.getStatus() == 1) {
            textStatus.setText("Pago");
        }

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void carregarContas() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference nomeFarmacia = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");

        nomeFarmacia.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nomeFarmacia = snapshot.getValue().toString();

                Query boletoRef = reference
                        .child("boletos")
                        .child(nomeFarmacia).orderByChild("status").equalTo(1);
                boletoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boletos.clear();

                        if (snapshot.exists()) {
                            progressDialog.dismiss();
                            //binding.textContasPagas.setVisibility(View.GONE);
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                boletos.add(ds.getValue(Boleto.class));

                            }
                            boletosFiltered = new ArrayList<>(boletos);
                            contaPagaAdapter.setData(boletos);
                        }else {
                            progressDialog.dismiss();
                            //binding.textContasPagas.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarNomeFarmacia() {

        DatabaseReference nomeRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");

        nomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {
                    String nomeFarmacia = snapshot.getValue().toString();
                    binding.textNomeFarmaciaContasPagas.setText("Você está na " + nomeFarmacia);
                }else {
                    Toast.makeText(context, "Erro", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        carregarContas();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}