package dataStorage;

import java.util.*;

/**
 * Класс, для хранения текущего состояния задачи во время симплекс-метода.
 */
public class SimplexTable {
    private ArrayList<Fraction> function;
    private Matrix matrix;
    private ArrayList<Integer> base;
    private ArrayList<Integer> freeVars;
    private String taskType;
    private String fracType;
    private String mode;
    private boolean isDecide;
    private ArrayList<Step> steps;
    private String errorMassage;

    public static String MIN_TYPE = "min";
    public static String MAX_TYPE = "max";

    /**
     * Конструктор для инициализации новой симплекс-таблицы со списком опорных элементов
     * @param function коэффициенты исходной функции
     * @param matrix матрица ограничений
     * @param base список номеров базисных переменных
     * @param freeVars список номеров свободных переменных
     * @param isDecide флаг решения задачи
     * @param taskType тип задачи
     * @param fracType тип дробей
     * @param mode режим работы
     * @param steps список проделанных шагов
     */
    public SimplexTable(
            ArrayList<Fraction> function,
            Matrix matrix,
            ArrayList<Integer> base,
            ArrayList<Integer> freeVars,
            boolean isDecide,
            String taskType,
            String fracType,
            String mode,
            ArrayList<Step> steps
    ) {
        this.function = function;
        this.matrix = matrix;
        this.base = base;
        this.freeVars = freeVars;
        this.isDecide = isDecide;
        this.taskType = taskType;
        this.fracType = fracType;
        this.mode = mode;
        this.steps = steps;
    }

    // конструктор копирования
    public SimplexTable(SimplexTable other) {
        this.function = other.getFunction();
        this.matrix = other.getMatrixObject();
        this.base = other.getBase();
        this.freeVars = other.getFreeVars();
        this.isDecide = other.isDecide();
        this.taskType = other.getTaskType();
        this.fracType = other.getFracType();
        this.mode = other.getMode();
        this.steps = other.getSteps();
        this.errorMassage = other.getErrorMassage();
    }


    /**
     * Конструктор для инициализации новой симплекс-таблицы
     * @param function коэффициенты исходной функции
     * @param matrix матрица ограничений
     * @param base список номеров базисных переменных
     * @param freeVars список номеров свободных переменных
     * @param isDecide флаг решения задачи
     * @param taskType тип задачи
     * @param fracType тип дробей
     * @param mode режим работы
     */
    public SimplexTable(
            ArrayList<Fraction> function,
            Matrix matrix,
            ArrayList<Integer> base,
            ArrayList<Integer> freeVars,
            boolean isDecide,
            String taskType,
            String fracType,
            String mode
    ) {
        this.function = function;
        this.matrix = matrix;
        this.base = base;
        this.freeVars = freeVars;
        this.isDecide = isDecide;
        this.taskType = taskType;
        this.fracType = fracType;
        this.mode = mode;
        this.steps = new ArrayList<>();
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
        taskType = curTask.getTaskType();
        fracType = curTask.getFracType();
        mode = curTask.getMode();
        steps = new ArrayList<>(); // инициализируем пустой список

    }


    public ArrayList<Fraction> getFunction() {
        return function;
    }

    /**
     * Метод возвращающий коэффициенты в виде строки в зависимости от выбранного типа дробей.
     * @return ArrayList<String> - список коэффициентов функции.
     */
    public ArrayList<String> getFunctionStr() {
        ArrayList<String> functionStr = new ArrayList<>();

        if (Objects.equals(fracType, Fraction.DECIMAL)) {
            for (Fraction i : function) {
                functionStr.add(i.toDecimal());
            }
        } else {
            for (Fraction i : function) {
                functionStr.add(i.toString());
            }
        }
        return  functionStr;
    }

    /**
     * Метод возвращающий ограничения в виде строки в зависимости от выбранного типа дробей.
     * @return String[][] - двумерный массив строк.
     */
    public String[][] getMatrixStr() {
        if (matrix.getMatrix().length == 0) {
            return null;
        }

        Fraction[][] mtx = matrix.getMatrix();
        int rows = mtx.length;
        int columns = mtx[0].length;
        String[][] matrixStr = new String[rows][columns];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                if (Objects.equals(fracType, Fraction.DECIMAL)) {
                    matrixStr[i][j] = mtx[i][j].toDecimal();
                } else {
                    matrixStr[i][j] = mtx[i][j].toString();
                }
            }
        }
        return matrixStr;
    }

    public void setFunction(ArrayList<Fraction> function) {
        this.function = function;
    }

    public Fraction[][] getMatrix() {
        return matrix.getMatrix();
    }

    public Matrix getMatrixObject() {
        return matrix;
    }

    public ArrayList<Integer> getBase() {
        return base;
    }

    public ArrayList<Integer> getFreeVars() {
        return freeVars;
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

    public String getTaskType() {
        return taskType;
    }

    public String getFracType() {
        return fracType;
    }

    public String getMode() {
        return mode;
    }

    public ArrayList<Step> getSteps() {
        return steps;
    }

    /**
     * Метод для добавления нового опорного элемента
     * @param step объект класс Step с данными о текущем шаге
     */
    public void addStep(Step step) {
        steps.add(step);
    }

    /**
     * Метод для извлечения последнего добавленного опорного элемента.
     * @return Step - данные о последнем шаге.
     *         null - если список был пустой.
     */
    public Step getAndDelLastStep() {
        if (steps == null) return null;
        try {
            return steps.removeLast();
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    /**
     * Метод для получения данных о последнем шаге
     * @return Step - данные о последнем шаге
     */
    public Step getLastStep() {
        try {
            return steps.getLast();
        } catch (NoSuchElementException e) {
            return null;
        }
    }


    /**
     * Метод для конвертации объекта в словарь.
     * @return словарь со значениями всех полей класса.
     */
    public Map<String, Object> toMap() {
        Map<String, Object> taskMap = new HashMap<>();

        taskMap.put("taskType", taskType);
        taskMap.put("fracType", fracType);
        taskMap.put("base", base);
        taskMap.put("freeVars", freeVars);
        taskMap.put("mode", mode);
        taskMap.put("function", getFunctionStr());
        taskMap.put("restrictions", getMatrixStr());

        return taskMap;
    }


    @Override
    public String toString(){
        String functionStr = "\nf = " + function.toString();
        String matrixStr = "\nRestrictions:\n" + matrix.toString();
        String taskTypeStr = "\nTask type: " + taskType;
        String fracTypeStr = "\nFractions type: " + fracType;
        String modeStr = "\nMode: " + mode;

        String baseStr = "Base: " + base.toString();
        String freeVStr = "Free: " + freeVars.toString();
        return functionStr + matrixStr + baseStr + "\n" + freeVStr + taskTypeStr + fracTypeStr + modeStr;
    }
}
