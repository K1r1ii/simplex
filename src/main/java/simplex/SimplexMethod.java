package simplex;

import dataStorage.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Класс, реализующий методы для решения задач поиска оптимального значения с помощью симплекс-метода.
 */
public class SimplexMethod {
    /**
     * Метод, реализующий автоматический режим решения задачи симплекс-методом.
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий исходную симплекс-таблицу.
     * @return объект класс <code>Solution</code> содержащий решение задачи: значение функции и вектор аргументов.
     */
    public static Solution autoMode(SimplexTable simplexTable){
        SimplexTable newStep = simplexStep(simplexTable, -1, -1, false);
        if(newStep.getErrorMassage() !=  null){
            return new Solution(newStep.getErrorMassage());
        }

        boolean isDecide = newStep.isDecide();
        while(!isDecide){
            newStep = simplexStep(newStep, -1, -1, false);
            if(newStep.getErrorMassage() !=  null){
                return new Solution(newStep.getErrorMassage());
            }
            isDecide = newStep.isDecide();
        }

        ArrayList<Fraction> solutionVector = new ArrayList<>();
        // заполнение вектора решения нулями
        for(int i = 0; i < newStep.getBase().size() + newStep.getFreeVars().size(); i++){
            solutionVector.add(Fraction.ZERO);
        }

        // заполнение базисных значений
        int colSize = newStep.getMatrix()[0].length;

        for(int i = 0; i < newStep.getBase().size(); i++){
            solutionVector.set(newStep.getBase().get(i) - 1, newStep.getMatrix()[i][colSize - 1]);
        }

        Fraction functionValue = Fraction.ZERO.difference(newStep.getFunction().getLast());
        return new Solution(functionValue, solutionVector);
    }

    /**
     * Метод, реализующий один шаг симплекс-метода (переход от одной таблицы к другой путем смены базисной переменной).
     * @param simplexTable объект, содержащий текущую симплекс таблицу.
     * @param supportRow ряд таблицы, содержащий опорный элемент.
     * @param supportColumn столбец таблицы, содержащий опорный элемент.
     * @return объект класса <code>SimplexTable</code>, содержащий новую симплекс-таблицу с новой базисной переменной.
     */
    public static SimplexTable simplexStep(SimplexTable simplexTable, int supportRow, int supportColumn,
                                           boolean artBasisMode){
        int sRow, sCol;

        ArrayList<Fraction> newFunction = simplexTable.getFunction(); // список, в котором будут новые коэффициенты функции

        ArrayList<Integer> newBase = simplexTable.getBase(); // новый базис
        ArrayList<Integer> newFreeV = simplexTable.getFreeVars(); // новый список свободных переменных

        Fraction[][] matrix = simplexTable.getMatrix(); // исходная матрица
        Matrix newMatrix = new Matrix(matrix.length, matrix[0].length); // новая нулевая матрица

        ArrayList<Integer> coords; // список координат
        // получение опорного элемента
        if (supportRow == -1 || supportColumn == -1){
            coords = findSupport(simplexTable);
        } else {

            if (!artBasisMode && !checkSupport(simplexTable, supportRow, supportColumn)){
                // опорный элемент не подходит
                return new SimplexTable("ERROR: неверный опорный элемент.");
            }
            coords = new ArrayList<>(List.of(supportRow, supportColumn));
        }

        if (coords == null){
            // оптимального решения нет (функция неограниченна снизу)
            return new SimplexTable("NOTICE: There is no optimal solution.");
        }
        if (coords.equals(List.of(-1, -1))){
            // задача решена
            simplexTable.setDecide(true);
            return simplexTable;
        }

        sRow = coords.get(0);
        sCol = coords.get(1);
        Fraction sValue = matrix[sRow][sCol]; // значение опорного элемента
        System.out.println("sValue: " + sValue);

        // замена базиса
        int curBaseNum = simplexTable.getBase().get(sRow);
        int curFreeNum = simplexTable.getFreeVars().get(sCol);

        newBase.set(sRow, curFreeNum);
        newFreeV.set(sCol, curBaseNum);

        // обновление опорного элемента
        newMatrix.addElementByIndex(sRow, sCol, sValue.getReverseFraction());

        // обновление опорной строки
        for(int i = 0; i < newMatrix.getMatrix()[0].length; i++){
            if (i == sCol) continue;
            newMatrix.addElementByIndex(
                    sRow,
                    i,
                    matrix[sRow][i].multiply(sValue.getReverseFraction())
            );
        }

        // обновление опорного столбца + коэффициент функции
        for(int i = 0; i < newMatrix.getMatrix().length; i++){
            if (i == sRow) continue;

            newMatrix.addElementByIndex(
                    i,
                    sCol,
                    matrix[i][sCol].multiply(Fraction.ZERO.difference(sValue.getReverseFraction()))
            );
        }
        Fraction oldSEl = newFunction.get(sCol); // старый элемент функции напротив опорного
        newFunction
                .set(sCol, newFunction.get(sCol).multiply(Fraction.ZERO.difference(sValue.getReverseFraction())));

        // обновление остальных строк + обновление функции
        for (int i = 0; i < newMatrix.getMatrix().length; i++){
            for (int j = 0; j < newMatrix.getMatrix()[0].length; j++){
                if (i == sRow || j == sCol){
                    continue;
                }
                Fraction curEl = matrix[i][sCol]; // элемент напротив опорного
                Fraction newEl = matrix[i][j].difference(curEl.multiply(newMatrix.getMatrix()[sRow][j]));
                newMatrix.addElementByIndex(i, j, newEl);
            }
        }
        for (int i = 0; i < newFunction.size(); i++){
            if (i == sCol) continue;
            newFunction.set(i, newFunction.get(i).difference(oldSEl.multiply(newMatrix.getMatrix()[sRow][i])));
        }

        SimplexTable newTable = new SimplexTable(newFunction, newMatrix, newBase, newFreeV, false);
        if (!checkBasisValue(newTable)) {
            return new SimplexTable("ERROR: исходный базис является ошибочным");
        }

        return newTable;
    }


