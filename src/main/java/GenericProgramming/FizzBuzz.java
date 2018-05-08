package GenericProgramming;

import com.sun.org.apache.xpath.internal.operations.Bool;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class FizzBuzz {

    public void fizzBuzz() {
        for(int i = 0; i < 100; i++){
            if(i % 15 == 0)
                System.out.println(i + "FizzBuzz");
            else if(i % 3 == 0)
                System.out.println(i + "Fizz");
            else if(i % 5 == 0)
                System.out.println(i + "Buzz");
            else
                System.out.println(i);
        }
    }

    public String improvedFizzBuzz(int start, int end, int firstModulo, int secondModulo,
                                   String firstWord, String secondWord) {
        return IntStream.rangeClosed(start, end)
                .mapToObj(i -> i + " " + ((i % firstModulo) == 0 ? firstWord : "")
                        + ((i % secondModulo) == 0 ? secondWord : ""))
                .collect(Collectors.joining("\n"));
    }

    public String finalFizzBuzz(int start, int end, int firstModulo, int secondModulo,
                                String firstWord, String secondWord){

        BiFunction<Integer, Integer, Boolean> moduleResult = (number, modulo) -> number % modulo == 0;

        BiFunction<Boolean, String, String> resultBuilder = (modRes, word) -> modRes ? word : "";

        return IntStream.rangeClosed(start, end)
                        .mapToObj(i -> i + " " + (resultBuilder.apply(moduleResult.apply(i, firstModulo), firstWord)
                                        + resultBuilder.apply(moduleResult.apply(i, secondModulo), secondWord)))
                        .collect(Collectors.joining("\n"));
    }
}
