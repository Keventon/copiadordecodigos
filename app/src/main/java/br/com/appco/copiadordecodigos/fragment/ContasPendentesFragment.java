package br.com.appco.copiadordecodigos.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;

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
import br.com.appco.copiadordecodigos.activity.EscolherComprovanteActivity;
import br.com.appco.copiadordecodigos.adapter.ContaPendenteAdapter;
import br.com.appco.copiadordecodigos.controller.ConfiguracoesFirebase;
import br.com.appco.copiadordecodigos.controller.UsuarioFirebase;
import br.com.appco.copiadordecodigos.database.ContaDAO;
import br.com.appco.copiadordecodigos.databinding.FragmentContasPendentesBinding;
import br.com.appco.copiadordecodigos.listener.RecyclerItemClickListener;
import br.com.appco.copiadordecodigos.model.Boleto;
import br.com.appco.copiadordecodigos.util.DataAtual;
import br.com.appco.copiadordecodigos.util.GetMask;
import br.com.appco.copiadordecodigos.util.MoedaUtils;

public class ContasPendentesFragment extends Fragment {

    private List<Boleto> boletos = new ArrayList<>();
    private List<Boleto> contasFiltradas = new ArrayList<>();
    private ContaPendenteAdapter contaPendenteAdapter;
    private ContaDAO dao;
    private MaterialCalendarView calendarView;
    private String mesAnoSelecionado, nomeFarmacia = "";
    private double valor = 0.0;
    private Context context;
    private String data;
    private ProgressDialog progressDialog;
    private ValueEventListener valueEventListenerBoletos;
    private FirebaseAuth auth = ConfiguracoesFirebase.getFirebaseAutenticacao();

    FragmentContasPendentesBinding binding;
    DatabaseReference reference = ConfiguracoesFirebase.getFirebase();
    DatabaseReference boletoRef;

    private List<Boleto> boletosFiltered = new ArrayList<>();
    private List<String> boletosData = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentContasPendentesBinding.inflate(inflater, container, false);

        //Configura recycleView
        binding.recycleContas.setLayoutManager(new LinearLayoutManager(context));
        binding.recycleContas.setHasFixedSize(true);
        contaPendenteAdapter = new ContaPendenteAdapter(boletos, context);
        binding.recycleContas.setAdapter(contaPendenteAdapter);

