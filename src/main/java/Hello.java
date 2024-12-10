import simplex.*;


public class Hello {
    public static void main(String[] args){
        String filePath = "test_5.txt";
        // считывание данных с файла
        Task testTask = ReadTask.readSM(filePath);
        if (testTask.getErrorMessage() != null) {
            System.out.println(testTask.getErrorMessage());
            return;
        }
        System.out.println("Задача:\n" + testTask);

        // запуск метода Гаусса для задачи
        SimplexTable gausTask = SimplexMethod.gauss(testTask);
        if (gausTask.getErrorMassage() != null) {
            System.out.println(gausTask.getErrorMassage());
            return;
        }

        // запуск автоматического режима симплекс метода для решения задачи
        Solution solution = SimplexMethod.autoMode(gausTask);
        if (solution.getErrorMessage() != null) {
            System.out.println(solution.getErrorMessage());
            return;
        }

        // вывод ответа
        System.out.println("Решение:\n" + solution);

    }
}