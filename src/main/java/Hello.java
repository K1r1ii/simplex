import artificialBasis.ArtificialBasisMethod;
import dataStorage.Fraction;
import dataStorage.SimplexTable;
import dataStorage.Solution;
import dataStorage.Task;
import simplex.*;

import java.math.BigDecimal;


public class Hello {
    public static void main(String[] args){
        String filePath = "test_vasya_2.txt";
        // считывание данных с файла
        Task testTask = ReadTask.readSM(filePath);
        if (testTask.getErrorMessage() != null) {
            System.out.println(testTask.getErrorMessage());
            return;
        }

        System.out.println("Задача:\n" + testTask);

//        SimplexTable test = ArtificialBasisMethod.createSimplexTable(testTask);
//        System.out.println(test);
//
//        SimplexTable test1 = ArtificialBasisMethod.artificialBasisStep(test, -1, -1, testTask.getFunction());
//        System.out.println(test1);
//
//        SimplexTable test2 = ArtificialBasisMethod.artificialBasisStep(test1, -1, -1, testTask.getFunction());
//        System.out.println(test2);
//
//        SimplexTable test3 = ArtificialBasisMethod.artificialBasisStep(test2, -1, -1, testTask.getFunction());
//        System.out.println(test3);

        // искусственный базис
        SimplexTable test4 = ArtificialBasisMethod.autoMode(testTask);
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

        // решение с готовым базисом

        // запуск метода Гаусса для задачи
//        SimplexTable gausTask = SimplexMethod.gauss(testTask);
//        if (gausTask.getErrorMassage() != null) {
//            System.out.println(gausTask.getErrorMassage());
//            return;
//        }
//        System.out.println("Гаусс:\n" + gausTask);
//
//        // запуск автоматического режима симплекс метода для решения задачи
//        Solution solution = SimplexMethod.autoMode(gausTask);
//        if (solution.getErrorMessage() != null) {
//            System.out.println(solution.getErrorMessage());
//            return;
//        }
//
//        // вывод ответа
//        System.out.println("Решение:\n" + solution);

    }
}