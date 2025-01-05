package dataStorage;

import java.util.ArrayList;

/**
 * Класс для хранения исходной задачи
 */
public class Task {
    private ArrayList<Fraction> function;
    private Matrix matrix;
    private ArrayList<Integer> base;
    private String taskType;
    private String fracType;
    private String mode;
    private boolean isDecide;
    private String errorMessage;

    public static String  AUTO_MODE = "auto";
    public static String  MANUAL_MODE = "manual";


    /**
     *
     * Конструктор для сохранения задачи со списком номеров базисных переменных
     * @param function список коэффициентов исходной функции
     * @param matrix матрица ограничений
     * @param isDecide флаг решения задачи
     * @param base список с номерами базисных переменных
     * @param taskType тип задачи
     * @param fracType тип дробей
     * @param mode режим работы (ручной, автоматический)
     */
    public Task(
            ArrayList<Fraction> function,
            Matrix matrix,
            boolean isDecide,
            ArrayList<Integer> base,
            String taskType,
            String fracType,
            String mode
    ){
        this.function = function;
        this.matrix = matrix;
        this.isDecide = isDecide;
        this.base = base;
        this.taskType = taskType;
        this.fracType = fracType;
        this.mode = mode;
    }


    /**
     *
     * Конструктор для сохранения задачи без списка номеров базисных переменных (для метода искусственного базиса)
     * @param function список коэффициентов исходной функции
     * @param matrix матрица ограничений
     * @param isDecide флаг решения задачи
     * @param taskType тип задачи
     * @param fracType тип дробей
     * @param mode режим работы (ручной, автоматический)
     */
    public Task(
            ArrayList<Fraction> function,
            Matrix matrix,
            boolean isDecide,
            String taskType,
            String fracType,
            String mode
    ){
        this.function = function;
        this.matrix = matrix;
        this.isDecide = isDecide;
        this.base = null;
        this.taskType = taskType;
        this.fracType = fracType;
        this.mode = mode;
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

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public String getFracType() {
        return fracType;
    }

    public void setFracType(String fracType) {
        this.fracType = fracType;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    @Override
    public String toString(){
        String functionStr = "\nf = " + function.toString();
        String matrixStr = "\nRestrictions:\n" + matrix.toString();
        String taskTypeStr = "\nTask type: " + taskType;
        String fracTypeStr = "\nFractions type: " + fracType;
        String modeStr = "\nMode: " + mode;

        if(base != null){
            String baseStr = "Base: " + base;
            return functionStr + matrixStr + baseStr + taskTypeStr + fracTypeStr + modeStr + matrixStr;
        } else {
            return functionStr + matrixStr + taskTypeStr + fracTypeStr + modeStr;
        }
    }
}
