package br.com.appco.copiadordecodigos.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.activity.AdicionarContaActivity;
import br.com.appco.copiadordecodigos.activity.EditarContaActivity;
import br.com.appco.copiadordecodigos.activity.EscolherBoletoActivity;
import br.com.appco.copiadordecodigos.activity.LoginActivity;
import br.com.appco.copiadordecodigos.adapter.ContaPendenteAdapter;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPendentesBinding;
import br.com.appco.copiadordecodigos.listener.RecyclerItemClickListener;
import br.com.appco.copiadordecodigos.model.Boleto;
import br.com.appco.copiadordecodigos.model.Conta;
import br.com.appco.copiadordecodigos.util.DataAtual;

public class ContasPendentesFragment extends Fragment {

    private List<Boleto> boletos = new ArrayList<>();
    private List<Boleto> contasFiltradas = new ArrayList<>();
    private ContaPendenteAdapter contaPendenteAdapter;
    private ContaDAO dao;
    private Context context;
    private ProgressDialog progressDialog;
    private FirebaseAuth auth = ConfiguracoesFirebase.getFirebaseAutenticacao();

    FragmentContasPendentesBinding binding;
    DatabaseReference reference = ConfiguracoesFirebase.getFirebase();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPendentesBinding.inflate(inflater, container, false);

        recuperarNomeFarmacia();

        //Configura recycleView
        binding.recycleContas.setLayoutManager(new LinearLayoutManager(context));
        binding.recycleContas.setHasFixedSize(true);
        contaPendenteAdapter = new ContaPendenteAdapter(boletos, context);
        binding.recycleContas.setAdapter(contaPendenteAdapter);

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

        binding.buttonEscolherFarmacia.setOnClickListener(view -> startActivity(new Intent(context, EscolherBoletoActivity.class)));

        binding.recycleContas.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        getContext(),
                        binding.recycleContas,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Boleto boleto = boletos.get(position);
                                detalhesConta(boleto);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Boleto boleto = boletos.get(position);
                                menuOpcoes(boleto);

                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                            }
                        }
                )
        );

        binding.textSairContaPendente.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(context, LoginActivity.class));
        });

        return binding.getRoot();
    }

    private void recuperarNomeFarmacia() {

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference nomeRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");

        nomeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressDialog.dismiss();
                String nomeFarmacia = snapshot.getValue().toString();
                binding.textNomeUsuario.setText("Você está na " + nomeFarmacia);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void menuOpcoes(Boleto boleto) {
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
            intent.putExtra("contaSelecionada", boleto);
            startActivity(intent);
        });

        buttonExcluir.setOnClickListener(view ->  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Confirmar exclusão");
            builder.setMessage("Deseja realmente apagar a conta: " + boleto.getDescricao() + "?");
            builder.setCancelable(false);
            builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                    DatabaseReference identificadorRef = reference
                            .child("usuario")
                            .child(UsuarioFirebase.getIdentificadorUsuario())
                            .child("identificador");
                    identificadorRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String identificador = snapshot.getValue().toString();

                            if (identificador.equals("A")) {
                                DatabaseReference contaRef = reference
                                        .child("boletos")
                                        .child(boleto.getNomeFarmacia())
                                        .child(boleto.getId());
                                contaRef.removeValue();
                                Toast.makeText(context, "Boleto removido com sucesso", Toast.LENGTH_SHORT).show();
                                bottomSheetDialog.dismiss();
                            }else {
                                Toast.makeText(context, "Você não tem permissão para reover boletos", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
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

    public void detalhesConta(Boleto boleto) {
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

        textDescricao.setText(boleto.getDescricao());

        String valorString = String.valueOf(boleto.getValor()).replace(".", ",");

        textValor.setText("Valor: R$" + valorString);
        textDataValidade.setText("Vencimento: " + boleto.getDataValidade());

        if (boleto.getCodigo().equals("null")) {
            textCodigo.setVisibility(View.GONE);
        }else {
            textCodigo.setText(boleto.getCodigo());
        }

        if (boleto.getStatus() == 0) {
            textStatus.setText("Não pago");
        }

        buttonCopiar.setOnClickListener(view ->  {
            if (!boleto.getCodigo().equals("null")) {
                ClipboardManager clipboardManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Copy", boleto.getCodigo());
                clipboardManager.setPrimaryClip(clipData);
                Toast.makeText(getContext(), "Código copiado com sucesso", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }else {
                Toast.makeText(getContext(), "Você não pode copiar", Toast.LENGTH_SHORT).show();
            }
        });

        buttonContaPaga.setOnClickListener(view ->  {
            //
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void buscarConta(String nome) {

        List<Boleto> contaFiltro = new ArrayList<>();
        for (Boleto b : boletos) {
            if (b.getDescricao().toLowerCase().contains(nome.toLowerCase())) {
                contaFiltro.add(b);
            }
        }

        if (contaFiltro.isEmpty()) {
            Toast.makeText(context, "Nenhuma conta encontrada", Toast.LENGTH_SHORT).show();
        }else {
            contaPendenteAdapter.setFilteredList(contaFiltro);
        }
    }

    public void carregarContas() {

        DatabaseReference nomeFarmacia = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");

        nomeFarmacia.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String nomeFarmacia = snapshot.getValue().toString();

                DatabaseReference produtosRef = reference
                        .child("boletos")
                        .child(nomeFarmacia);
                produtosRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        boletos.clear();

                        if (dataSnapshot.getValue() != null) {
                            for (DataSnapshot ds: dataSnapshot.getChildren()) {
                                boletos.add(ds.getValue(Boleto.class));
                                binding.textContas.setText("");

                            }
                            contaPendenteAdapter.notifyDataSetChanged();
                        }else {
                            binding.textContas.setText("Sem boletos pendentes");
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        boletos.clear();
        carregarContas();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (boletos.isEmpty()) {
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