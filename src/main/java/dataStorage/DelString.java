package dataStorage;

import java.util.ArrayList;

/**
 * Класс для хранения удаленного из матрицы столбца.
 */
public class DelString {
    private ArrayList<Fraction> string;
    private int stringIndex;
    private int baseNum;

    public DelString(ArrayList<Fraction> string, int stringIndex, int baseNum) {
        this.string = string;
        this.stringIndex = stringIndex;
        this.baseNum = baseNum;
    }

    public ArrayList<Fraction> getString() {
        return string;
    }

    public int getStringIndex() {
        return stringIndex;
    }

    public int getBaseNum() {
        return baseNum;
    }
}