    public static Task simplexStepBack(Task curTask, int supportRow, int supportColumn, Fraction supportValue){
        // TODO: сделать метод для возврата к предыдущему шагу по опорному элементу
        return null;
    }

    /**
     * Метод, реализующий алгоритм Гаусса, для подготовки задачи к симплекс-методу.
     * Относительно выбранного базиса строится единичная матрица, после чего из полученных данных выражается исходная
     * функция и переводится в объект <code>SimplexTable</code> для последующего решения задачи симплекс-методом.
     * @param curTask объект, содержащий исходную задачу (матрица ограничений, функция, базис)
     * @return объект класса <code>SimplexTable</code> содержащий результат работы алгоритма Гаусса, подготовленный к
     * решению задачи симплекс-методом.
     */
    public static SimplexTable gauss(Task curTask){
        if (curTask.getBase() == null){
            // нет базиса
            return new SimplexTable("ERROR: Нет базиса!");
        }

        // перестановка базисных столбцов в начало
        Matrix mtx = curTask.getMatrix();
        for(int i = 0; i < curTask.getBase().size(); i++){
            mtx.replaceColumns(i, curTask.getBase().get(i) - 1);
        }
        int sizeBiggestMinor = Math.min(mtx.getMatrix().length, mtx.getMatrix()[0].length);

        // прямой ход
        for(int i = 0; i < sizeBiggestMinor; i++) {
            int indMinEl = i;

            // поиск ненулевого элемента
            Fraction minEl = new Fraction(0);
            for (int j = i; j < mtx.getMatrix().length; j++) {
                if (!Objects.equals(mtx.getMatrix()[j][i].getNum(), BigDecimal.ZERO)) {
                    minEl = mtx.getMatrix()[j][i];
                    indMinEl = j;
                    break;
                }
            }

            // проверка на нулевой столбец
            if (Objects.equals(minEl.getNum(), BigDecimal.ZERO) && i + 1 < curTask.getBase().size()) {
                return new SimplexTable("ERROR: Базисный столбец состоит из нулей.");
            }

            mtx.replaceRows(i, indMinEl);
            mtx.multiplyRowByNumber(i, mtx.getMatrix()[i][i].getReverseFraction());
            for(int row = i + 1; row < mtx.getMatrix().length; row++){
                mtx.nullingRow(row, i, i);
            }
        }


        // проверка базиса
        int rang = 0;
        for(Fraction[] row: mtx.getMatrix()){
            boolean zeroFlag = false;
            for(Fraction rowEl: row){
                if(!Objects.equals(rowEl.getNum(), BigDecimal.ZERO)){
                    zeroFlag = true;
                    break;
                }
            }
            if (zeroFlag){
                rang += 1;
            }
        }

        if (rang < curTask.getBase().size()){
            return new SimplexTable("ERROR: Количество базисных переменных больше возможного.");
        }

        if (rang > curTask.getBase().size()){
            return new SimplexTable("ERROR: Количество базисных переменных меньше возможного.");
        }

        // обратный ход
        for(int i = curTask.getBase().size() - 1; i >= 0 ; i--){
            for(int row = i - 1; row >= 0; row--){
                mtx.nullingRow(row, i, i);
            }
        }
        // базисные столбцы ставятся на места
        for(int i = curTask.getBase().size() - 1; i >= 0 ; i--){
            mtx.replaceColumns(curTask.getBase().get(i) - 1, i);
        }
        // список для измененной функции заполненный 0
        ArrayList<Fraction> newFunction = new ArrayList<>();
        for(int i = 0; i <= curTask.getFunction().size(); i++){
            newFunction.add(new Fraction(0));
        }
        // выражение функции
        int baseInd = 0;
        for(int i = 0; i < curTask.getFunction().size(); i++){
            if (curTask.getBase().contains(i + 1)){
                for(int j = 0; j <= curTask.getFunction().size(); j++){
                    if (i == j){
                        continue;
                    }
                    Fraction minusCoef;
                    if(j == curTask.getFunction().size()){
                        minusCoef = curTask.getFunction().get(i)
                                .multiply(mtx.getMatrix()[baseInd][j]);

                    } else {
                        minusCoef = curTask.getFunction().get(i)
                                .multiply(Fraction.ZERO.difference(mtx.getMatrix()[baseInd][j]));
                    }
                    newFunction.set(j, newFunction.get(j).sum(minusCoef));
                }
                baseInd++;
            } else {
                newFunction.set(i, newFunction.get(i).sum(curTask.getFunction().get(i)));
            }
        }
        newFunction.set(newFunction.size() - 1, Fraction.ZERO.difference(newFunction.getLast()));
        return new SimplexTable(new Task(newFunction, mtx, false, curTask.getBase()));
    }

