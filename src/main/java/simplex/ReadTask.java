package simplex;

import dataStorage.Fraction;
import dataStorage.Matrix;
import dataStorage.Task;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс, реализующий считывание задачи с различных источников
 */
public class ReadTask {
    /**
     * Метод, реализующий считывание задачи симплекс-метода с файла
     * @param filePath строка с путем к файлу с задачей
     * @return объект класса <code>Task</code> содержащий считанную задачу
     */
    public static Task readSM(String filePath ) {
        // TODO: изменить тип файла на json
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            Task currentTask;

            int countColumn = Integer.parseInt(reader.readLine()); // кол-во переменных

            ArrayList<Fraction> function = new ArrayList<>();
            String[] functionStr = reader.readLine().split(" +"); // коэффициенты исходной функции

            for(String num: functionStr){
                function.add(Fraction.parseFraction(num));
            }
            // если не введен свободный член функции
            if(function.size() == countColumn){
                countColumn += 1;
            }

            int countRestrictions = Integer.parseInt(reader.readLine()); // кол-во ограничений

            Matrix matrix = new Matrix(countRestrictions, countColumn); // нулевая матрица
            for(int i = 0; i < countRestrictions; i++){
                String[] dataRow = reader.readLine().split(" +");
                if (dataRow.length > countColumn) {
                    return new Task("ERROR: Количество переменных больше возможного.");
                }
                for(int j = 0; j < countColumn; j++){
                    matrix.addElementByIndex(i, j, Fraction.parseFraction(dataRow[j]));
                }
            }

            String baseStrOrNull = reader.readLine();
            if(baseStrOrNull != null){
                String[] baseStr = baseStrOrNull.split(" +");
                ArrayList<Integer> base = new ArrayList<>();
                for(String num: baseStr){
                    base.add(Integer.parseInt(num));
                }
                currentTask = new Task(function, matrix, false, base);
            } else{
                currentTask = new Task(function, matrix, false);
            }
            return currentTask;

        } catch (IOException e){
            return new Task("ERROR: Некорректные данные.");
        } catch (NumberFormatException e){
            return new Task("ERROR: Символ не является числом.");
        }
    }
}
