package dataStorage;

import java.util.ArrayList;

/**
 * Класс для хранения удаленного из матрицы столбца.
 */
public class DelColumn {
    private ArrayList<Fraction> column;
    private Fraction functionValue;
    private int columnIndex;
    private int varNum;

    public DelColumn(ArrayList<Fraction> column, Fraction functionValue, int columnIndex, int varNum) {
        this.column = column;
        this.functionValue = functionValue;
        this.columnIndex = columnIndex;
        this.varNum = varNum;
    }

    public ArrayList<Fraction> getColumn() {
        return column;
    }

    public Fraction getFunctionValue() {
        return functionValue;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public int getVarNum() {
        return varNum;
    }
}
