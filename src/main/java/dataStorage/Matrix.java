package dataStorage;

import java.util.ArrayList;

/**
 * Класс, реализующий хранение матрицы и методы, реализующие операции с ними.
 */
public class Matrix {
    private Fraction[][] matrix;

    /**
     * Конструктор для инициализации нулевой матрицы заданных размеров
     * @param rows количество рядов
     * @param columns количество столбцов
     */
    public Matrix(int rows, int columns){
        matrix = new Fraction[rows][columns];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < columns; j++){
                matrix[i][j] = new Fraction(0);
            }
        }
    }

    /**
     * Конструктор для инициализации готовой матрицы
     * @param matrix матрица
     */
    public Matrix(Fraction[][] matrix){
        this.matrix = matrix;
    }

    /**
     * Метод, реализующий добавление элемента на конкретное место
     * @param i номер ряда элемента
     * @param j номер столбца элемента
     * @param element новое значение элемента
     */
    public void addElementByIndex(int i, int j, Fraction element){
        matrix[i][j] = element;
    }


    /**
     *  Метод для добавления нового столбца в матрицу
     * @param delColumn данные о новом столбце
     */
    public void addColumnByIndex(DelColumn delColumn) {
        ArrayList<Fraction> column = delColumn.getColumn();
        int columnIndex = delColumn.getColumnIndex();

        Matrix newMatrix = new Matrix(matrix.length, matrix[0].length + 1);
        Fraction[][] newMtx = newMatrix.getMatrix();
        for (int i = 0; i < newMtx.length; i++) {
            int count = 0;
            for (int j = 0; j < newMtx[0].length; j++) {
                if (columnIndex == j) {
                    newMatrix.addElementByIndex(i, j, column.get(i));
                    count++;
                } else {
                    newMatrix.addElementByIndex(i, j, matrix[i][j - count]);
                }
            }
        }
        setMatrix(newMtx);
    }

    /**
     * Метод для добавления новой строки в матрицу
     * @param delString данные о новой строке
     */
    public void addRowByIndex(DelString delString) {
        ArrayList<Fraction> string = delString.getString();
        int stringIndex = delString.getStringIndex();

        Matrix newMatrix = new Matrix(matrix.length + 1, matrix[0].length);
        Fraction[][] newMtx = newMatrix.getMatrix();
        int count = 0;
        for (int i = 0; i < newMtx.length; i++) {
            if (stringIndex == i) count++;
            for (int j = 0; j < newMtx[0].length; j++) {
                if (stringIndex == i) {
                    newMatrix.addElementByIndex(i, j, string.get(j));
                } else {
                    newMatrix.addElementByIndex(i, j, matrix[i - count][j]);
                }
            }
        }
        setMatrix(newMtx);
    }

    /**
     * Метод, реализующий перестановку двух рядов
     * @param i номер первого ряда
     * @param j номер второго ряда
     */
    public void replaceRows(int i, int j){
        Fraction[] firstRow  = matrix[i];
        matrix[i] = matrix[j];
        matrix[j] = firstRow;
    }

    /**
     * Метод, реализующий перестановку двух столбцов
     * @param i номер первого столбца
     * @param j номер второго столбца
     */
    public void replaceColumns(int i, int j){
        // change matrix
        for(int iRow = 0; iRow < matrix.length; iRow++){
            Fraction firstC = matrix[iRow][i];
            matrix[iRow][i] = matrix[iRow][j];
            matrix[iRow][j] = firstC;
        }
    }

    /**
     * Метод, реализующий домножение ряда на число
     * @param i номер ряда
     * @param number число для домножения
     */
    public void multiplyRowByNumber(int i, Fraction number){
        for(int j = 0; j < matrix[i].length; j++){
            matrix[i][j] = matrix[i][j].multiply(number);
        }
    }

    /**
     * Метод, реализующий зануление выбранного ряда
     * @param iNullingRow номер зануляемого ряда
     * @param iCurrentRow номер текущего ряда в методе гаусса
     * @param curColumn номер текущего столбца
     */
    public void nullingRow(int iNullingRow, int iCurrentRow, int curColumn){
        Fraction firstEl = matrix[iNullingRow][curColumn];
        for(int i = curColumn; i < matrix[iNullingRow].length; i++){
            matrix[iNullingRow][i] = matrix[iNullingRow][i]
                    .subtract(matrix[iCurrentRow][i]
                            .multiply(firstEl));
        }
    }


    /**
     * Метод для удаления заданной строки из матрицы
     * @param indRow индекс заданной строки
     */
    public void deleteRow(int indRow) {
        int rows = getMatrix().length;
        int columns = getMatrix()[0].length;
        int temp = 0;

        Matrix newMatrix = new Matrix(rows - 1, columns);
        for(int i = 0; i < rows; i++){
            if (i == indRow) {
                temp++;
                continue;
            }
            for(int j = 0; j < columns; j++){
                newMatrix.addElementByIndex(i+temp, j, getMatrix()[i][j]);
            }
        }
        setMatrix(newMatrix.getMatrix());
    }

    /**
     * Метод, реализующий вывод матрицы в консоль
     * @param msg сообщение, которое будет выведено перед матрицей
     */
    public void printM(String msg){
        System.out.println("Step: " + msg);

        int maxLen = String.valueOf(matrix[0].length).length() + 1;
        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                maxLen = Math.max(fractions[j].toString().length(), maxLen);
            }
        }

        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                String format = "%" + maxLen + "s ";
                System.out.printf(format, fractions[j].toString());
            }
            System.out.print("\n");
        }
    }

    @Override
    public String toString(){
        String mtxStr = "";
        int maxLen = String.valueOf(matrix[0].length).length() + 1;
        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                maxLen = Math.max(fractions[j].toString().length(), maxLen);
            }
        }

        for (Fraction[] fractions : matrix) {
            for (int j = 0; j < matrix[0].length; j++) {
                String format = "%" + maxLen + "s ";
                mtxStr += String.format(format, fractions[j].toString());
            }
            mtxStr += "\n";

        }
        return mtxStr;
    }

    public Fraction[][] getMatrix(){
        return this.matrix;
    }

    public void setMatrix(Fraction[][] newMatrix){
        this.matrix = newMatrix;
    }
}
