package br.com.appco.copiadordecodigos.activity.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import br.com.appco.copiadordecodigos.R;
import br.com.appco.copiadordecodigos.databinding.ResButtonBackgroundBinding;

public class ButtonMain extends LinearLayout {
    public ResButtonBackgroundBinding binding;

    public ButtonMain(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        binding = ResButtonBackgroundBinding.inflate(LayoutInflater.from(context), this, true);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ButtonBackground);

        String textButton = typedArray.getString(R.styleable.ButtonBackground_textButton);
        if (textButton != null) {
            textButton(textButton);
        }

        binding.linearLayoutButtonBackground.setOnClickListener(null);

        typedArray.recycle();
    }

    public void textButton(String text) {
        binding.textView.setText(text);
    }

    public void buttonAtivado() {
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.textView.setText("Por favor, aguarde...");
    }

    public void buttonFinalizado() {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.verde));
        binding.constrainLayout.setBackgroundResource(R.drawable.button_custom_background_green);
        binding.progressBar.setVisibility(View.GONE);
        binding.textView.setTextColor(binding.textView.getResources().getColor(R.color.white));
        binding.textView.setText("Tudo certo :)");
    }

    public void buttonAcessar() {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.verde));
        binding.constrainLayout.setBackgroundResource(R.drawable.button_custom_background_green);
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.progressBar.setIndeterminateTintList(binding.progressBar.getResources().getColorStateList(R.color.white));
        binding.textView.setTextColor(binding.textView.getResources().getColor(R.color.white));
        binding.textView.setText("Acessando...");
    }

    public void buttonErro() {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.vermelho));
        binding.constrainLayout.setBackgroundResource(R.drawable.button_custom_background_red);
        binding.progressBar.setVisibility(View.GONE);
        binding.textView.setText("Ocorreu um erro :(");
    }

    public void buttonEncerrar() {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.verde));
        binding.constrainLayout.setBackgroundResource(R.drawable.button_custom_background_main);
        binding.progressBar.setVisibility(View.GONE);
        binding.textView.setTextColor(binding.textView.getResources().getColor(R.color.black));
        binding.textView.setText("Finalizar cadastro");
    }

    public void buttonLoginNovamente() {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.verde));
        binding.constrainLayout.setBackgroundResource(R.drawable.button_custom_background_main);
        binding.progressBar.setVisibility(View.GONE);
        binding.linearLayoutButtonBackground.setEnabled(true);
        binding.textView.setText("Acessar");
    }

    public void buttonDisable() {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.Stroke_Secondary));
        binding.constrainLayout.setBackgroundResource(R.drawable.custom_input_text_disable);
        binding.progressBar.setVisibility(View.GONE);
        binding.linearLayoutButtonBackground.setEnabled(false);
        binding.textView.setText("Acessar");
    }

    public void buttonTextCustom(String text, boolean isVisible) {
        binding.constrainLayout.setBackgroundColor(getResources().getColor(R.color.verde));
        binding.constrainLayout.setBackgroundResource(R.drawable.button_custom_background_main);
        binding.textView.setText(text);

        if (isVisible) {
            binding.progressBar.setVisibility(View.VISIBLE);
        }else {
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}
