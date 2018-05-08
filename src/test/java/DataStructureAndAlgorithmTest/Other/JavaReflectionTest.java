package DataStructureAndAlgorithmTest.Other;

import DataStructuresAndAlgorithms.Other.JavaReflection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.*;

public class JavaReflectionTest {
    private JavaReflection obj;

    @BeforeEach
    void setUp() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, InstantiationException, NoSuchFieldException {

    }

    @Test
    void displayDefaultArray() throws IllegalAccessException, NoSuchFieldException,
            InvocationTargetException, InstantiationException, NoSuchMethodException, ClassNotFoundException {
        System.out.println("Displaying randomly populated 2D array");
        Class cls = Class.forName("DataStructuresAndAlgorithms.Other.JavaReflection");

        //populate array
        Constructor defaultConsturctor = cls.getConstructor();
        obj = (JavaReflection) defaultConsturctor.newInstance();

        //allocate grid/array size
        int[] twoDimentions = {3, 4};
        Object gridObj = Array.newInstance(Integer.TYPE, twoDimentions);

        //set x and y dimentions
        Field xDimentionField = cls.getDeclaredField("xDimention");
        xDimentionField.setAccessible(true);
        xDimentionField.setInt(obj, twoDimentions[0]);

        Field yDimentionField = cls.getDeclaredField("yDimention");
        yDimentionField.setAccessible(true);
        yDimentionField.setInt(obj, twoDimentions[1]);

        //set grid with newly allocated array.
        Field gridField = cls.getDeclaredField("grid");
        gridField.setAccessible(true);
        gridField.set(obj, gridObj);

        obj.buildGrid();

        obj.displayArray();
    }


    @Test
    void displayArrayWithProvidedDimentions() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class cls = Class.forName("DataStructuresAndAlgorithms.Other.JavaReflection");

        Class[] ctorArgs = {Integer.TYPE, Integer.TYPE};
        Constructor argConstructor = cls.getDeclaredConstructor(ctorArgs);

        obj = (JavaReflection) argConstructor.newInstance(3, 4);

        obj.buildGrid();

        obj.displayArray();
    }

    @Test
    void methodInvocationTest() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class cls = Class.forName("DataStructuresAndAlgorithms.Other.JavaReflection");

        Method m = cls.getDeclaredMethod("dummyModuloCalculation", Integer.TYPE);
        m.setAccessible(true);

        Constructor ctor = cls.getDeclaredConstructor(Integer.TYPE, Integer.TYPE);

        obj = (JavaReflection) ctor.newInstance(3, 4);

        obj.buildGrid();
        obj.displayArray();

        double result = (double) m.invoke(obj, 5);
        System.out.println("Sample module result " + result);
    }
}
