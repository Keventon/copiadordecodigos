package br.com.appco.copiadordecodigos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.model.Boleto;

public class ContaPagaAdapter extends RecyclerView.Adapter<ContaPagaAdapter.MyViewHolder> {

    private List<Boleto> listaBoletos;
    private Context context;

    public ContaPagaAdapter(List<Boleto> boletos, Context context) {
        this.listaBoletos = boletos;
        this.context = context;
    }

    public void setFilteredList(List<Boleto> filteredList) {
        this.listaBoletos = filteredList;
        notifyDataSetChanged();
    }

    public void setData(List<Boleto> boletos){
        this.listaBoletos = new ArrayList<>(boletos);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.conta_paga, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Boleto boleto = listaBoletos.get(position);

        holder.dataVencimento.setText("Conta paga no dia: " + boleto.getDataPagamento());
        holder.textNomeEmpresaContaPaga.setText("Empresa: " + boleto.getNomeEmpresa());

        if (boleto.getStatus() == 1) {
            holder.status.setText("Pago");
        }
    }

    @Override
    public int getItemCount() {
        return this.listaBoletos.size();
    }

    private void addItem(int position, Boleto boleto){
        listaBoletos.add(position, boleto);
        notifyItemChanged(position);
    }
    private Boleto removeItem(int position){
        final Boleto empresaRemoved = listaBoletos.remove(position);
        notifyItemRemoved(position);
        return empresaRemoved;

    }
    private void moveItem(int fromPostition, int toPosition){
        final Boleto empresaMoved = listaBoletos.remove(fromPostition);
        listaBoletos.add(toPosition, empresaMoved);
        notifyItemMoved(fromPostition, toPosition);
    }

    public void animateTo(List<Boleto> boletos){
        applyAndAnimateAdditions(boletos);
        applyAndAnimateRemovals(boletos);
        applyAndAnimateMovedItems(boletos);
    }

    private void applyAndAnimateRemovals(List<Boleto> newContas) {
        for (int i = listaBoletos.size() - 1; i >= 0; i--) {
            final Boleto model = listaBoletos.get(i);
            if (!newContas.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Boleto> newBoletos) {
        for (int i = 0, count = newBoletos.size(); i < count; i++) {
            final Boleto model = newBoletos.get(i);
            if (!listaBoletos.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Boleto> newBoletos) {
        for (int toPosition = newBoletos.size() - 1; toPosition >= 0; toPosition--) {
            final Boleto model = newBoletos.get(toPosition);
            final int fromPosition = listaBoletos.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView status, dataVencimento, textNomeEmpresaContaPaga;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            status = itemView.findViewById(R.id.textStatusContaPaga);
            dataVencimento = itemView.findViewById(R.id.dataPagamentoContaPaga);
            textNomeEmpresaContaPaga = itemView.findViewById(R.id.textNomeEmpresaContaPaga);
        }
    }
}
