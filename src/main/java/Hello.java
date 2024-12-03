import simplex.Fraction;
import simplex.ReadTask;
import simplex.SimplexMethod;
import simplex.Task;

import java.util.ArrayList;

public class Hello {
    public static void main(String[] args){
//        Fraction f1 = new Fraction(2, 3);
//        Fraction f2 = new Fraction(0);
//        System.out.println(f2.difference(f1));
        String filePath = "test_3.txt";
        Task testTask = ReadTask.readSM(filePath);
        if(testTask != null){
            System.out.println(testTask.toString());
            Task gausTask = SimplexMethod.gauss(testTask);
            if(gausTask != null) {
                Task step0 = SimplexMethod.simplexStep(gausTask, -1, -1);
//                System.out.println(step0);
                if(step0 != null) {
                    Task step1 = SimplexMethod.simplexStep(step0, -1, -1);
                    System.out.println(step0);
                    System.out.println(step1);
                }
            }
        }

    }
}
