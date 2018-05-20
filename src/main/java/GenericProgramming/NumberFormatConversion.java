package GenericProgramming;

import java.util.HashMap;
import java.util.Map;

public class NumberFormatConversion {
    private final Map<String, Integer> hexMapToDec;
    private final Map<Integer, String> hexMapToHex;

    public NumberFormatConversion() {
        this.hexMapToDec = new HashMap<>();
        hexMapToDec.put("A", 10);
        hexMapToDec.put("B", 11);
        hexMapToDec.put("C", 12);
        hexMapToDec.put("D", 13);
        hexMapToDec.put("E", 14);
        hexMapToDec.put("F", 15);

        hexMapToHex = new HashMap<>();
        hexMapToHex.put(hexMapToDec.get("A"), "A");
        hexMapToHex.put(hexMapToDec.get("B"), "B");
        hexMapToHex.put(hexMapToDec.get("C"), "C");
        hexMapToHex.put(hexMapToDec.get("D"), "D");
        hexMapToHex.put(hexMapToDec.get("E"), "E");
        hexMapToHex.put(hexMapToDec.get("F"), "F");
    }

    public int toDecimal(int base, String number){
        int result = 0;
        int strLen = number.length();
        int numberAtChar = -1;

        if(base < 2 || base > 16){
            System.out.println("Number format not supported with given base.");
            return -1;
        }

        for(int i = number.length(); i > 0; i--){
            if(base == 2)
                result += Math.pow(base, strLen - i)* (number.charAt(i - 1) - '0');
            if(base == 16){
                numberAtChar = number.charAt(i - 1) - '0';
                numberAtChar = numberAtChar >= 0 && numberAtChar < 10 ? numberAtChar : hexMapToDec.get(String.valueOf(number.charAt(i - 1)));
                result += Math.pow(base, strLen - i)* (numberAtChar);
            }
        }

        return result;
    }

    public String toBase(int base, int decimal){
        StringBuffer stringBuffer = new StringBuffer();

        if(base < 2 || base > 16){
            System.out.println("Number format not supported.");
            return null;
        }

        while(decimal >= base){
            if (base == 2)
                stringBuffer.append(decimal % base);
            if(base == 16)
                stringBuffer.append(hexMapToHex.get(decimal % base));

            decimal /= base;
        }

        stringBuffer.append(decimal);

        return stringBuffer.reverse().toString();
    }
}
