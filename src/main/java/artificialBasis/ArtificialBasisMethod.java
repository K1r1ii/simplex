package artificialBasis;

import dataStorage.*;
import simplex.SimplexMethod;

import java.util.ArrayList;

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
     * Метод, реализующий автоматический режим выполнения метода искусственного базиса.
     * @param curTask объект класса <code>Task</code>, содержащий текущую задачу.
     * @return объект класса <code>SimplexTable</code>, содержащий симплекс-таблицу готовую к использованию в основном алгоритме
     */
    public static SimplexTable autoMode(Task curTask) {
        SimplexTable simplexTable = createSimplexTable(curTask); // переход к симплекс-таблице
        System.out.println(simplexTable);
        boolean isDecide = simplexTable.isDecide();
         while (!isDecide) {
             simplexTable = artificialBasisStep(simplexTable, -1, -1, curTask.getFunction());
             if (simplexTable.getErrorMassage() != null) {
                 break;
             }
             isDecide = simplexTable.isDecide();
             System.out.println(simplexTable);
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

        // удаление столбцов с выведенной дополнительной переменной
        simplexTable = deleteAdditionalColumn(simplexTable, originalFunction);
        // удаление нулевых строк
        simplexTable = deleteNullString(simplexTable);

        // проверка не отрицательности коэффициентов функции
        boolean positiveFlag = true;
        ArrayList<Fraction> function = simplexTable.getFunction();

        // поиск отрицательных коэффициентов функции
        for (int i = 0; i < function.size() - 1; i++) {
            if (!function.get(i).isMore(Fraction.ZERO) && !function.get(i).equals(Fraction.ZERO)) {
                positiveFlag = false;
                break;
            }
        }

        if (positiveFlag) {

            // реализация холостых шагов для вывода дополнительных переменных из базиса
            int additionalVarNum = checkAdditionalVarInBasis(simplexTable, originalFunction);
            if (additionalVarNum == -1) {

                // холостой ход не нужен
                if (!checkNullFunction(function)) {
                    return new SimplexTable("ERROR: Функция не занулилась!");
                }
                ArrayList<Fraction> newFunction = expressionFunction(simplexTable, originalFunction); // выражение функции через новый базис
                Matrix mtx = new Matrix(simplexTable.getMatrix());
                return new SimplexTable(newFunction, mtx, simplexTable.getBase(), simplexTable.getFreeVars(),
                        true, simplexTable.getTaskType(), simplexTable.getFracType(), simplexTable.getMode());
            } else {
                // вызываем холостой шаг симплекс-метода
                return SimplexMethod
                        .simplexStep(simplexTable, simplexTable.getBase().indexOf(additionalVarNum), 0, true);
            }
        } else {
            // вызываем шаг симплекс-метода
            return SimplexMethod.simplexStep(simplexTable, supportRow, supportColumn, false);
        }
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
        ArrayList<Integer> additionalVars = new ArrayList<>();
        ArrayList<Integer> freeVars = simplexTable.getFreeVars();

        // ищем индексы дополнительных переменных
        for (int i: freeVars) {
            if (i > originalFunction.size()) {
                additionalVars.add(freeVars.indexOf(i));
            }
        }

        Fraction[][] matrix = simplexTable.getMatrix();
        ArrayList<Fraction> function = simplexTable.getFunction();

        // инициализация нулевой матрицы новой размерности
        Matrix newMatrix = new Matrix(matrix.length, matrix[0].length - additionalVars.size());

        // инициализация функции новой размерности
        ArrayList<Fraction> newFunction = new ArrayList<>();

        // инициализация нового списка свободных переменных
        ArrayList<Integer> newFreeVars = new ArrayList<>();

        // заполнение новой матрицы
        System.out.println(additionalVars);
        for (int i = 0; i < matrix.length; i++) {
            int countAdditional = 0;
            for (int j = 0; j < matrix[0].length; j++) {
                if (!additionalVars.contains(j)) {
                    newMatrix.addElementByIndex(i, j - countAdditional, matrix[i][j]);
                } else {
                    countAdditional++;
                }
            }
        }

        // заполнение новой функции
        for (int i = 0; i < function.size(); i++) {
            if (!additionalVars.contains(i)) {
                newFunction.add(function.get(i));
            }
        }

        // заполнение нового списка свободных переменных
        for (int i = 0; i < freeVars.size(); i++) {
            if (!additionalVars.contains(i)) {
                newFreeVars.add(freeVars.get(i));
            }
        }

        return new SimplexTable(newFunction, newMatrix, simplexTable.getBase(), newFreeVars, simplexTable.isDecide(),
                simplexTable.getTaskType(), simplexTable.getFracType(), simplexTable.getMode());
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
                System.out.println(i.getNum());
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

        ArrayList<Integer> delStrings = new ArrayList<>();

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
                delStrings.add(i);
            }
        }

        // удаление нулевых строк
        Matrix newMatrix = new Matrix(matrix.length - delStrings.size(), matrix[0].length);
        int countDelStr = 0;
        for (int i = 0; i < matrix.length; i++) {
            if (!delStrings.contains(i)) {
                for (int j = 0; j < matrix[0].length; j++) {
                    newMatrix.addElementByIndex(i - countDelStr, j, matrix[i][j]);
                }
            } else {
                countDelStr++;
            }
        }

        // перезапись базиса
        ArrayList<Integer> newBase = new ArrayList<>();
        for (int i : curBase) {
            if (!delStrings.contains(curBase.indexOf(i))) {
                newBase.add(i);
            }
        }

        return new SimplexTable(simplexTable.getFunction(), newMatrix, newBase, simplexTable.getFreeVars(),
                simplexTable.isDecide(), simplexTable.getTaskType(), simplexTable.getFracType(), simplexTable.getMode());
    }
}
