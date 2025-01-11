package simplex;

import artificialBasis.ArtificialBasisMethod;
import dataStorage.*;

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
        return getSolution(newStep);
    }

    /**
     * Метод, реализующий один шаг симплекс-метода (переход от одной таблицы к другой путем смены базисной переменной).
     * @param simplexTable объект, содержащий текущую симплекс таблицу.
     * @param supportRow ряд таблицы, содержащий опорный элемент.
     * @param supportColumn столбец таблицы, содержащий опорный элемент.
     * @return объект класса <code>SimplexTable</code>, содержащий новую симплекс-таблицу с новой базисной переменной.
     */
    public static SimplexTable simplexStep(SimplexTable simplexTable, int supportRow, int supportColumn,
                                           boolean freeStep){
        int sRow, sCol;

        ArrayList<Fraction> newFunction = simplexTable.getFunction(); // список, в котором будут новые коэффициенты функции

        ArrayList<Integer> newBase = simplexTable.getBase(); // новый базис
        ArrayList<Integer> newFreeV = simplexTable.getFreeVars(); // новый список свободных переменных

        Fraction[][] matrix = simplexTable.getMatrix(); // исходная матрица
        if (matrix.length == 0) {
            if (SimplexMethod.isZeroFunction(simplexTable.getFunction())) {
                return new SimplexTable("Функция не зависит от ограничений; f = 0");
            }
            return new SimplexTable("Функция не зависит от ограничений; f - неограниченна");
        }
        Matrix newMatrix = new Matrix(matrix.length, matrix[0].length); // новая нулевая матрица

        ArrayList<Integer> coords; // список координат
        // получение опорного элемента
        if (supportRow == -1 || supportColumn == -1){
            coords = findSupport(simplexTable);
        } else {
            if (!freeStep && !checkSupport(simplexTable, supportRow, supportColumn)){
                // опорный элемент не подходит
                return new SimplexTable("Неверный опорный элемент.");
            }
            coords = new ArrayList<>(List.of(supportRow, supportColumn));
        }

        if (coords == null){
            // оптимального решения нет (функция неограниченна снизу)
            return new SimplexTable("Функция не ограничена - оптимального решения нет.");
        }
        if (coords.equals(List.of(-1, -1))){
            // задача решена
            simplexTable.setDecide(true);
            return simplexTable;
        }

        // проверка на решенную задачу
        if (checkSolution(simplexTable) && !freeStep) {
            // задача решена
            simplexTable.setDecide(true);
            return simplexTable;
        }

        sRow = coords.get(0);
        sCol = coords.get(1);
        Fraction sValue = matrix[sRow][sCol]; // значение опорного элемента

        // замена базиса
        int curBaseNum = simplexTable.getBase().get(sRow);
        int curFreeNum = simplexTable.getFreeVars().get(sCol);

        newBase.set(sRow, curFreeNum);
        newFreeV.set(sCol, curBaseNum);

        // обновление опорного элемента
        newMatrix.addElementByIndex(sRow, sCol, Fraction.ONE.divide(sValue));

        // обновление опорной строки
        for(int i = 0; i < newMatrix.getMatrix()[0].length; i++){
            if (i == sCol) continue;
            newMatrix.addElementByIndex(
                    sRow,
                    i,
                    matrix[sRow][i].divide(sValue)
            );
        }

        // обновление опорного столбца + коэффициент функции
        for(int i = 0; i < newMatrix.getMatrix().length; i++){
            if (i == sRow) continue;

            newMatrix.addElementByIndex(
                    i,
                    sCol,
                    matrix[i][sCol].divide(Fraction.ZERO.subtract(sValue))
            );
        }
        Fraction oldSEl = newFunction.get(sCol); // старый элемент функции напротив опорного
        newFunction
                .set(sCol, newFunction.get(sCol).divide(Fraction.ZERO.subtract(sValue)));

        // обновление остальных строк + обновление функции
        for (int i = 0; i < newMatrix.getMatrix().length; i++){
            for (int j = 0; j < newMatrix.getMatrix()[0].length; j++){
                if (i == sRow || j == sCol){
                    continue;
                }
                Fraction curEl = matrix[i][sCol]; // элемент напротив опорного
                Fraction newEl = matrix[i][j].subtract(curEl.multiply(newMatrix.getMatrix()[sRow][j]));
                newMatrix.addElementByIndex(i, j, newEl);
            }
        }
        for (int i = 0; i < newFunction.size(); i++){
            if (i == sCol) continue;
            newFunction.set(i, newFunction.get(i).subtract(oldSEl.multiply(newMatrix.getMatrix()[sRow][i])));
        }
        // обновление списка шагов
        if (!freeStep) {
            Step newStep;
            if (simplexTable.getLastStep() != null) {
                newStep = new Step(simplexTable.getLastStep().getNum() + 1, coords, Step.SIMPLEX_METHOD);
            } else {
                newStep = new Step(0, coords, Step.SIMPLEX_METHOD);
            }
            simplexTable.addStep(newStep);
        }

        // новая симплекс таблица
        SimplexTable newTable = new SimplexTable(newFunction,newMatrix, newBase, newFreeV, false,
                simplexTable.getTaskType(), simplexTable.getFracType(), simplexTable.getMode(), simplexTable.getSteps());

        // проверка значений базисных переменных
        if (!checkBasisValue(newTable)) {
            return new SimplexTable("Исходный базис является ошибочным");
        }

        // проверка на решенную задачу
        if (checkSolution(newTable)) {
            // задача решена
            newTable.setDecide(true);
        }

        return newTable;
    }


    /**
     * Метод, реализующий шаг назад для симплекс-метода и ИБ.
     * @param simplexTable - текущая симплекс-таблица.
     * @return SimplexTable - новая таблица.
     */
    public static SimplexTable simplexStepBack(SimplexTable simplexTable){
        // получение координат предыдущего опорного элемента
        Step lastStep = simplexTable.getAndDelLastStep();
        if (lastStep == null) {
            return null;
        }

        // обнуление функции (если возвращаемся к ИБ)
        if (lastStep.getEndAB()) {
            simplexTable.setFunction(ArtificialBasisMethod.getZeroFunction(simplexTable.getFunction()));
        }
        ArrayList<DelString> delStrings = lastStep.getDelStrings();

        // добавление нулевых строк
        if (delStrings != null) {
            for (DelString delString : delStrings) {
                // добавление строки в матрицу
                Matrix matrix = simplexTable.getMatrixObject();
                matrix.addRowByIndex(delString);

                // добавление значения в список базисных переменных
                ArrayList<Integer> baseVars = simplexTable.getBase();
                baseVars.add(delString.getStringIndex(), delString.getBaseNum());
            }
        }

        DelColumn delColumn = lastStep.getDelColumn();
        // добавление старого столбца
        if (delColumn != null) {
            // добавление столбца в матрицу
            Matrix matrix = simplexTable.getMatrixObject();
            matrix.addColumnByIndex(delColumn);

            // добавление значения в функцию
            ArrayList<Fraction> function = simplexTable.getFunction();
            function.add(delColumn.getColumnIndex(), delColumn.getFunctionValue());

            // добавление значения в список свободных переменных
            ArrayList<Integer> freeVars = simplexTable.getFreeVars();
            freeVars.add(delColumn.getColumnIndex(), delColumn.getVarNum());
        }

        ArrayList<Integer> support = lastStep.getSupport();
        
        int row = support.getFirst();
        int column = support.getLast();

        // симплекс шаг с этим опорным элементом
        return SimplexMethod.simplexStep(simplexTable, row, column, true);
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
            Fraction minEl = Fraction.ZERO;
            for (int j = i; j < mtx.getMatrix().length; j++) {
                if (mtx.getMatrix()[j][i].getNum() != 0) {
                    minEl = mtx.getMatrix()[j][i];
                    indMinEl = j;
                    break;
                }
            }

            // проверка на нулевой столбец
            if (minEl.getNum() == 0 && i + 1 < curTask.getBase().size() && minEl.equals(Fraction.ZERO)) {
                return new SimplexTable("ERROR: Базисный столбец состоит из нулей.");
            }

            mtx.replaceRows(i, indMinEl);
            mtx.multiplyRowByNumber(i, mtx.getMatrix()[i][i].getReverseFraction());
            for(int row = i + 1; row < mtx.getMatrix().length; row++){
                mtx.nullingRow(row, i, i);
            }
        }


        // проверка ранга базиса
        int rang = 0;
        for (int i = 0; i < mtx.getMatrix().length; i++) {
            boolean zeroFlag = false;
            for (int j = 0; j < mtx.getMatrix()[0].length - 1; j++) {
                if (mtx.getMatrix()[i][j].getNum() != 0) {
                    zeroFlag = true;
                    break;
                }
            }
            if (zeroFlag) {
                rang++;
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

        // поиск нулевых строк
        ArrayList<Integer> nullRows = new ArrayList<>();
        for (int i = 0; i < mtx.getMatrix().length; i++) {
            boolean zeroFlag = true;
            for (int j = 0; j < mtx.getMatrix()[0].length; j++) {
                if (!mtx.getMatrix()[i][j].equals(Fraction.ZERO)) {
                    zeroFlag = false;
                }
            }
            if (zeroFlag) {
                nullRows.add(i);
            }
        }

        // удаление нулевых строк
        for (int i : nullRows) {
            mtx.deleteRow(i);
        }

        if (mtx.getMatrix().length > curTask.getFunction().size()) {
            return new SimplexTable("Некорректная система ограничений");
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
                                .multiply(Fraction.ZERO.subtract(mtx.getMatrix()[baseInd][j]));
                    }
                    newFunction.set(j, newFunction.get(j).sum(minusCoef));
                }
                baseInd++;
            } else {
                newFunction.set(i, newFunction.get(i).sum(curTask.getFunction().get(i)));
            }
        }

        newFunction.set(newFunction.size() - 1, Fraction.ZERO.subtract(newFunction.getLast()));
        return new SimplexTable(new Task(
                newFunction,
                mtx,
                false,
                curTask.getBase(),
                curTask.getTaskType(),
                curTask.getFracType(),
                curTask.getMode()
                ));
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
                            .divide(mtx[j][i]);
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
                            .divide(mtx[j][i]);
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
    public static boolean checkSupport(SimplexTable simplexTable, int supportRow, int supportColumn){
        Fraction[][] matrix = simplexTable.getMatrix();
        int lastIndex = matrix[0].length - 1;

        // коэффициент функции > 0
        if (simplexTable.getFunction().get(supportColumn).isMore(Fraction.ZERO) ||
                simplexTable.getFunction().get(supportColumn).equals(Fraction.ZERO)) {
            return false;
        }

        // неположительный коэффициент ограничения
        if (!matrix[supportRow][supportColumn].isMore(Fraction.ZERO)) {
            return false;
        }

        Fraction curBestRelation = matrix[supportRow][lastIndex]
                .divide(matrix[supportRow][supportColumn]);

        // проверка на минимальность отношения свободного члена к опорному элементу в текущем столбце
        for (Fraction[] row : matrix) {
            if (row[supportColumn].isMore(Fraction.ZERO)) {
                Fraction curRelation = row[lastIndex]
                        .divide(row[supportColumn]);
                curBestRelation = Fraction.min(curBestRelation, curRelation);
            }
        }

        return curBestRelation.equals(matrix[supportRow][lastIndex].divide(matrix[supportRow][supportColumn]));
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

    /**
     * Метод для проверки решена ли задача.
     * @param simplexTable текущая симплекс-таблица задачи.
     * @return <code>true</code> если задача решена
     *         <code>false</code> если задача не решена
     */
    public static boolean checkSolution(SimplexTable simplexTable) {
        return Objects.equals(findSupport(simplexTable), new ArrayList<>(List.of(-1, -1)));
    }

    /**
     * Метод для получения решения из данных симплекс-таблицы
     * @param simplexTable текущая симплекс-таблица
     * @return Solution объект с данными решения задачи
     */
    public static Solution getSolution(SimplexTable simplexTable) {
        ArrayList<Fraction> solutionVector = new ArrayList<>();
        // заполнение вектора решения нулями
        for(int i = 0; i < simplexTable.getBase().size() + simplexTable.getFreeVars().size(); i++){
            solutionVector.add(Fraction.ZERO);
        }

        // заполнение базисных значений
        int colSize = simplexTable.getMatrix()[0].length;

        for(int i = 0; i < simplexTable.getBase().size(); i++){
            solutionVector.set(simplexTable.getBase().get(i) - 1, simplexTable.getMatrix()[i][colSize - 1]);
        }

        // запись значения функции
        Fraction functionValue;
        if (Objects.equals(simplexTable.getTaskType(), SimplexTable.MAX_TYPE)) {
            functionValue = simplexTable.getFunction().getLast();
        } else {
            functionValue = Fraction.ZERO.subtract(simplexTable.getFunction().getLast());
        }

        return new Solution(functionValue, solutionVector, simplexTable.getFracType());
    }

    /**
     * Метод, проверяющий является ли функция нулевой
     * @param function исходная функция
     * @return <code>true</code> если функция нулевая
     *         <code>false</code> иначе
     */
    private static boolean isZeroFunction(ArrayList<Fraction> function) {
        boolean zeroFlag = true;
        for (Fraction i : function) {
            if (!i.equals(Fraction.ZERO)) {
                zeroFlag = false;
                break;
            }
        }
        return zeroFlag;
    }
}
