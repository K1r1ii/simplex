package simplex;

import java.util.Arrays;

public class Matrix {
    private Fraction[][] matrix;

    public Matrix(int rows, int columns){
        matrix = new Fraction[rows][columns];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                matrix[i][j] = new Fraction(0);
            }
        }
    }

    public Matrix(Fraction[][] matrix){
        this.matrix = matrix;
    }

    public void addElementByIndex(int i, int j, Fraction element){
        matrix[i][j] = element;
    }

    public void replaceRows(int i, int j){
        Fraction[] firstRow  = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = firstRow;
    }

    public void replaceColumns(int i, int j){
        // change matrix
        for(int iRow = 0; iRow < matrix.length; iRow++){
            Fraction firstC = matrix[iRow][i];
            matrix[iRow][i] = matrix[iRow][j];
            matrix[iRow][j] = firstC;
        }
    }

    public void multiplyRowByNumber(int i, Fraction number){
        for(int j = 0; j < matrix[i].length; j++){
            matrix[i][j] = matrix[i][j].multiply(number);
        }
    }

    public void nullingRow(int iNullingRow, int iCurrentRow, int curColumn){
        Fraction firstEl = matrix[iNullingRow][curColumn];
        for(int i = curColumn; i < matrix[iNullingRow].length; i++){
            matrix[iNullingRow][i] = matrix[iNullingRow][i]
                    .difference(matrix[iCurrentRow][i]
                            .multiply(firstEl));
        }
    }

    public void printM(String msg){
        System.out.println("Step: " + msg);

        int maxLen = String.valueOf(matrix[0].length).length() + 1;
        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                maxLen = Math.max(fractions[j].toString().length(), maxLen);
            }
        }

        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                String format = "%" + maxLen + "s ";
                System.out.printf(format, fractions[j].toString());
            }
            System.out.print("\n");
        }
    }

    @Override
    public String toString(){
        String mtxStr = "";
        int maxLen = String.valueOf(matrix[0].length).length() + 1;
        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                maxLen = Math.max(fractions[j].toString().length(), maxLen);
            }
        }

        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                String format = "%" + maxLen + "s ";
                mtxStr += String.format(format, fractions[j].toString());
            }
            mtxStr += "\n";

        }
        return mtxStr;
    }

    public Fraction[][] getMatrix(){
        return this.matrix;
    }

    public void setMatrix(Fraction[][] newMatrix){
        this.matrix = newMatrix;
    }
}
