import artificialBasis.ArtificialBasisMethod;
import dataStorage.SimplexTable;
import dataStorage.Solution;
import dataStorage.Task;
import simplex.*;

// TODO: добавить тип задачи (min, max), добавить тип дробей; SceneBuilder

public class Hello {
    public static void main(String[] args){


        String filePath = "test_data/test_j1.json";
        // считывание данных с файла
        Task testTask = ReadTask.readSMFromJson(filePath);
        if (testTask.getErrorMessage() != null) {
            System.out.println(testTask.getErrorMessage());
            return;
        }

        System.out.println("Задача:\n" + testTask);

        if (testTask.getBase() == null) {
            // решение с использованием метода искусственного базиса
            artificialBasis(testTask);

        } else {
            // решение с готовым базисом
            classicSimplex(testTask);
        }

    }

    public static void classicSimplex(Task curTask) {
        SimplexTable gausTask = SimplexMethod.gauss(curTask);
        if (gausTask.getErrorMassage() != null) {
            System.out.println(gausTask.getErrorMassage());
            return;
        }
        System.out.println("Гаусс:" + gausTask);

        // запуск автоматического режима симплекс метода для решения задачи
        Solution solution = SimplexMethod.autoMode(gausTask);
        if (solution.getErrorMessage() != null) {
            System.out.println(solution.getErrorMessage());
            return;
        }

        // вывод ответа
        System.out.println(solution);
    }

    public static void artificialBasis(Task curTask) {
        // искусственный базис
        SimplexTable test4 = ArtificialBasisMethod.autoMode(curTask);
        if (test4.getErrorMassage() != null) {
            System.out.println(test4.getErrorMassage());
            return;
        }
        System.out.println("Результат искусственного базиса:" + test4);
        Solution solution = SimplexMethod.autoMode(test4);
        if (solution.getErrorMessage() != null) {
            System.out.println(solution.getErrorMessage());
            return;
        }
        System.out.println(solution);
    }
}