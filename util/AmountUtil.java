package util;

import java.text.NumberFormat;
import java.util.Locale;

public class AmountUtil {
    public static String formatTotalAmount(double totalAmount){
         NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US);
            formatter.setMinimumFractionDigits(2);
            formatter.setMaximumFractionDigits(2);

         return formatter.format(totalAmount);
    }
}
