package simplex;

import java.util.ArrayList;

/**
 * Класс, для хранения данных о решении задачи (аргументы и значение функции)
 */
public class Solution {
    private Fraction functionValue;
    private ArrayList<Fraction> vectorSolution;
    private String errorMessage;

    /**
     * Конструктор для сохранения решения.
     * @param functionValue оптимальное значение функции
     * @param vectorSolution список аргументов для оптимального значения функции
     */
    public Solution(Fraction functionValue, ArrayList<Fraction> vectorSolution){
        this.functionValue = functionValue;
        this.vectorSolution = vectorSolution;
    }

    /**
     * Конструктор для обработки ошибок
     * @param errorMessage сообщение об ошибке.
     */
    public Solution(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public Fraction getFunctionValue() {
        return functionValue;
    }

    public void setFunctionValue(Fraction functionValue) {
        this.functionValue = functionValue;
    }

    public ArrayList<Fraction> getVectorSolution() {
        return vectorSolution;
    }

    public void setVectorSolution(ArrayList<Fraction> vectorSolution) {
        this.vectorSolution = vectorSolution;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String toString(){
        return "f = " + functionValue.toString() + "\n" + "x = " + vectorSolution.toString();
    }
}

