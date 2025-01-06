package dataStorage;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Класс, для хранения данных о решении задачи (аргументы и значение функции)
 */
public class Solution {
    private Fraction functionValue;
    private ArrayList<Fraction> vectorSolution;
    private String errorMessage;
    private String fracType;

    /**
     * Конструктор для сохранения решения.
     * @param functionValue оптимальное значение функции
     * @param vectorSolution список аргументов для оптимального значения функции
     */
    public Solution(Fraction functionValue, ArrayList<Fraction> vectorSolution, String fracType){
        this.functionValue = functionValue;
        this.vectorSolution = vectorSolution;
        this.fracType = fracType;
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

    public ArrayList<Fraction> getVectorSolution() {
        return vectorSolution;
    }


    /**
     * Метод возвращающий коэффициенты в виде строки в зависимости от выбранного типа дробей.
     * @return ArrayList<String> - список коэффициентов функции.
     */
    public ArrayList<String> getVectorSolutionStr() {
        ArrayList<String> functionStr = new ArrayList<>();

        if (Objects.equals(fracType, Fraction.DECIMAL)) {
            for (Fraction i : vectorSolution) {
                functionStr.add(i.toDecimal());
            }
        } else {
            for (Fraction i : vectorSolution) {
                functionStr.add(i.toString());
            }
        }
        return  functionStr;
    }

    /**
     * Метод возвращающий значение функции в виде строки в зависимости от типа дробей
     * @return String - строка, содержащая значение функции
     */
    public String getFunctionValueStr() {
        if (Objects.equals(fracType, Fraction.DECIMAL)) {
            return functionValue.toDecimal();
        } else {
            return functionValue.toString();
        }
    }

    public String getErrorMessage() {
        return errorMessage;
    }


    @Override
    public String toString(){
        return "\nРешение:\nf = " + functionValue.toString() + "\n" + "x = " + vectorSolution.toString();
    }
}

