package GenericProgramming;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FizzBuzzTest {
    private FizzBuzz obj;

    @BeforeEach
    void setUp() {
        obj = new FizzBuzz();
    }

    @Test
    void fizzBuzzTraditionTest() {
        obj.fizzBuzz();
    }

    @Test
    void fizzBuzzImprovedTest() {
        String str = obj.improvedFizzBuzz(1, 100, 3, 5,
                "Fizz", "Buzz");
        System.out.println(str);
    }

    @Test
    void finalFizzBuzzTest() {
        String str = obj.finalFizzBuzz(1, 100, 3, 5, "Fizz", "Buzz");
        System.out.println(str);
    }
}