    /**
     * Метод для поиска опорного элемента для текущей симплекс-таблицы.
     * @param simplexTable объект, содержащий симплекс таблицу.
     * @return список из двух элементов: ряд и столбце найденного опорного элемента
     *         список вида [-1, -1], если задача уже решена и поиск опорного элемента не требуется
     *         <code>null</code> если опорного элемента нет (оптимального решения нет)
     */
    private static ArrayList<Integer> findSupport(SimplexTable simplexTable){
        Fraction[][] mtx = simplexTable.getMatrix();
        ArrayList<Integer> supportCoordinates = new ArrayList<>();

        boolean isDecided = true;
        for(int i = 0; i < simplexTable.getFunction().size() - 1; i++){
            if (simplexTable.getFunction().get(i).isMore(Fraction.ZERO) || simplexTable.getFunction().get(i).equals(Fraction.ZERO)){
                // если находимся на базисном столбце или коэффициент функции неотрицательный
                continue;
            }
            Fraction minValue = null;
            isDecided = false;
            for(int j = 0; j < mtx.length; j++) {
                if (mtx[j][i].isMore(Fraction.ZERO)) {
                    minValue = mtx[j][mtx[0].length - 1]
                            .multiply(mtx[j][i].getReverseFraction());
                    supportCoordinates.add(j); // добавляем номер ряда
                    supportCoordinates.add(i); // добавляем номер столбца
                    break;
                }
            }
            if (minValue == null){
                // нет подходящих элементов
                continue;
            }
            for(int j = 0; j < mtx.length; j++) {
                if (mtx[j][i].isMore(Fraction.ZERO)) {
                    Fraction value = mtx[j][mtx[0].length - 1]
                            .multiply(mtx[j][i].getReverseFraction());
                    if (minValue.isMore(value)) {
                        minValue = value;
                        supportCoordinates.set(0, j); // добавляем номер ряда
                        supportCoordinates.set(1, i); // добавляем номер столбца
                    }
                }
            }
            break;

        }
        return isDecided ? new ArrayList<>(List.of(-1, -1)) : (!supportCoordinates.isEmpty() ? supportCoordinates : null);
    }

    /**
     * Метод для проверки выбранного опорного элемента.
     * @param simplexTable объект, содержащий текущую симплекс таблицу.
     * @param supportRow индекс ряда матрицы, содержащего опорный элемент.
     * @param supportColumn индекс столбца матрицы, содержащего опорный элемент.
     * @return <code>true</code> если опорный элемент подходит
     *         <code>false</code> если опорный элемент не подходит
     */
    private static boolean checkSupport(SimplexTable simplexTable, int supportRow, int supportColumn){
        Fraction[][] matrix = simplexTable.getMatrix();
        int lastIndex = matrix.length - 1;

        Fraction curBestRelation = matrix[supportRow][lastIndex]
                .multiply(matrix[supportRow][supportColumn].getReverseFraction());

        // проверка на минимальность отношения свободного члена к опорному элементу в текущем столбце
        for (Fraction[] row : matrix) {
            Fraction curRelation = row[lastIndex]
                    .multiply(row[supportColumn].getReverseFraction());
            curBestRelation = Fraction.min(curBestRelation, curRelation);
        }
        if (curBestRelation != matrix[supportRow][supportColumn]) {
            return false;
        }

        // проверка на отрицательность коэффициента функции и положительность опорного элемента
        return !simplexTable.getFunction().get(supportColumn).isMore(Fraction.ZERO)
                && simplexTable.getMatrix()[supportRow][supportColumn].isMore(Fraction.ZERO);
    }

    /**
     * Статический метод, реализующий проверку значений базисных переменных (должны быть неотрицательными)
     * @param simplexTable объект класса <code>SimplexTable</code>, содержащий текущую симплекс-таблицу
     * @return <code>true</code> если все значения неотрицательны
     *         <code>false</code> если есть отрицательные значения
     */
    private static boolean checkBasisValue(SimplexTable simplexTable) {
        Fraction[][] matrix = simplexTable.getMatrix();
        int lastIndex = matrix[0].length - 1;

        for (Fraction[] fractions : matrix) {
            if (!fractions[lastIndex].isMore(Fraction.ZERO) && !fractions[lastIndex].equals(Fraction.ZERO)) {
                return false;
            }
        }
        return true;
    }
}
