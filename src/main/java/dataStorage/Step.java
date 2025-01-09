package dataStorage;

import java.util.ArrayList;

/**
 * Класс для хранения данных о симплекс-шаге
 */
public class Step {
    private int num;
    private ArrayList<Integer> support;
    private String method;
    private DelColumn delColumn;
    private ArrayList<DelString> delStrings;
    private boolean endAB = false;

    public static String SIMPLEX_METHOD = "simplex";
    public static String ARTIFICIAL_BASIS_METHOD = "artificial_basis_method";

    /**
     * Конструктор БЕЗ удаленного столбца
     * @param num порядковый номер шага
     * @param support координаты опорного элемента
     * @param method метод решения (симплекс, ИБ)
     */
    public Step(int num, ArrayList<Integer> support, String method) {
        this.num = num;
        this.support = support;
        this.method = method;
    }

    public int getNum() {
        return num;
    }

    public ArrayList<Integer> getSupport() {
        return support;
    }

    public String getMethod() {
        return method;
    }

    public DelColumn getDelColumn() {
        return delColumn;
    }

    public boolean getEndAB() {
        return endAB;
    }

    public void setEndAB(boolean endAB) {
        this.endAB = endAB;
    }

    public void setDelColumn(DelColumn delColumn) {
        this.delColumn = delColumn;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public ArrayList<DelString> getDelStrings() {
        return delStrings;
    }

    public void setDelStrings(ArrayList<DelString> delStrings) {
        this.delStrings = delStrings;
    }

    @Override
    public String toString() {
        String numStr = "\nНомер шага: " + num;
        String supportStr = "\nКоординаты опорного элемента: " + support;
        String methodStr = "\nМетод: " + method;
        String endAbStr = "\nПоследний шаг ИБ: " + endAB;
        return numStr + supportStr + methodStr + endAbStr;
    }
}
