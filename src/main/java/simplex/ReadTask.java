package simplex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadTask {
    public static Task readSM(String filePath ) {
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
                    throw new IndexOutOfBoundsException("Слишком много переменных!");
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
            System.out.println("ERROR: " + e);
            return null;
        } catch (NumberFormatException e){
            System.out.println("ERROR: string is not number!");
            return null;
        }
    }
}
