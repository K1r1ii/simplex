import simplex.*;


public class Hello {
    public static void main(String[] args){
        String filePath = "test_5.txt";
        Task testTask = ReadTask.readSM(filePath);
        if(testTask != null) {
            System.out.println(testTask);
            SimplexTable gausTask = SimplexMethod.gauss(testTask);
            Solution solution = SimplexMethod.autoMode(gausTask);
            System.out.println(solution);
        }

    }
}
