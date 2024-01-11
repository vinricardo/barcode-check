package barcode.check.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public class BarcodeUtil {
    public String verifyTypeByReferenceDigit(Integer digit){
        if(digit == 6 || digit == 7)
            return "TEN";
        else if(digit == 8 || digit == 9)
            return "ELEVEN";
        return "";
    }

    public List<String> removeVerificationDigits(List<String> digits) {
        Integer[] indexesToDelete = new Integer[]{11,23,35,47};
        Arrays.stream(indexesToDelete).toList().forEach(index -> {
            digits.remove((int) index);
            digits.add(index,"");
        });
        return digits;
    }

    public Boolean verifyModuleTen(List<String> digits, Boolean fourthDigit){
        AtomicInteger i = new AtomicInteger(0);
        if(fourthDigit){
            digits = removeVerificationDigits(digits).stream().filter(f -> !Objects.equals(f, "")).toList();
        }
        List<String> fields = new LinkedList<>(digits);
        String last = fourthDigit ? fields.remove((int) 3) : fields.removeLast();
        int sum = fields.reversed().stream().map(Integer::parseInt).reduce(0, (a,b) -> {
            AtomicInteger temp = new AtomicInteger(b);
            if(i.getAndIncrement() % 2 == 0) temp.set(temp.get()*2);
            int length = (int) (Math.log10(temp.get()) + 1);
            if(length > 1){
                AtomicInteger c = new AtomicInteger();
                Arrays.stream(String.valueOf(temp.get()).split("")).map(Integer::parseInt).forEach(c::addAndGet);
                return a + c.get();
            }
            return a + temp.get();
        });
        int rest = sum % 10;
        int digit = 10 - rest;
        return digit == Integer.parseInt(last);
    }

    public Boolean verifyModuleEleven(List<String> digits, Boolean fourthDigit){
        AtomicInteger i = new AtomicInteger(0);
        AtomicInteger j = new AtomicInteger(0);
        if(fourthDigit){
            digits = removeVerificationDigits(digits).stream().filter(f -> !Objects.equals(f, "")).toList();
        }
        Integer[] valuesToMultiply = new Integer[]{2,3,4,5,6,7,8,9};
        List<String> fields = new LinkedList<>(digits);
        String selected = fourthDigit ? fields.remove((int) 3) : fields.removeLast();
        int sum = fields.reversed().stream().map(Integer::parseInt).reduce(0, (a,b) -> {
            AtomicInteger temp = new AtomicInteger(b);
            if(j.getAndIncrement() == 45) return a;
            temp.set(temp.get()*valuesToMultiply[i.getAndIncrement()]);
            if(i.get() == 8) i.set(0);
            return a + temp.get();
        });
        int rest  = sum%11;
        int digit = 0;
        if(rest == 0 || rest == 1) digit = 0;
        else if(rest == 10) digit = 1;
        else digit = rest;
        return Integer.parseInt(selected) == digit;
    }

    public String checkSegment(Integer field){
        String segment = "";
        switch (field){
            case 1:  segment = "Prefeituras";break;
            case 2:  segment = "Saneamento";break;
            case 3: segment = "Energia Elétrica e Gás";break;
            case 4: segment = "Telecomunicações";break;
            case 5 : segment ="Órgãos Governamentais";break;
            case 6: segment =  "Carnes e Assemelhados ou demais empresas / Órgãos que serão identificadas através do CNPJ"; break;
            case 7: segment = "Multas de trânsito";break;
            case 9: segment = "Uso exclusivo do banco";break;
            default: segment= "Segmento não identificado";break;
        }
        return segment;
    }
}
