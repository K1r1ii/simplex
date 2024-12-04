package simplex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimplexMethod {
    // TODO: проработать все null значения и обработать неограниченную снизу функцию
    public static Solution autoMode(SimplexTable simplexTable){
        SimplexTable newStep = simplexStep(simplexTable, -1, -1);
        if(newStep == null){
            return null;
        }
        boolean isDecide = newStep.isDecide();
        while(!isDecide){
            newStep = simplexStep(newStep, -1, -1);
            if(newStep == null){
                return null;
            }
            isDecide = newStep.isDecide();
        }

        ArrayList<Fraction> solutionVector = new ArrayList<>();
        // заполнение вектора решения нулями
        for(int i = 0; i < newStep.getBase().size() + newStep.getFreeVars().size(); i++){
            solutionVector.add(Fraction.ZERO);
        }

        // заполнение базисных значений
        int rowSize = newStep.getMatrix().getMatrix().length;
        int colSize = newStep.getMatrix().getMatrix()[0].length;

        for(int i = 0; i < rowSize; i++){
            solutionVector.set(newStep.getBase().get(i) - 1, newStep.getMatrix().getMatrix()[i][colSize - 1]);
        }

        Fraction functionValue = Fraction.ZERO.difference(newStep.getFunction().getLast());
        return new Solution(functionValue, solutionVector);
    }

    public static SimplexTable simplexStep(SimplexTable simplexTable, int supportRow, int supportColumn){
        int sRow, sCol;

        ArrayList<Fraction> newFunction = simplexTable.getFunction(); // список, в котором будут новые коэф. функции

        ArrayList<Integer> newBase = simplexTable.getBase(); // новый базис
        ArrayList<Integer> newFreeV = simplexTable.getFreeVars(); // новый список свободных переменных

        Matrix matrix = simplexTable.getMatrix(); // исходная матрица
        Matrix newMatrix = new Matrix(matrix.getMatrix().length, matrix.getMatrix()[0].length); // новая нулевая матрица

        ArrayList<Integer> coords; // список координат
        // получение опорного элемента
        if (supportRow == -1 || supportColumn == -1){
            coords = findSupport(simplexTable);
        } else {
            if (!checkSupport(simplexTable, supportRow, supportColumn)){
                return null;
            }
            coords = new ArrayList<>(List.of(supportRow, supportColumn));
        }

        if (coords == null){
            // оптимального решения нет
            return null;
        }
        if (coords.equals(List.of(-1, -1))){
            // задача решена
            simplexTable.setDecide(true);
            return simplexTable;
        }

        sRow = coords.get(0);
        sCol = coords.get(1);
        Fraction sValue = matrix.getMatrix()[sRow][sCol]; // значение опорного элемента

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
                    matrix.getMatrix()[sRow][i].multiply(sValue.getReverseFraction())
            );
        }

        // обновление опорного столбца + коэффициент функции
        for(int i = 0; i < newMatrix.getMatrix().length; i++){
            if (i == sRow) continue;

            newMatrix.addElementByIndex(
                    i,
                    sCol,
                    matrix.getMatrix()[i][sCol].multiply(Fraction.ZERO.difference(sValue.getReverseFraction()))
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
                Fraction curEl = matrix.getMatrix()[i][sCol]; // элемент напротив опорного
                Fraction newEl = matrix.getMatrix()[i][j].difference(curEl.multiply(newMatrix.getMatrix()[sRow][j]));
                newMatrix.addElementByIndex(i, j, newEl);
            }
        }
        for (int i = 0; i < newFunction.size(); i++){
            if (i == sCol) continue;
            newFunction.set(i, newFunction.get(i).difference(oldSEl.multiply(newMatrix.getMatrix()[sRow][i])));
        }

        return new SimplexTable(newFunction, newMatrix, newBase, newFreeV, false);
    }


    public static Task simplexStepBack(Task curTask, int supportRow, int supportColumn, Fraction supportValue){
        // TODO: сделать метод для возврата к предыдущему шагу по опорному элементу
        return null;
    }

    public static SimplexTable gauss(Task curTask){
        if (curTask.getBase() == null){
            return null;
        }

        // replace base column to the beginning
        Matrix mtx = curTask.getMatrix();
        for(int i = 0; i < curTask.getBase().size(); i++){
            mtx.replaceColumns(i, curTask.getBase().get(i) - 1);
        }
        int sizeBiggestMinor = Math.min(mtx.getMatrix().length, mtx.getMatrix()[0].length);
        // straight stroke
        for(int i = 0; i < sizeBiggestMinor; i++) {
            int indMinEl = i;

            // find first min element
            Fraction minEl = new Fraction(0);
            for (int j = 0; j < mtx.getMatrix().length; j++) {
                if (!Objects.equals(mtx.getMatrix()[j][i].num, BigDecimal.ZERO)) {
                    minEl = mtx.getMatrix()[j][i];
                    indMinEl = j;
                }
            }

            // check zero matrix
            if (Objects.equals(minEl.num, BigDecimal.ZERO)) {
                System.out.println("ERROR: zero base column passed!");
                return null;
            }

            mtx.replaceRows(i, indMinEl);
            mtx.multiplyRowByNumber(i, mtx.getMatrix()[i][i].getReverseFraction());
            for(int row = i + 1; row < mtx.getMatrix().length; row++){
                mtx.nullingRow(row, i, i);
            }
        }

        int rang = 0;
        for(Fraction[] row: mtx.getMatrix()){
            boolean zeroFlag = false;
            for(Fraction rowEl: row){
                if(!Objects.equals(rowEl.num, BigDecimal.ZERO)){
                    zeroFlag = true;
                    break;
                }
            }
            if (zeroFlag){
                rang += 1;
            }
        }

        if (rang < curTask.getBase().size()){
            System.out.println("ERROR: count base numbers is more possible!");
            return null;
        }

        if (rang > curTask.getBase().size()){
            System.out.println("ERROR: count base numbers is less possible!");
            return null;
        }
        // reverse stroke
        for(int i = curTask.getBase().size() - 1; i >= 0 ; i--){
            for(int row = i - 1; row >= 0; row--){
                mtx.nullingRow(row, i, i);
//                mtx.printM("");
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
        // вывод функции
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


    private static ArrayList<Integer> findSupport(SimplexTable simplexTable){
        // Метод для поиска опорного элемента для текущей задачи
        // вернёт список индексов элемента если найден, [-1, -1] если задача решена, null если опорного элемента нет.
        Matrix mtx = simplexTable.getMatrix();
        ArrayList<Integer> supportCoordinates = new ArrayList<>();

        boolean isDecided = true;
        for(int i = 0; i < simplexTable.getFunction().size(); i++){
            if (simplexTable.getFunction().get(i).isMore(Fraction.ZERO) ){
                // если находимся на базисном столбце или коэффициент функции неотрицательный
                continue;
            }
            Fraction minValue = null;
            isDecided = false;
            for(int j = 0; j < mtx.getMatrix().length; j++) {
                if (mtx.getMatrix()[j][i].isMore(Fraction.ZERO)) {
                    minValue = mtx.getMatrix()[j][mtx.getMatrix()[0].length - 1]
                            .multiply(mtx.getMatrix()[j][i].getReverseFraction());
                    supportCoordinates.add(j); // добавляем номер ряда
                    supportCoordinates.add(i); // добавляем номер столбца
                    break;
                }
            }
            if (minValue == null){
                // нет подходящих элементов
                continue;
            }
            for(int j = 0; j < mtx.getMatrix().length; j++) {
                if (mtx.getMatrix()[j][i].isMore(Fraction.ZERO)) {
                    Fraction value = mtx.getMatrix()[j][mtx.getMatrix()[0].length - 1]
                            .multiply(mtx.getMatrix()[j][i].getReverseFraction());
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

    private static Boolean checkSupport(SimplexTable simplexTable, int supportRow, int supportColumn){
        // TODO: добавить проверку минимальности в столбце!
        return !simplexTable.getFunction().get(supportColumn).isMore(Fraction.ZERO)
                && simplexTable.getMatrix().getMatrix()[supportRow][supportColumn].isMore(Fraction.ZERO);
    }
}
