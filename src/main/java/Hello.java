import simplex.Fraction;
import simplex.ReadTask;
import simplex.Task;

public class Hello {
    public static void main(String[] args){
        Fraction f1 = new Fraction(2, 3);
        Fraction f2 = new Fraction(2, 7);
        Fraction f3 = new Fraction(3);
        Fraction f4 = new Fraction(3.24);
        Fraction f6 = new Fraction(0.13);
        Fraction f5 = new Fraction(4, 3);

//        System.out.println(f1.sum(f2).toString());
//        System.out.println(f4.toString());
//        System.out.println(f5.sum(f4));
//        System.out.println(f6.sum(f4));
//        System.out.println(f1.sum(f5));
        String filePath = "test.txt";
        Task testTask = ReadTask.readSM(filePath);
        if(testTask != null){
            System.out.println(testTask.toString());
        }
    }
}
