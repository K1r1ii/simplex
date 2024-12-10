package dataStorage;

import java.util.ArrayList;

/**
 * Класс для хранения исходной задачи
 */
public class Task {
    private ArrayList<Fraction> function;
    private Matrix matrix;
    private ArrayList<Integer> base;
    private boolean isDecide;
    private String errorMessage;

    /**
     * Конструктор для сохранения задачи со списком номеров базисных переменных
     * @param function список коэффициентов исходной функции
     * @param matrix матрица ограничений
     * @param isDecide флаг решения задачи
     * @param base список с номерами базисных переменных
     */
    public Task(ArrayList<Fraction> function, Matrix matrix, boolean isDecide, ArrayList<Integer> base){
        this.function = function;
        this.matrix = matrix;
        this.isDecide = isDecide;
        this.base = base;
    }

    /**
     * Конструктор для сохранения задачи без списка номеров базисных переменных (для метода искусственного базиса)
     * @param function список коэффициентов исходной функции
     * @param matrix матрица ограничений
     * @param isDecide флаг решения задачи
     */
    public Task(ArrayList<Fraction> function, Matrix matrix, boolean isDecide){
        this.function = function;
        this.matrix = matrix;
        this.isDecide = isDecide;
        this.base = null;
    }

    /**
     * Конструктор для обработки ошибок
     * @param errorMessage сообщение об ошибке
     */
    public Task(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public ArrayList<Fraction> getFunction() {
        return function;
    }

    public void setFunction(ArrayList<Fraction> function) {
        this.function = function;
    }

    public Matrix getMatrix() {
        return matrix;
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

    public boolean isDecide() {
        return isDecide;
    }

    public void setDecide(boolean decide) {
        isDecide = decide;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString(){
        String functionStr = "f = " + function.toString();
        String matrixStr = "Restrictions:\n" + matrix.toString();

        if(base != null){
            String baseStr = "Base: " + base.toString();
            return functionStr + "\n" + matrixStr + baseStr + "\n";
        } else {
            return functionStr + "\n" + matrixStr;
        }
    }
}
