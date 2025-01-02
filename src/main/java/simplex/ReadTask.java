package simplex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dataStorage.Fraction;
import dataStorage.Matrix;
import dataStorage.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

/**
 * Класс, реализующий считывание задачи с различных источников
 */
public class ReadTask {
    /**
     * Метод, реализующий считывание задачи с файла json.
     * @param filePath путь к файлу.
     * @return объект класса <code>Task</code>, содержащий данные о задаче.
     */
    public static Task readSMFromJson(String filePath) {
        File jsonFile = new File(filePath);  // Путь к json

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile); // объект для работы с деревом json

            int countVars = rootNode.path("countVars").asInt(); // кол-во переменных
            int countRestrictions = rootNode.path("countRestrictions").asInt(); // кол-во ограничений
            String taskType = rootNode.path("taskType").asText(); // тип задачи
            String fracType = rootNode.path("fracType").asText(); // тип дробейfunction
            String mode = rootNode.path("mode").asText(); // способ решения (ручной или авто)


            // считывание базиса
            ArrayList<Integer> base = new ArrayList<>();
            JsonNode baseNode = rootNode.path("base");

            if (!Objects.equals(baseNode.asText(), "null")) {
                for (int i = 0; i < baseNode.size(); i++) {
                    base.add(baseNode.get(i).asInt());
                }
            } else {
                base = null;
            }

            // считывание функции
            ArrayList<Fraction> function = new ArrayList<>();
            JsonNode functionNode = rootNode.path("function");
            if (functionNode.size() != countVars) {
                return new Task("ERROR: Некорректная функция.");
            }
            for (int i = 0; i < functionNode.size(); i++) {
                Fraction fraction = Fraction.parseFraction(functionNode.get(i).asText());
                if (!Objects.equals(fraction.getFracType(), fracType) && !Objects.equals(fraction.getFracType(), Fraction.INTEGER)) {
                    return new Task("ERROR: Некорректный тип дроби. ");
                }
                function.add(fraction);
            }

            // считывание ограничений
            Matrix matrix = new Matrix(countRestrictions, countVars + 1);
            JsonNode restrictionsNode = rootNode.path("restrictions");

            if (restrictionsNode.size() != countRestrictions) {
                return new Task("ERROR: Некорректное количество ограничений.");
            }
            for (int i = 0; i < restrictionsNode.size(); i++) {
                JsonNode rowNode = restrictionsNode.get(i);
                if (rowNode.size() != countVars + 1) {
                    return new Task("ERROR: Некорректное ограничение.");
                }
                for (int j = 0; j < rowNode.size(); j++) {
                    Fraction fraction = Fraction.parseFraction(rowNode.get(j).asText());
                    if (!Objects.equals(fraction.getFracType(), fracType) && !Objects.equals(fraction.getFracType(), Fraction.INTEGER)) {
                        return new Task("ERROR: Некорректный тип дроби.");
                    }
                    matrix.addElementByIndex(i, j, fraction);
                }
            }

            if (base != null){
                return new Task(function, matrix, false, base, taskType, fracType, mode);
            } else {
                return new Task(function, matrix, false, taskType, fracType, mode);
            }

        } catch (IOException e) {
            return new Task("ERROR: Некорректные данные. " + e);
        }
    }
}
