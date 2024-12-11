import artificialBasis.ArtificialBasisMethod;
import dataStorage.SimplexTable;
import dataStorage.Solution;
import dataStorage.Task;
import simplex.*;

// TODO: считывание с json, добавить тип задачи (min, max), добавить тип дробей

public class Hello {
    public static void main(String[] args){
        String filePath = "test_data/test_vasya_2.txt";
        // считывание данных с файла
        Task testTask = ReadTask.readSM(filePath);
        if (testTask.getErrorMessage() != null) {
            System.out.println(testTask.getErrorMessage());
            return;
        }

        System.out.println("Задача:\n" + testTask);

        // решение с использованием метода искусственного базиса
        artificialBasis(testTask);

        // решение с готовым базисом
        classicSimplex(testTask);

    }

    public static void classicSimplex(Task curTask) {
        SimplexTable gausTask = SimplexMethod.gauss(curTask);
        if (gausTask.getErrorMassage() != null) {
            System.out.println(gausTask.getErrorMassage());
            return;
        }
        System.out.println("Гаусс:\n" + gausTask);

        // запуск автоматического режима симплекс метода для решения задачи
        Solution solution = SimplexMethod.autoMode(gausTask);
        if (solution.getErrorMessage() != null) {
            System.out.println(solution.getErrorMessage());
            return;
        }

        // вывод ответа
        System.out.println("Решение:\n" + solution);
    }

    public static void artificialBasis(Task curTask) {
        // искусственный базис
        SimplexTable test4 = ArtificialBasisMethod.autoMode(curTask);
        if (test4.getErrorMassage() != null) {
            System.out.println(test4.getErrorMassage());
            return;
        }
        System.out.println("Результат искусственного базиса:\n" + test4);
        Solution solution = SimplexMethod.autoMode(test4);
        if (solution.getErrorMessage() != null) {
            System.out.println(solution.getErrorMessage());
            return;
        }
        System.out.println(solution);
    }
}