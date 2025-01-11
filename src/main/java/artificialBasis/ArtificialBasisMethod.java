package artificialBasis;

import dataStorage.*;
import simplex.SimplexMethod;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Класс, реализующий методы для поиска начального базиса в задаче на поиск оптимального решения.
 */
public class ArtificialBasisMethod {

    /**
     * Метод, реализующий конвертацию исходной задачи в симплекс таблицу с дополнительными переменными.
     * @param curTask объект класса <code>Task</code> для хранения исходной задачи.
     * @return объект класса <code>SimplexTable</code> содержащий симплекс таблицу с дополнительными переменными.
     */
    public static SimplexTable createSimplexTable(Task curTask){
        ArrayList<Fraction> newFunction = new ArrayList<>();
        ArrayList<Integer> newBase = new ArrayList<>();
        ArrayList<Integer> newFreeVars = new ArrayList<>();

        Fraction[][] matrix = curTask.getMatrix().getMatrix();

        // домножение на -1 строк ограничений, в которых свободный член < 0
        int lastInd = matrix[0].length - 1;
        Matrix newMatrix = new Matrix(matrix.length, matrix[0].length);

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                if (!matrix[i][lastInd].isMore(Fraction.ZERO) && matrix[i][lastInd] != Fraction.ZERO) {
                    newMatrix.addElementByIndex(i, j, matrix[i][j].multiply(new Fraction(-1)));
                } else {
                    newMatrix.addElementByIndex(i, j, matrix[i][j]);
                }

            }
        }

        // пересчет коэффициентов функции
        for (int i = 0; i < newMatrix.getMatrix()[0].length; i++) {
            Fraction curSum = Fraction.ZERO;

            for (Fraction[] fractions : newMatrix.getMatrix()) {
                curSum = curSum.sum(fractions[i]);
            }
            newFunction.add(Fraction.ZERO.subtract(curSum));
        }

        // список номеров базисных переменных
        for (int i = 0; i < matrix.length; i++) {
            newBase.add(i + newFunction.size());
        }

        // список свободных переменных
        for (int i = 1; i < newBase.getFirst(); i++) {
            newFreeVars.add(i);
        }

        return new SimplexTable(newFunction, newMatrix, newBase, newFreeVars, false,
                curTask.getTaskType(), curTask.getFracType(), curTask.getMode());
    }

    /**
     *
     * Метод, реализующий автоматический режим выполнения метода искусственного базиса.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий текущую симплекс-таблицу.
     * @param function список коэффициентов исходной функции.
     * @return объект класса <code>SimplexTable</code>, содержащий симплекс-таблицу готовую к использованию в основном алгоритме
     */
    public static SimplexTable autoMode(SimplexTable simplexTable, ArrayList<Fraction> function) {

        boolean isDecide = simplexTable.isDecide();
         while (!isDecide) {
             simplexTable = artificialBasisStep(simplexTable, -1, -1, function);
             if (simplexTable.getErrorMassage() != null) {
                 break;
             }
             isDecide = simplexTable.isDecide();
         }
         return simplexTable;
    }


    /**
     * Метод, реализующий один шаг метода искусственного базиса с использованием симплекс-метода.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий текущую симплекс-таблицу.
     * @param supportRow номер ряда, содержащего опорный элемент.
     * @param supportColumn номер столбца, содержащего опорный элемент.
     * @param originalFunction список с коэффициентами исходной функции.
     * @return объект класса <code>SimplexTable</code>, содержащий следующий шаг симплекс-метода для данной задчаи.
     */
    public static SimplexTable artificialBasisStep(
            SimplexTable simplexTable,
            int supportRow,
            int supportColumn,
            ArrayList<Fraction> originalFunction
            ) {
        // вызываем шаг симплекс-метода
        SimplexTable newTable = SimplexMethod.simplexStep(simplexTable, supportRow, supportColumn, false);

        // удаление столбцов с выведенной дополнительной переменной
        newTable = deleteAdditionalColumn(newTable, originalFunction);

        // удаление нулевых строк
        newTable = deleteNullString(newTable);

        if (newTable.isDecide()) {
            int additionalVarNum = checkAdditionalVarInBasis(newTable, originalFunction);
            while (additionalVarNum != -1) {
                newTable = freeStep(newTable, additionalVarNum);
                if (newTable.getErrorMassage() != null) {
                    return newTable;
                }
                additionalVarNum = checkAdditionalVarInBasis(simplexTable, originalFunction);
            }
            // если доп переменных не осталось
            if(!checkNullFunction(newTable.getFunction())){
                return new SimplexTable("Функция не занулилась!");
            }
            ArrayList<Fraction> newFunction = expressionFunction(newTable, originalFunction);
            newTable.setFunction(newFunction);

            // ставим метку перехода на симплекс метод
            Step lastStep = newTable.getLastStep();
            if (lastStep != null) {
                lastStep.setEndAB(true);
            }
            return newTable;
        }
        Step laststep = newTable.getLastStep();
        laststep.setMethod(Step.ARTIFICIAL_BASIS_METHOD);
        return newTable;
    }

    /**
     * Метод, реализующий поиск дополнительной переменной в базисе.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий симплекс-таблицу.
     * @return номер дополнительной переменой в базисе, если она есть, и -1, если дополнительных переменных в базисе нет
     */
    private static int checkAdditionalVarInBasis(SimplexTable simplexTable, ArrayList<Fraction> originalFunction) {
        for (int i: simplexTable.getBase()) {
            if (i > originalFunction.size()) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Метод, реализующий выражение новых коэффициентов целевой функции через найденный базис.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий симплекс-таблицу.
     * @param originalFunction список с коэффициентами исходной функции.
     * @return список новых коэффициентов целевой функции.
     */
    private static ArrayList<Fraction> expressionFunction(SimplexTable simplexTable, ArrayList<Fraction> originalFunction) {
        ArrayList<Fraction> newFunction = new ArrayList<>();

        Fraction[][] matrix = simplexTable.getMatrix();
        ArrayList<Integer> curBase = simplexTable.getBase();
        ArrayList<Integer> curFreeVars = simplexTable.getFreeVars();

        // заполнение новой функции 0
        for (int i = 0; i <= simplexTable.getFreeVars().size(); i++) {
            newFunction.add(Fraction.ZERO);
        }

        // заполнение функции выведенными значениями

        // суммирование все переменные из базиса
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[0].length; j++) {
                newFunction.set(j, newFunction.get(j).sum(Fraction.ZERO.subtract(matrix[i][j])
                        .multiply(originalFunction.get(curBase.get(i) - 1))));
            }
        }

        // добавление свободных переменных, который были в исходной функции
        for (int i = 0; i < curFreeVars.size(); i++) {
            newFunction.set(i, newFunction.get(i).sum(originalFunction.get(curFreeVars.get(i) - 1)));
        }
        return newFunction;
    }

    /**
     * Метод, реализующий удаление столбцов с дополнительными переменными из матрицы ограничений, целевой функции и списка свободных переменных.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий симплекс-таблицу.
     * @param originalFunction список с коэффициентами исходной функции.
     * @return объект класса <code>SimplexTable</code>, содержащий ту же симплекс-таблицу, но с удаленными дополнительными переменными
     */
    private static SimplexTable deleteAdditionalColumn(SimplexTable simplexTable, ArrayList<Fraction> originalFunction) {
        int additionalVar = -1;
        ArrayList<Integer> freeVars = simplexTable.getFreeVars();

        // для сохранения удаленного столбца
        int columnIndex = -1;
        int varNum = -1;
        ArrayList<Fraction> delColumnValues= new ArrayList<>();

        // ищем индексы дополнительных переменных
        for (int i: freeVars) {
            if (i > originalFunction.size()) {
                additionalVar = freeVars.indexOf(i);
                columnIndex = additionalVar;
                varNum = i;
                break;
            }
        }

        Fraction[][] matrix = simplexTable.getMatrix();
        ArrayList<Fraction> function = simplexTable.getFunction();

        // инициализация нулевой матрицы новой размерности
        Matrix newMatrix = new Matrix(matrix.length, matrix[0].length - (additionalVar == -1 ? 0 : 1));

        // инициализация функции новой размерности
        ArrayList<Fraction> newFunction = new ArrayList<>();

        // инициализация нового списка свободных переменных
        ArrayList<Integer> newFreeVars = new ArrayList<>();

        // заполнение новой матрицы
        for (int i = 0; i < matrix.length; i++) {
            int countAdditional = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                if (additionalVar != j) {
                    newMatrix.addElementByIndex(i, j - countAdditional, matrix[i][j]);
                } else {
                    delColumnValues.add(matrix[i][j]);
                    countAdditional++;
                }
            }
        }

        // заполнение новой функции
        for (int i = 0; i < function.size(); i++) {
            if (additionalVar != i) {
                newFunction.add(function.get(i));
            }
        }

        // заполнение нового списка свободных переменных
        for (int i = 0; i < freeVars.size(); i++) {
            if (additionalVar != i) {
                newFreeVars.add(freeVars.get(i));
            }
        }

        // обновление списка шагов
        if (additionalVar != -1) {
            Fraction functionValue = function.get(additionalVar);
            DelColumn delColumn = new DelColumn(delColumnValues, functionValue, columnIndex, varNum);

            // изменение последнего шага
            Step lastStep = simplexTable.getAndDelLastStep();
            lastStep.setDelColumn(delColumn);
            simplexTable.addStep(lastStep);
        }

        return new SimplexTable(newFunction, newMatrix, simplexTable.getBase(), newFreeVars, simplexTable.isDecide(),
                simplexTable.getTaskType(), simplexTable.getFracType(), simplexTable.getMode(), simplexTable.getSteps());
    }

    /**
     * Метод, реализующий проверку коэффициентов целевой функции равенство нулю.
     * @param function список с коэффициентами целевой функции.
     * @return <code>true</code> если все коэффициенты равны 0,
     *         <code>false</code> если хотя бы один коэффициент не равен 0.
     */
    private static boolean checkNullFunction(ArrayList<Fraction> function) {
        for (Fraction i : function) {
            if (!i.equals(Fraction.ZERO)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Метод, реализующий удаление нулевой строки из симплекс-таблицы.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий симплекс-таблицу.
     * @return объект класса <code>SimplexTable</code>, содержащий ту же симплекс-таблицу, но с удаленными нулевыи строками.
     */
    private static SimplexTable deleteNullString(SimplexTable simplexTable){
        ArrayList<Integer> curBase = simplexTable.getBase();
        Fraction[][] matrix = simplexTable.getMatrix();

        ArrayList<Integer> delIndStrings = new ArrayList<>();

        // для сохранения удаленных строк
        ArrayList<DelString> delStrings = new ArrayList<>();

        // поиск нулевых строк
        for (int i = 0; i < matrix.length; i++) {
            boolean zeroFlag = true;
            for (int j = 0; j < matrix[0].length; j++) {
                if (!matrix[i][j].equals(Fraction.ZERO)) {
                    zeroFlag = false;
                    break;
                }
            }
            if (zeroFlag) {
                delIndStrings.add(i);
            }
        }
        // удаление нулевых строк
        Matrix newMatrix = new Matrix(matrix.length - delIndStrings.size(), matrix[0].length);
        int countDelStr = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (!delIndStrings.contains(i)) {
                for (int j = 0; j < matrix[0].length; j++) {
                    newMatrix.addElementByIndex(i - countDelStr, j, matrix[i][j]);
                }
            } else {
                countDelStr++;
                // сохранение удаленной строки
                ArrayList<Fraction> delString = new ArrayList<>(Arrays.asList(matrix[i]));
                delStrings.add(new DelString(delString, i, curBase.get(i)));
            }
        }

        // перезапись базиса
        ArrayList<Integer> newBase = new ArrayList<>();
        for (int i : curBase) {
            if (!delIndStrings.contains(curBase.indexOf(i))) {
                newBase.add(i);
            }
        }

        // обновление данных в этом шаге
        Step lastStep = simplexTable.getLastStep();
        if (!delIndStrings.isEmpty() && lastStep != null) {
            lastStep.setDelStrings(delStrings);
        }

        return new SimplexTable(simplexTable.getFunction(), newMatrix, newBase, simplexTable.getFreeVars(),
                simplexTable.isDecide(), simplexTable.getTaskType(), simplexTable.getFracType(), simplexTable.getMode(),
                simplexTable.getSteps());
    }

    /**
     * Метод, реализующий "холостой" шаг симплекс-метода.
     * @param simplexTable текущая симплекс-таблица.
     * @param additionalVarNum номер дополнительной переменной, которую надо вывести.
     * @return SimplexTable - таблица с выведенной доп. переменной.
     */
    private static SimplexTable freeStep(SimplexTable simplexTable, int additionalVarNum) {
        // метод, реализующий холостой ход симплекс таблицы
        Fraction[][] mtx = simplexTable.getMatrix();
        int addVarRowInd = simplexTable.getBase().indexOf(additionalVarNum);
        int addVarColInd = -1;
        for (int i = 0; i < mtx[addVarRowInd].length - 1; i++) {
            if (mtx[addVarRowInd][i].getNum() != 0) {
                addVarColInd = i;
                break;
            }
        }
        if (addVarColInd == -1) {
            return new SimplexTable("Появилась нулевая строка с дополнительной переменной!");
        }

        simplexTable = SimplexMethod
                .simplexStep(simplexTable, addVarRowInd, addVarColInd, true);
        return simplexTable;
    }

    /**
     * Метод для получения нулевой функции нужной размерности.
     * @param function исходная функция.
     * @return ArrayList<Fraction> - список нулевых коэффициентов функции.
     */
    public static ArrayList<Fraction> getZeroFunction(ArrayList<Fraction> function) {
        ArrayList<Fraction> zeroFunction = new ArrayList<>();
        for (Fraction i : function) {
            zeroFunction.add(Fraction.ZERO);
        }
        return zeroFunction;
    }
}
