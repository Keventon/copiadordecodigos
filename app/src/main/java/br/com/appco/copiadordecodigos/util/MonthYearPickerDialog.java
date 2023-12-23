package br.com.appco.copiadordecodigos.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Calendar;

import br.com.appco.copiadordecodigos.R;

public class MonthYearPickerDialog extends Dialog {

    private DatePicker datePicker;
    private DatePickerListener listener;

    public MonthYearPickerDialog(@NonNull Context context, DatePickerListener listener) {
        super(context);
        this.listener = listener;
    }

    public interface DatePickerListener {
        void onDateSet(int year, int month);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_month_year_picker);

        datePicker = findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(false);
        datePicker.setSpinnersShown(true);

        // Configurar o DatePicker para iniciar com o mês e ano atuais
        Calendar calendar = Calendar.getInstance();
        datePicker.init(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                1,
                null
        );

        // Remover a exibição do campo do dia
        setOnShowListener(dialog -> {
            LinearLayout dayLinearLayout = datePicker.findViewById(Resources.getSystem().getIdentifier("day", "id", "android"));
            if (dayLinearLayout != null) {
                dayLinearLayout.setVisibility(View.GONE);
            }
        });

        Button confirmButton = findViewById(R.id.confirmButton);
        confirmButton.setOnClickListener(v -> {
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth();
            listener.onDateSet(selectedYear, selectedMonth);
            dismiss();
        });

        Button cancelButton = findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