        binding.imageCalendario.setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    getContext(), R.style.BottomSheetTheme
            );

            View bottomSheetView = LayoutInflater.from(getContext())
                    .inflate(
                            R.layout.escolher_data_mes,
                            (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                    );

            //Iniciando layouts
            Button buttonEscolherPeloDia = bottomSheetView.findViewById(R.id.buttonEscolherPeloDia);
            Button buttonEscolherPeloMes = bottomSheetView.findViewById(R.id.buttonEscolherPeloMes);

            buttonEscolherPeloMes.setOnClickListener(view1 -> {
                ViewGroup viewGroup = view.findViewById(android.R.id.content);

                Spinner spinner;
                Button buttonPesquisarBoleto;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_layout_escolher_mes, viewGroup, false);
                builder.setView(inflate);
                builder.setCancelable(true);

                spinner = inflate.findViewById(R.id.spinnerMesAno);
                buttonPesquisarBoleto = inflate.findViewById(R.id.buttonPesquisarBoletoDialog);

                carregarSpinnerMesesDoAno(spinner);

                final AlertDialog dialog = builder.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        if (i != 0) {
                            buttonPesquisarBoleto.setVisibility(View.VISIBLE);
                        }else {
                            buttonPesquisarBoleto.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });

                buttonPesquisarBoleto.setOnClickListener(view2 -> {
                    if (spinner.getSelectedItem().toString().equals("Janeiro")) {
                        buscarBoletoPorMes("01/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Fevereiro")) {
                        buscarBoletoPorMes("02/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Março")) {
                        buscarBoletoPorMes("03/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Abril")) {
                        buscarBoletoPorMes("04/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Maio")) {
                        buscarBoletoPorMes("05/2023");
                        dialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Junho")) {
                        buscarBoletoPorMes("06/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Julho")) {
                        buscarBoletoPorMes("07/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Agosto")) {
                        buscarBoletoPorMes("08/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Setembro")) {
                        buscarBoletoPorMes("09/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Outubro")) {
                        buscarBoletoPorMes("10/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Novembro")) {
                        buscarBoletoPorMes("11/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }else if (spinner.getSelectedItem().toString().equals("Dezembro")) {
                        buscarBoletoPorMes("12/2023");
                        dialog.dismiss();
                        bottomSheetDialog.dismiss();
                    }
                });

                dialog.show();
            });

            buttonEscolherPeloDia.setOnClickListener(view1 -> {
                abrirCalendario(view1);
                bottomSheetDialog.dismiss();
            });

            bottomSheetDialog.setContentView(bottomSheetView);
            bottomSheetDialog.show();
        });

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

        binding.textTodosBoletosPendentes.setOnClickListener(view -> carregarContas());

        binding.linear.setOnClickListener(view -> startActivity(new Intent(context, EscolherBoletoActivity.class)));

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

        return binding.getRoot();
    }

    private void buscarBoletoPorMes(String data) {
        valor = 0.0;

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
                        .child(nomeFarmacia).orderByChild("mes").equalTo(data);
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
                                    valor += boleto.getValor();
                                }

                            }

                            DecimalFormat format = new DecimalFormat("0.00");
                            binding.textValorBoletos.setText("R$ " + GetMask.getValor(valor));
                            boletosFiltered = new ArrayList<>(boletos);

                            contaPendenteAdapter.setData(boletos);
                        }else {
                            binding.textValorBoletos.setText(GetMask.getValor(0.0));
                            progressDialog.dismiss();
                            recuperarNomeFarmacia();
                            boletosFiltered = new ArrayList<>(boletos);
                            contaPendenteAdapter.setData(boletos);
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

    public void configuraCalendarView() {

        CharSequence meses[] = {"Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"};
        calendarView.setTitleMonths(meses);

        CalendarDay dataAtual = calendarView.getCurrentDate();
        String mesSelecionado = String.format("%02d", (dataAtual.getMonth() + 1));
        mesAnoSelecionado = String.valueOf((mesSelecionado) + "" + dataAtual.getYear());

        calendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
                String mesSelecionado = String.format("%02d", (date.getMonth() + 1));
                mesAnoSelecionado = String.valueOf((mesSelecionado) + "/" + date.getYear());



                reference.removeEventListener(valueEventListenerBoletos);
                recuperarBoletos();
            }
        });

    }

    private void carregarSpinnerMesesDoAno(Spinner spinner) {
        String[] meses = getResources().getStringArray(R.array.mesesDoAno);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                context, android.R.layout.simple_spinner_item,
                meses
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

    }

    private void recuperarBoletos() {

        valor = 0.0;

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
                                Boleto boleto = ds.getValue(Boleto.class);
                                recuperarNomeFarmacia();
                                boletosData.add(boleto.getDataValidade());
                                valor += boleto.getValor();

                            }

                            Toast.makeText(context, String.valueOf(boletosData), Toast.LENGTH_SHORT).show();



                            DecimalFormat format = new DecimalFormat("0.00");
                            binding.textValorBoletos.setText("R$ " + GetMask.getValor(valor));
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

        calendarView.setOnDateChangeListener((calendarView1, ano, mes, diadoMes) -> {
            int mesAno = mes + 1;

            if (diadoMes < 10) {
                if (mesAno < 10) {
                    data = "0" + diadoMes + "/0" + mesAno + "/" + ano;
                }else {
                    data = "0" + diadoMes + "/" + mesAno + "/" + ano;
                }
                buscarBoletoPorData(data);
            }else {
                String data;
                if (mesAno < 10) {
                    data = diadoMes + "/0" + mesAno + "/" + ano;
                }else {
                    data = diadoMes + "/" + mesAno + "/" + ano;
                }
                buscarBoletoPorData(data);
            }

        });

        builder.setOnDismissListener(dialogInterface -> carregarContas());

        buttonEscolherData.setOnClickListener(view1 -> dialog.dismiss());

        dialog.show();
    }

    public void buscarBoletoPorData(String data) {

        valor = 0.0;

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
                                    valor += boleto.getValor();
                                    boletos.add(ds.getValue(Boleto.class));
                                    recuperarNomeFarmacia();
                                }else {
                                    boletosFiltered = new ArrayList<>(boletos);
                                    contaPendenteAdapter.setData(boletos);
                                }

                            }
                            DecimalFormat format = new DecimalFormat("0.00");
                            binding.textValorBoletos.setText("R$ " + GetMask.getValor(valor));
                            boletosFiltered = new ArrayList<>(boletos);
                            contaPendenteAdapter.setData(boletos);
                        }else {
                            binding.textValorBoletos.setText("R$ " + GetMask.getValor(0.0));
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
                    nomeFarmacia = snapshot.getValue().toString();
                    binding.textNomeUsuarioContaPendente.setText(nomeFarmacia);
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
            builder.setMessage("Deseja realmente apagar o boleto?");
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
                                Toast.makeText(context, "Você não tem permissão para remover boletos", Toast.LENGTH_SHORT).show();
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

        textValor.setText("Valor do boleto: R$ " +GetMask.getValor((boleto.getValor())));
        textDataValidade.setText("Vencimento: " + boleto.getDataValidade());


        textValorComMulta.setVisibility(View.GONE);

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

            BottomSheetDialog bottomSheetDialog2 = new BottomSheetDialog(
                    getContext(), R.style.BottomSheetTheme
            );

            View bottomSheetView2 = LayoutInflater.from(getContext())
                    .inflate(
                            R.layout.menu_calendario_conta_pendente,
                            (LinearLayout) binding.getRoot().findViewById(R.id.bottomSheetContainer)
                    );

            Button buttonEscolherDataCalendario = bottomSheetView2.findViewById(R.id.buttonAbrirCalendario);
            Button buttonEscolherDataAtual = bottomSheetView2.findViewById(R.id.buttonDataAtual);

            buttonEscolherDataAtual.setOnClickListener(view1 -> {
                Boleto boleto1 = new Boleto();

                String mesAno = DataAtual.dataAtual().substring(3);

                boleto1.setStatus(1);
                boleto1.setDataPagamento(DataAtual.dataAtual());
                boleto1.setId(boleto.getId());
                boleto1.setCodigo(boleto.getCodigo());
                boleto1.setImagemComprovante("");
                boleto1.setMes(mesAno);
                boleto1.setNomeFarmacia(boleto.getNomeFarmacia());
                boleto1.setNomeEmpresa(boleto.getNomeEmpresa());
                boleto1.setValor(boleto.getValor());
                boleto1.setDataValidade(boleto.getDataValidade());
                boleto1.atualizar(((error, ref) -> {
                    Toast.makeText(context, "Boleto pago com sucesso", Toast.LENGTH_SHORT).show();
                    bottomSheetDialog.dismiss();
                    bottomSheetDialog2.dismiss();
                    Intent intent = new Intent(context, EscolherComprovanteActivity.class);
                    intent.putExtra("info_boleto", boleto);
                    startActivity(intent);
                }));
            });

            buttonEscolherDataCalendario.setOnClickListener(view1 -> {
                ViewGroup viewGroup = view.findViewById(android.R.id.content);

                CalendarView calendarView;
                Button buttonEscolherData;

                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                View inflate = LayoutInflater.from(context).inflate(R.layout.dialog_layout__escolher_data_calendario, viewGroup, false);
                builder.setView(inflate);
                builder.setCancelable(true);

                calendarView = inflate.findViewById(R.id.calendario);
                buttonEscolherData = inflate.findViewById(R.id.buttonEscolherDataCalendario);

                final AlertDialog dialog = builder.create();

                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                calendarView.setOnDateChangeListener((calendarView1, ano, mes, diadoMes) -> {
                    int mesAno = mes + 1;

                    if (diadoMes < 10) {
                        if (mesAno < 10) {
                            data = "0" + diadoMes + "/0" + mesAno + "/" + ano;
                        }else {
                            data = "0" + diadoMes + "/" + mesAno + "/" + ano;
                        }
                    }else {
                        if (mesAno < 10) {
                            data = diadoMes + "/0" + mesAno + "/" + ano;
                        }else {
                            data = diadoMes + "/" + mesAno + "/" + ano;
                        }
                    }

                    Boleto boleto1 = new Boleto();

                    String mesAno1 = data.substring(3);
                    boleto1.setStatus(1);
                    boleto1.setDataPagamento(data);
                    boleto1.setId(boleto.getId());
                    boleto1.setCodigo(boleto.getCodigo());
                    boleto1.setNomeFarmacia(boleto.getNomeFarmacia());
                    boleto1.setImagemComprovante("");
                    boleto1.setMes(mesAno + "/" + ano);
                    boleto1.setNomeEmpresa(boleto.getNomeEmpresa());
                    boleto1.setValor(boleto.getValor());
                    boleto1.setDataValidade(boleto.getDataValidade());
                    boleto1.atualizar(((error, ref) -> {
                        Toast.makeText(context, "Boleto pago com sucesso", Toast.LENGTH_SHORT).show();
                        bottomSheetDialog.dismiss();
                        bottomSheetDialog2.dismiss();
                        dialog.dismiss();
                        Intent intent = new Intent(context, EscolherComprovanteActivity.class);
                        intent.putExtra("info_boleto", boleto);
                        startActivity(intent);
                    }));

                });

                builder.setOnDismissListener(dialogInterface -> carregarContas());

                buttonEscolherData.setOnClickListener(view2 -> dialog.dismiss());

                dialog.show();
            });

            bottomSheetDialog2.setContentView(bottomSheetView2);
            bottomSheetDialog2.show();
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }

    public void carregarContas() {

        valor = 0.0;

        progressDialog = new ProgressDialog(getContext());
        progressDialog.show();
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        DatabaseReference nomeFarmaciaRef = reference
                .child("usuario")
                .child(UsuarioFirebase.getIdentificadorUsuario())
                .child("nomeFarmacia");

        nomeFarmaciaRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                nomeFarmacia = snapshot.getValue().toString();

                if (!nomeFarmacia.equals("")) {
                    Query boletoRef = reference
                            .child("boletos")
                            .child(nomeFarmacia).orderByChild("status").equalTo(0);
                    boletoRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            valor = 0.0;
                            boletos.clear();

                            if (snapshot.getValue() != null) {
                                progressDialog.dismiss();
                                //binding.textContasPendentes.setVisibility(View.GONE);
                                for (DataSnapshot ds: snapshot.getChildren()) {
                                    boletos.add(ds.getValue(Boleto.class));
                                    Boleto boleto = ds.getValue(Boleto.class);
                                    recuperarNomeFarmacia();
                                    valor += boleto.getValor();

                                }

                                contaPendenteAdapter.notifyDataSetChanged();
                                DecimalFormat format = new DecimalFormat("0.00");
                                binding.textValorBoletos.setText("R$ " + GetMask.getValor(valor));
                                boletosFiltered = new ArrayList<>(boletos);
                                contaPendenteAdapter.setData(boletos);
                            }else {
                                contaPendenteAdapter.notifyDataSetChanged();
                                boletosFiltered = new ArrayList<>(boletos);
                                contaPendenteAdapter.setData(boletos);
                                binding.textValorBoletos.setText("R$ " + GetMask.getValor(0));
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