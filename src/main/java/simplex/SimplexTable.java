package simplex;

import java.util.ArrayList;

/**
 * Класс, для хранения текущего состояния задачи во время симплекс-метода.
 */
public class SimplexTable {
    private ArrayList<Fraction> function;
    private Matrix matrix;
    private ArrayList<Integer> base;
    private ArrayList<Integer> freeVars;
    private boolean isDecide;
    private String errorMassage;

    /**
     * Конструктор
     * @param function коэффициенты исходной функции
     * @param matrix матрица ограничений
     * @param base список номеров базисных переменных
     * @param freeVars список номеров свободных переменных
     * @param isDecide флаг решения задачи
     */
    public SimplexTable(
            ArrayList<Fraction> function,
            Matrix matrix,
            ArrayList<Integer> base,
            ArrayList<Integer> freeVars,
            boolean isDecide
    ) {
        this.function = function;
        this.matrix = matrix;
        this.base = base;
        this.freeVars = freeVars;
        this.isDecide = isDecide;
    }

    /**
     * Конструктор на случай обработки ошибок
     * @param errorMessage сообщение об ошибке
     */
    public SimplexTable(String errorMessage) {
        this.errorMassage = errorMessage;
    }

    /**
     * Конструктор для метода Гаусса (переводит объект класс <code>Task</code> в объект класса <code>SimplexTable</code>)
     * @param curTask объект класса <code>Task</code> содержащий исходную задачу.
     */
    public SimplexTable(Task curTask){
        ArrayList<Fraction> curFunction = curTask.getFunction();
        int countRows = curTask.getMatrix().getMatrix().length;
        int countColumns = curTask.getMatrix().getMatrix()[0].length;

        base = curTask.getBase();
        matrix = new Matrix(countRows, countColumns - base.size());

        // список свободных переменных
        freeVars = new ArrayList<>();
        for(int i = 0; i < curFunction.size() - 1; i++){
            if(!base.contains(i + 1)) {
                freeVars.add(i + 1);
            }
        }
        // редактирование коэффициентов функции
        function = new ArrayList<>();
        for (int i = 0; i < curFunction.size(); i++){
            if(!base.contains(i + 1)){
                function.add(curFunction.get(i));
            }
        }
        // редактирование матрицы

        for(int i = 0; i < countRows; i++){
            int countBase = 0;
            for(int j = 0; j < countColumns; j++){
                if(!base.contains(j + 1)){
                    matrix.addElementByIndex(i, j - countBase, curTask.getMatrix().getMatrix()[i][j]);
                } else {
                    countBase++;
                }
            }
        }
        isDecide = curTask.isDecide();

    }



    public ArrayList<Fraction> getFunction() {
        return function;
    }

    public void setFunction(ArrayList<Fraction> function) {
        this.function = function;
    }

    public Fraction[][] getMatrix() {
        return matrix.getMatrix();
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public ArrayList<Integer> getBase() {
        return base;
    }

    public void setBase(ArrayList<Integer> base) {
        this.base = base;
    }

    public ArrayList<Integer> getFreeVars() {
        return freeVars;
    }

    public void setFreeVars(ArrayList<Integer> freeVars) {
        this.freeVars = freeVars;
    }

    public boolean isDecide() {
        return isDecide;
    }

    public void setDecide(boolean decide) {
        isDecide = decide;
    }

    public String getErrorMassage() {
        return errorMassage;
    }

    public void setErrorMassage(String errorMassage) {
        this.errorMassage = errorMassage;
    }

    @Override
    public String toString(){
        String functionStr = "f = " + function.toString();
        String matrixStr = "Restrictions:\n" + matrix.toString();

        String baseStr = "Base: " + base.toString();
        String freeVStr = "Free: " + freeVars.toString();
        return functionStr + "\n" + matrixStr + baseStr + "\n" + freeVStr + "\n";
    }
}
