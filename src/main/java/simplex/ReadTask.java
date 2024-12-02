package simplex;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ReadTask {
    public static Task readSM(String filePath ) {
        try(BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            Task currentTask;

            int countVar = Integer.parseInt(reader.readLine()); // кол-во переменных
            ArrayList<Fraction> function = new ArrayList<>();
            String[] functionStr = reader.readLine().split(" +"); // коэффициенты исходной функции
            //TODO: обработать исключение из Fraction.parseFraction(...)
            for(String num: functionStr){
                function.add(Fraction.parseFraction(num));
            }
            int countRestrictions = Integer.parseInt(reader.readLine()); // кол-во ограничений

            Matrix matrix = new Matrix(countRestrictions, countVar); // нулевая матрица
            for(int i = 0; i < countRestrictions; i++){
                String[] dataRow = reader.readLine().split(" +");
                if (dataRow.length > countVar) {
                    throw new IndexOutOfBoundsException("Слишком много переменных!");
                }
                for(int j = 0; j < countVar; j++){
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
                currentTask = new Task(function, matrix, base);
            } else{
                currentTask = new Task(function, matrix);
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
