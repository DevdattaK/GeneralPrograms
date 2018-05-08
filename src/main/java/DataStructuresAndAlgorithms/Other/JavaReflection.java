package DataStructuresAndAlgorithms.Other;

import java.util.Arrays;
import java.util.Random;

public class JavaReflection {
    private int xDimention;
    private int yDimention;
    private int[][] grid;

    public JavaReflection(){
    }

    public JavaReflection(int xDimention, int yDimention){
        this.xDimention = xDimention;
        this.yDimention = yDimention;
        grid = new int[xDimention][yDimention];
    }

    public int getxDimention() {
        return xDimention;
    }

    public int getyDimention() {
        return yDimention;
    }

    public int[][] getGrid() {
        return grid;
    }


    public void buildGrid() {
        for (int i = 0; i < xDimention; i++){
            for(int j = 0; j < yDimention; j++){
                grid[i][j] = (new Random()).nextInt(10);
            }
        }
    }

    public void displayArray() {
        int i, j;
        for(i = 0; i < xDimention; i++){
            System.out.print("{");
            for(j = 0; j < yDimention; j++){
                System.out.print((j < (yDimention - 1)) ? grid[i][j] + ", " : grid[i][j]);
            }

            System.out.print((i < (xDimention -1)) ? "}," : "}");
            System.out.println();
        }
    }

    private double dummyModuloCalculation(int moduloOp){
        //calculate sum of all elements in grid
        long sum = Arrays.stream(grid).map(oneDimension -> Arrays.stream(oneDimension))
                                .flatMapToInt(f -> f)
                                .sum();

        System.out.println("Sum : " + sum);
        //sum % moduloOp

        return sum % moduloOp;
    }
}
