package br.com.appco.copiadordecodigos.activity.components;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;

import br.com.appco.copiadordecodigos.databinding.DialogLayoutContaIncorretaBinding;


public class CustomAlertDialog {

    private AlertDialog dialog;

    public CustomAlertDialog(Context context, int iconResId, String title, String message, String textButton, String textButtonCancel, boolean buttonCancelisVisible, int buttonColor, View.OnClickListener onPositiveButtonClick, View.OnClickListener onCancelButtonClick) {
        DialogLayoutContaIncorretaBinding binding = DialogLayoutContaIncorretaBinding.inflate(LayoutInflater.from(context));

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setView(binding.getRoot());
        builder.setCancelable(false);

        dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        // Configurando os elementos do layout com os valores fornecidos
        binding.iconDialog.setImageResource(iconResId);
        binding.titleDialog.setText(title);
        binding.messageDialog.setText(message);
        binding.buttonDialog.setBackgroundResource(buttonColor);
        binding.buttonDialog.setText(textButton);
        binding.buttonCancel.setText(textButtonCancel);

        if (buttonCancelisVisible) {
            binding.buttonCancel.setVisibility(View.VISIBLE);
        }else {
            binding.buttonCancel.setVisibility(View.GONE);
        }

        binding.buttonDialog.setOnClickListener(onPositiveButtonClick);

        binding.buttonCancel.setOnClickListener(view -> {
            onCancelButtonClick.onClick(view);
            dialog.dismiss();
        });
    }

    public void show() {
        dialog.show();
    }

    public void dismiss() {
        dialog.dismiss();
    }
}