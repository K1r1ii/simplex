package dataStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
        // домножение функции на -1 если тип задачи max
        if (Objects.equals(taskType, SimplexTable.MAX_TYPE)) {
            this.function = new ArrayList<>();
            for (Fraction fraction : function) {
                this.function.add(Fraction.ZERO.subtract(fraction));
            }
        } else {
            this.function = function;
        }
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

    public Matrix getMatrix() {
        return matrix;
    }

    public ArrayList<Integer> getBase() {
        return base;
    }

    public boolean isDecide() {
        return isDecide;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getTaskType() {
        return taskType;
    }

    public String getFracType() {
        return fracType;
    }

    public String getMode() {
        return mode;
    }


    /**
     * Метод для конвертации объекта в словарь.
     * @return словарь со значениями всех полей класса.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> taskMap = new HashMap<>();

        // конвертация коэффициентов функции
        ArrayList<String> functionStr = new ArrayList<>();
        for (Fraction i : function) {
            if (Objects.equals(fracType, Fraction.DECIMAL)) {
                functionStr.add(i.toDecimal());
            } else {
                functionStr.add(i.toString());
            }
        }

        // конвертация коэффициентов матрицы
        Fraction[][] mtx = matrix.getMatrix();
        int rows = mtx.length;
        int columns = mtx[0].length;

        String[][] matrixStr = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Objects.equals(fracType, Fraction.DECIMAL)) {
                    matrixStr[i][j] = String.valueOf(mtx[i][j].toDecimal());
                } else {
                    matrixStr[i][j] = mtx[i][j].toString();
                }
            }
        }

        taskMap.put("countVars", function.size());
        taskMap.put("countRestrictions", matrix.getMatrix().length);
        taskMap.put("taskType", taskType);
        taskMap.put("fracType", fracType);
        taskMap.put("base", base);
        taskMap.put("mode", mode);
        taskMap.put("function", functionStr);
        taskMap.put("restrictions", matrixStr);

        return taskMap;
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
