package barcode.check.service;

import barcode.check.util.BarcodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BarcodeService extends BarcodeUtil {

    public void checkBarcode(String barcode){
        boolean collectionDocument = barcode.startsWith("8");
        String result = "";
        if(collectionDocument) result =collectionCheck(barcode);
        System.out.println(result);
    }

    private String collectionCheck(String barcode){
        List<String> digits = new ArrayList<>(Arrays.stream(barcode.split("")).toList());
        final int LIMIT = 12;

        if(digits.size() != 48) return "O boleto não corresponde a essa categoria.";

        Integer segmentDigit = Integer.parseInt(digits.get(1));
        Integer referenceDigit = Integer.parseInt(digits.get(2));
        String type = verifyTypeByReferenceDigit(referenceDigit);
        String documentValue = barcode.substring(4,11) +  barcode.substring(12,16);
        if(type == "TEN")
            if(!verifyModuleTen(digits, true)) return "Boleto com numeração inválida.";
        else if(type == "ELEVEN")
            if(!verifyModuleEleven(digits,true))  return "Boleto com numeração inválida.";

        digits = new ArrayList<>(Arrays.stream(barcode.split("")).toList());
        for(int range = 1; range <= 4; range++) {
          var interval = LIMIT *range;
          List<String> target = digits.stream().filter(d -> !Objects.equals(d, "-")).limit(LIMIT).toList();
          if(type == "TEN")
            if(!verifyModuleTen(target,false)) return "Boleto com numeração inválida.";
          else if(type == "ELEVEN")
              if(!verifyModuleEleven(digits,true))  return "Boleto com numeração inválida.";

          AtomicInteger i = new AtomicInteger(0);
          digits = digits.stream().map(t -> {
              if (i.getAndIncrement() >= interval) return t;
              return t.replace(t, "-");
          }).toList();
        }

        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);

       return new StringBuilder().append("+++++ BOLETO A SER PAGO +++++").append("\nSegmento: ").append(checkSegment(segmentDigit))
               .append("\nValor do documento: ").append("R$ ").append(df.format(Float.parseFloat(documentValue)/100))
               .append("\nDocumento validado")
                .append("\n+++++++++++++++++++++++++++++").toString();
    }

}