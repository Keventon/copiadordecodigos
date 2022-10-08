package br.com.appco.copiadordecodigos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.model.Conta;

public class ContaPendenteAdapter extends RecyclerView.Adapter<ContaPendenteAdapter.MyViewHolder> {

    private List<Conta> listaContas;
    private Context context;

    public ContaPendenteAdapter(List<Conta> contas, Context context) {
        this.listaContas = contas;
        this.context = context;
    }

    public void setFilteredList(List<Conta> filteredList) {
        this.listaContas = filteredList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemLista = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.conta_pendente, parent, false);

        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Conta conta = listaContas.get(position);

        holder.descricao.setText(conta.getDescricao());
        holder.dataVencimento.setText("Vencimento: " + conta.getDataValidade());

        if (conta.getStatus() == 0) {
            holder.status.setText("Não pago");
        }
    }

    @Override
    public int getItemCount() {
        return this.listaContas.size();
    }

    private void addItem(int position, Conta conta){
        listaContas.add(position, conta);
        notifyItemChanged(position);
    }
    private Conta removeItem(int position){
        final Conta empresaRemoved = listaContas.remove(position);
        notifyItemRemoved(position);
        return empresaRemoved;

    }
    private void moveItem(int fromPostition, int toPosition){
        final Conta empresaMoved = listaContas.remove(fromPostition);
        listaContas.add(toPosition, empresaMoved);
        notifyItemMoved(fromPostition, toPosition);
    }

    public void animateTo(List<Conta> contas){
        applyAndAnimateAdditions(contas);
        applyAndAnimateRemovals(contas);
        applyAndAnimateMovedItems(contas);
    }

    private void applyAndAnimateRemovals(List<Conta> newContas) {
        for (int i = listaContas.size() - 1; i >= 0; i--) {
            final Conta model = listaContas.get(i);
            if (!newContas.contains(model)) {
                removeItem(i);
            }
        }
    }

    private void applyAndAnimateAdditions(List<Conta> newContas) {
        for (int i = 0, count = newContas.size(); i < count; i++) {
            final Conta model = newContas.get(i);
            if (!listaContas.contains(model)) {
                addItem(i, model);
            }
        }
    }

    private void applyAndAnimateMovedItems(List<Conta> newContas) {
        for (int toPosition = newContas.size() - 1; toPosition >= 0; toPosition--) {
            final Conta model = newContas.get(toPosition);
            final int fromPosition = listaContas.indexOf(model);
            if (fromPosition >= 0 && fromPosition != toPosition) {
                moveItem(fromPosition, toPosition);
            }
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView descricao, status, dataVencimento;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            descricao = itemView.findViewById(R.id.textDescricao);
            status = itemView.findViewById(R.id.textStatus);
            dataVencimento = itemView.findViewById(R.id.textVencimento);
        }
    }
}
