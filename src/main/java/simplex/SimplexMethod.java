package simplex;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimplexMethod {
    public static Solution autoMode(){
        // TODO: сделать метод для реализации автоматического режима
        return null;
    }

    public static Task simplexStep(Task curTask, int supportRow, int supportColumn){
        int sRow, sCol;
        ArrayList<Fraction> newFunction = curTask.getFunction();
        ArrayList<Integer> newBase = curTask.getBase();
        Matrix matrix = curTask.getMatrix();
        Matrix newMatrix = new Matrix(matrix.getMatrix().length, matrix.getMatrix()[0].length);

        ArrayList<Integer> coords;
        // получение опорного элемента
        if (supportRow == -1 || supportColumn == -1){
            coords = findSupport(curTask);
        } else {
            if (!checkSupport(curTask, supportRow, supportColumn)){
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
            curTask.setDecide(true);
            return curTask;
        }

        sRow = coords.get(0);
        sCol = coords.get(1);
        Fraction sValue = matrix.getMatrix()[sRow][sCol]; // значение опорного элемента

        // замена базиса
        newBase.set(sRow, sCol + 1);

        // обновление опорного элемента
        newMatrix.addElementByIndex(sRow, sCol, sValue.getReverseFraction());
        System.out.println(newMatrix.getMatrix()[sRow][sCol]);

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

        return new Task(newFunction, newMatrix, false, newBase);
    }


    public static Task simplexStepBack(Task curTask, int supportRow, int supportColumn, Fraction supportValue){
        // TODO: сделать метод для возврата к предыдущему шагу по опорному элементу
        return null;
    }

    public static Task gauss(Task curTask){
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
        for(int i = 0; i < curTask.getBase().size(); i++){
            mtx.replaceColumns(curTask.getBase().get(i) - 1, i);
        }

        // список для измененной функции зполненный 0
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

        return new Task(newFunction, mtx, false, curTask.getBase());
    }


    private static ArrayList<Integer> findSupport(Task curTask){
        // Метод для поиска опорного элемента для текущей задачи
        // вернёт список индексов элемента если найден, [-1, -1] если задача решена, null если опорного элемента нет.
        Matrix mtx = curTask.getMatrix();
        ArrayList<Integer> supportCoordinates = new ArrayList<>();

        boolean isDecided = true;
        for(int i = 0; i < curTask.getFunction().size(); i++){
            if (curTask.getBase().contains(i + 1) || curTask.getFunction().get(i).isMore(Fraction.ZERO) ){
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

    private static Boolean checkSupport(Task curTask, int supportRow, int supportColumn){
        return !curTask.getFunction().get(supportColumn).isMore(Fraction.ZERO)
                && curTask.getMatrix().getMatrix()[supportRow][supportColumn].isMore(Fraction.ZERO);
    }
}
