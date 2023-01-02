package br.com.appco.copiadordecodigos.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
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
import br.com.appco.copiadordecodigos.activity.AdicionarContaActivity;
import br.com.appco.copiadordecodigos.activity.CadastrarFuncionarioActivity;
import br.com.appco.copiadordecodigos.activity.ContasActivity;
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
import br.com.appco.copiadordecodigos.model.Usuario;
import br.com.appco.copiadordecodigos.util.Base64Custom;
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

    private List<Boleto> boletosFiltered = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPendentesBinding.inflate(inflater, container, false);

        //Configura recycleView
        binding.recycleContas.setLayoutManager(new LinearLayoutManager(context));
        binding.recycleContas.setHasFixedSize(true);
        contaPendenteAdapter = new ContaPendenteAdapter(boletos, context);
        binding.recycleContas.setAdapter(contaPendenteAdapter);

        binding.imageCalendario.setOnClickListener(this::abrirCalendario);

        binding.searchViewContasPendentes.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (boletos.size() > 0) {
                    boletosFiltered = filterList(boletos, newText);
                    contaPendenteAdapter.animateTo(boletosFiltered);
                    binding.recycleContas.scrollToPosition(0);
                }
                return true;
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
                                Boleto boleto = boletosFiltered.get(position);
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

        binding.textCadastrarFuncionarioContaPendente.setOnClickListener(view -> {
            auth.signOut();
            startActivity(new Intent(context, CadastrarFuncionarioActivity.class));
        });

        return binding.getRoot();
    }

    private void abrirCalendario(View view) {
        ViewGroup viewGroup = view.findViewById(android.R.id.content);

        CalendarView calendarView;
        Button buttonEscolherData;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_layout_calendario, viewGroup, false);
        builder.setView(inflate);
        builder.setCancelable(true);

        calendarView = inflate.findViewById(R.id.calendarView);
        buttonEscolherData = inflate.findViewById(R.id.buttonEscolherDataContaPendente);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int ano, int mes, int diadoMes) {
                int mesAno = mes + 1;

                if (diadoMes < 10) {
                    String data = "0" + diadoMes + "/0" + mesAno + "/" + ano;
                    buscarBoletoPorData(data);
                }else {
                    String data = diadoMes + "/0" + mesAno + "/" + ano;
                    buscarBoletoPorData(data);
                }

            }
        });

        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                carregarContas();
            }
        });

        buttonEscolherData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void buscarBoletoPorData(String data) {
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
                        .child(nomeFarmacia).orderByChild("dataValidade").equalTo(data);
                boletoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boletos.clear();

                        if (snapshot.getValue() != null) {
                            progressDialog.dismiss();
                            //binding.textContasPendentes.setVisibility(View.GONE);
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                Boleto boleto = ds.getValue(Boleto.class);
                                if (boleto.getStatus() == 0) {
                                    boletos.add(ds.getValue(Boleto.class));
                                    recuperarNomeFarmacia();
                                }else {
                                    boletosFiltered = new ArrayList<>(boletos);
                                    contaPendenteAdapter.setData(boletos);
                                }

                            }
                            boletosFiltered = new ArrayList<>(boletos);
                            contaPendenteAdapter.setData(boletos);
                        }else {
                            progressDialog.dismiss();
                            recuperarNomeFarmacia();
                            boletosFiltered = new ArrayList<>(boletos);
                            contaPendenteAdapter.setData(boletos);
                            //binding.textContasPendentes.setVisibility(View.VISIBLE);
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

    private void verificarNivelAcesso() {
        DatabaseReference acessoRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("identificador");
        acessoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String acesso = snapshot.getValue().toString();
                if (acesso.equals("A")) {
                    binding.textCadastrarFuncionarioContaPendente.setVisibility(View.VISIBLE);
                }else {
                    binding.textCadastrarFuncionarioContaPendente.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperarNomeFarmacia() {

        //verificarNivelAcesso();

        DatabaseReference nomeRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");

        nomeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.getValue() != null) {
                    String nomeFarmacia = snapshot.getValue().toString();
                    binding.textNomeUsuarioContaPendente.setText("Você está na " + nomeFarmacia);
                }
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
                                startActivity(new Intent(context, ContasActivity.class));
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
                        R.layout.conta_pendente_layout,
                        (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                );

        //Iniciando layouts
        TextView textNomeEmpresaConta = bottomSheetView.findViewById(R.id.textNomeEmpresaConta);
        TextView textValor = bottomSheetView.findViewById(R.id.textValorConta);
        TextView textDataValidade = bottomSheetView.findViewById(R.id.textDataVencimentoConta);
        TextView textStatus = bottomSheetView.findViewById(R.id.textStatusConta);
        TextView textValorComMulta = bottomSheetView.findViewById(R.id.textValorComMulta);
        TextView textCodigo = bottomSheetView.findViewById(R.id.textCodigoBarraConta);
        Button buttonCopiar = bottomSheetView.findViewById(R.id.buttonCopiarCodigoConta);
        Button buttonContaPaga = bottomSheetView.findViewById(R.id.buttonContaPaga);

        textNomeEmpresaConta.setText(boleto.getNomeEmpresa());

        DecimalFormat format = new DecimalFormat("0.00");

        String valorString = String.valueOf(boleto.getValor()).replace(".", ",");

        textValor.setText("Valor do boleto em R$: " + format.format(boleto.getValor()));
        textDataValidade.setText("Vencimento: " + boleto.getDataValidade());
        textValorComMulta.setText("Valor dos juros por dia em R$: " + format.format(boleto.getValorMulta()));

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
            Boleto boleto1 = new Boleto();
            boleto1.setStatus(1);
            boleto1.setDataPagamento(DataAtual.dataAtual());
            boleto1.setId(boleto.getId());
            boleto1.setCodigo(boleto.getCodigo());
            boleto1.setNomeFarmacia(boleto.getNomeFarmacia());
            boleto1.setValorMulta(boleto.getValorMulta());
            boleto1.setNomeEmpresa(boleto.getNomeEmpresa());
            boleto1.setValor(boleto.getValor());
            boleto1.setDataValidade(boleto.getDataValidade());
            boleto1.atualizar(((error, ref) -> {
                Toast.makeText(context, "Boleto pago com sucesso", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
                startActivity(new Intent(getContext(), ContasActivity.class));
            }));
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
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
            contaPendenteAdapter.setFilteredList(contaFiltro);
        }
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
                        .child(nomeFarmacia).orderByChild("status").equalTo(0);
                boletoRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boletos.clear();

                        if (snapshot.getValue() != null) {
                            progressDialog.dismiss();
                            //binding.textContasPendentes.setVisibility(View.GONE);
                            for (DataSnapshot ds: snapshot.getChildren()) {
                                boletos.add(ds.getValue(Boleto.class));
                                recuperarNomeFarmacia();

                            }
                            boletosFiltered = new ArrayList<>(boletos);
                            contaPendenteAdapter.setData(boletos);
                        }else {
                            progressDialog.dismiss();
                            recuperarNomeFarmacia();
                            //binding.textContasPendentes.setVisibility(View.VISIBLE);
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

    @Override
    public void onStart() {
        super.onStart();
        carregarContas();
    }

    @Override
    public void onResume() {
        super.onResume();
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