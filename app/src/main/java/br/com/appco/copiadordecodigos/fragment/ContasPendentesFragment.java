package br.com.appco.copiadordecodigos.fragment;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.AdicionarContaActivity;
import br.com.appco.copiadordecodigos.activity.EditarContaActivity;
import br.com.appco.copiadordecodigos.adapter.ContaPendenteAdapter;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPendentesBinding;
import br.com.appco.copiadordecodigos.listener.RecyclerItemClickListener;
import br.com.appco.copiadordecodigos.model.Conta;
import br.com.appco.copiadordecodigos.util.DataAtual;

public class ContasPendentesFragment extends Fragment {

    private List<Conta> contas = new ArrayList<>();
    private List<Conta> contasFiltradas = new ArrayList<>();
    private ContaPendenteAdapter contaPendenteAdapter;
    private ContaDAO dao;
    private Context context;

    FragmentContasPendentesBinding binding;
    DatabaseReference reference = ConfiguracoesFirebase.getFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPendentesBinding.inflate(inflater, container, false);

        recuperarNomeUsuario();

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

        binding.recycleContas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        binding.recycleContas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Conta conta = contas.get(position);
                                detalhesConta(conta);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Conta conta = contas.get(position);
                                menuOpcoes(conta);

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        return binding.getRoot();
    }

    private void recuperarNomeUsuario() {
        DatabaseReference nomeRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nome");

        nomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nome = snapshot.getValue().toString();
                binding.textNomeUsuario.setText("Olá, " + nome);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void menuOpcoes(Conta conta) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetTheme
        );

        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.menu_opcoes_layout,
                        (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                );

        //Iniciando layouts
        Button buttonEditar = bottomSheetView.findViewById(R.id.buttonEditar);
        Button buttonExcluir = bottomSheetView.findViewById(R.id.buttonExcluir);

        buttonEditar.setOnClickListener(view ->  {
            bottomSheetDialog.dismiss();
            Intent intent = new Intent(getContext(), EditarContaActivity.class);
            intent.putExtra("contaSelecionada", conta);
            startActivity(intent);
        });

        buttonExcluir.setOnClickListener(view ->  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirmar exclusão");
            builder.setMessage("Deseja realmente apagar a conta: " + conta.getDescricao() + "?");
            builder.setCancelable(false);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    ContaDAO contaDAO = new ContaDAO(getContext());

                    if (contaDAO.deletar(conta)) {
                        bottomSheetDialog.dismiss();
                        carregarContas();
                        Toast.makeText(context, "Conta excluída com sucesso", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(context, "Erro ao excluir conta", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancelar", null);

            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.floatingAddConta.setOnClickListener(view1 ->  {
            startActivity(new Intent(requireContext(), AdicionarContaActivity.class));
        });
    }

    public void detalhesConta(Conta conta) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                getContext(), R.style.BottomSheetTheme
        );

        View bottomSheetView = LayoutInflater.from(getContext())
                .inflate(
                        R.layout.conta_pendente_layout,
                        (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                );

        //Iniciando layouts
        TextView textDescricao = bottomSheetView.findViewById(R.id.textDescricaoConta);
        TextView textValor = bottomSheetView.findViewById(R.id.textValorConta);
        TextView textDataValidade = bottomSheetView.findViewById(R.id.textDataVencimentoConta);
        TextView textStatus = bottomSheetView.findViewById(R.id.textStatusConta);
        TextView textCodigo = bottomSheetView.findViewById(R.id.textCodigoBarraConta);
        Button buttonCopiar = bottomSheetView.findViewById(R.id.buttonCopiarCodigoConta);
        Button buttonContaPaga = bottomSheetView.findViewById(R.id.buttonContaPaga);

        textDescricao.setText(conta.getDescricao());

        String valorString = String.valueOf(conta.getValor()).replace(".", ",");

        textValor.setText("Valor: R$" + valorString);
        textDataValidade.setText("Vencimento: " + conta.getDataValidade());

        if (conta.getCodigo().equals("null")) {
            textCodigo.setVisibility(View.GONE);
        }else {
            textCodigo.setText(conta.getCodigo());
        }

        if (conta.getStatus() == 0) {
            textStatus.setText("Não pago");
        }

        buttonCopiar.setOnClickListener(view ->  {
            if (!conta.getCodigo().equals("null")) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copy", conta.getCodigo());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Código copiado com sucesso", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }else {
                Toast.makeText(getContext(), "Você não pode copiar", Toast.LENGTH_SHORT).show();
            }
        });

        buttonContaPaga.setOnClickListener(view ->  {
            dao = new ContaDAO(getContext());

            conta.setCodigo(conta.getCodigo());
            conta.setDescricao(conta.getDescricao());
            conta.setId(conta.getId());
            conta.setStatus(1);
            conta.setDataValidade(conta.getDataValidade());
            conta.setValor(conta.getValor());
            conta.setDataPagamento(DataAtual.dataAtual());

            if (dao.atualizar(conta)) {
                bottomSheetDialog.dismiss();
                Toast.makeText(getContext(), "Conta paga com sucesso", Toast.LENGTH_SHORT).show();
                contaPendenteAdapter.notifyDataSetChanged();
                carregarContas();
            }else {
                Toast.makeText(getContext(), "Erro ao pagar conta", Toast.LENGTH_SHORT).show();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void buscarConta(String nome) {

        List<Conta> contaFiltro = new ArrayList<>();
        for (Conta c : contas) {
            if (c.getDescricao().toLowerCase().contains(nome.toLowerCase())) {
                contaFiltro.add(c);
            }
        }

        if (contaFiltro.isEmpty()) {
            Toast.makeText(context, "Nenhuma conta encontrada", Toast.LENGTH_SHORT).show();
        }else {
            contaPendenteAdapter.setFilteredList(contaFiltro);
        }
    }

    public void carregarContas() {

        ContaDAO contaDAO = new ContaDAO(getContext());
        contas = contaDAO.listar();

        contaPendenteAdapter = new ContaPendenteAdapter(contas, getContext());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.recycleContas.setLayoutManager(layoutManager);
        binding.recycleContas.setHasFixedSize(true);
        binding.recycleContas.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayout.VERTICAL));
        binding.recycleContas.setAdapter(contaPendenteAdapter);
        contaPendenteAdapter.notifyDataSetChanged();
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
            binding.textContas.setVisibility(View.VISIBLE);
        }else {
            carregarContas();
            binding.textContas.setVisibility(View.GONE);
        }
    }

    public void onAttach(Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}