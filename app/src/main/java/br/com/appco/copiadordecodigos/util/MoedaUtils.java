package br.com.appco.copiadordecodigos.util;

import java.text.NumberFormat;
import java.util.Locale;

public class MoedaUtils {

    public static String formatarMoeda(double valor){
        Locale localBrasil = new Locale("pt", "BR");
        return NumberFormat.getCurrencyInstance(localBrasil).format(valor);
    }
}
