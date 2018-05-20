package GenericProgramming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NumberFormatTest {
    private NumberFormatConversion conversion;

    @BeforeEach
    void setUp() {
        conversion = new NumberFormatConversion();
    }

    @Test
    void asciiTest() {
        String str = "1001";
        int num = (str.charAt(0) - '0');
        System.out.println(num);
    }

    @Test
    void convertBinaryToDecimal() {
        String binaryStr = "1001";
        int decimal = conversion.toDecimal(2, binaryStr);

        assertEquals(decimal, 9);

        binaryStr = "10001";
        decimal = conversion.toDecimal(2, binaryStr);
        assertEquals(decimal, 17);

        binaryStr = "1";
        decimal = conversion.toDecimal(2, binaryStr);
        assertEquals(decimal, 1);

        binaryStr = "0";
        decimal = conversion.toDecimal(2, binaryStr);
        assertEquals(decimal, 0);

        binaryStr = "10";
        decimal = conversion.toDecimal(2, binaryStr);
        assertEquals(decimal, 2);

    }

    @Test
    void nonSupportedBaseTest() {
        String binaryStr = "10";
        int decimal = conversion.toDecimal(1, binaryStr);
        assertEquals(decimal, -1);

        binaryStr = "10";
        decimal = conversion.toDecimal(17, binaryStr);
        assertEquals(decimal, -1);
    }

    @Test
    void convertDecimalToBinary() {
        String binaryRep = conversion.toBase(2, 17);
        assertEquals("10001", binaryRep);

        binaryRep = conversion.toBase(2, 27);
        assertEquals("11011", binaryRep);

        binaryRep = conversion.toBase(2, 8);
        assertEquals("1000", binaryRep);
    }

    @Test
    void convertHexToDecimal() {
        String hex = "25";

        int number = conversion.toDecimal(16, hex);
        assertEquals(37, number);

        number = conversion.toDecimal(16, "141");
        assertEquals(321, number);

        number = conversion.toDecimal(16, "4B");
        assertEquals(75, number);
    }

    @Test
    void binaryToHexTest() {
        String binary = "1001011";

        int dec = conversion.toDecimal(2, binary);
        System.out.println(dec);
        String hex = conversion.toBase(16, dec);

        assertEquals("4B", hex);
    }
}
