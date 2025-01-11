package simplex;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import dataStorage.Fraction;
import dataStorage.Matrix;
import dataStorage.SimplexTable;
import dataStorage.Task;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

/**
 * Класс, реализующий считывание задачи с различных источников
 */
public class ReadTask {
    /**
     * Метод, реализующий считывание задачи с файла json.
     * @param jsonFile файл формата json.
     * @return объект класса <code>Task</code>, содержащий данные о задаче.
     */
    public static Task readSMFromJson(File jsonFile) {

        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(jsonFile); // объект для работы с деревом json

            if (!checkJson(rootNode)) {
                return new Task("Некорректный json файл!");
            }

            int countVars = rootNode.path("countVars").asInt(); // кол-во переменных
            int countRestrictions = rootNode.path("countRestrictions").asInt(); // кол-во ограничений
            String taskType = rootNode.path("taskType").asText(); // тип задачи
            String fracType = rootNode.path("fracType").asText(); // тип дробей function
            String mode = rootNode.path("mode").asText(); // способ решения (ручной или авто)

            // валидация данных
            if (countVars < 1 || countVars > 16) {
                return new Task("Некорректное количество переменных");
            }

            if (countRestrictions < 1 || countRestrictions > 16) {
                return new Task("Некорректное количество ограничений");
            }

            if (!Objects.equals(taskType, SimplexTable.MAX_TYPE) && !Objects.equals(taskType, SimplexTable.MIN_TYPE)) {
                return new Task("Некорректный тип задачи");
            }

            if (!Objects.equals(fracType, Fraction.DECIMAL) && !Objects.equals(fracType, Fraction.ORDINARY)) {
                return new Task("Некорректный тип дробей");
            }

            if (!Objects.equals(mode, Task.MANUAL_MODE) && !Objects.equals(mode, Task.AUTO_MODE)) {
                return new Task("Некорректный режим работы");
            }

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
                return new Task("Некорректная функция.");
            }
            for (int i = 0; i < functionNode.size(); i++) {
                Fraction fraction = Fraction.parseFraction(functionNode.get(i).asText());
                if (!Objects.equals(fraction.getFracType(), fracType) && !Objects.equals(fraction.getFracType(), Fraction.INTEGER)) {
                    return new Task("Некорректный тип дроби. ");
                }
                function.add(fraction);
            }

            // считывание ограничений
            Matrix matrix = new Matrix(countRestrictions, countVars + 1);
            JsonNode restrictionsNode = rootNode.path("restrictions");

            if (restrictionsNode.size() != countRestrictions) {
                return new Task("Некорректное количество ограничений.");
            }
            for (int i = 0; i < restrictionsNode.size(); i++) {
                JsonNode rowNode = restrictionsNode.get(i);
                if (rowNode.size() != countVars + 1) {
                    return new Task("Некорректное ограничение.");
                }
                for (int j = 0; j < rowNode.size(); j++) {
                    Fraction fraction = Fraction.parseFraction(rowNode.get(j).asText());
                    if (!Objects.equals(fraction.getFracType(), fracType) && !Objects.equals(fraction.getFracType(), Fraction.INTEGER)) {
                        return new Task("Некорректный тип дроби.");
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

    public static void saveSMasJson(Task task, File jsonFile) throws IOException {
        // Настройка ObjectMapper для сериализации
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> taskMap = task.toMap();
        mapper.writeValue(jsonFile, taskMap);
    }

    private static boolean checkJson(JsonNode jsonNode) {
        return jsonNode.has("countVars") && jsonNode.has("countRestrictions") &&
                jsonNode.has("taskType") && jsonNode.has("fracType") &&
                jsonNode.has("mode") && jsonNode.has("base") &&
                jsonNode.has("function") && jsonNode.has("restrictions");
    }
}
