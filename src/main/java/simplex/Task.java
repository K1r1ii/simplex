package simplex;


import java.util.ArrayList;

public class Task {
    private ArrayList<Fraction> function;
    private Matrix matrix;
    private ArrayList<Integer> base;

    public Task(ArrayList<Fraction> function, Matrix matrix, ArrayList<Integer> base){
        this.function = function;
        this.matrix = matrix;
        this.base = base;
    }

    public Task(ArrayList<Fraction> function, Matrix matrix){
        this.function = function;
        this.matrix = matrix;
        this.base = null;
    }

    public ArrayList<Fraction> getFunction() {
        return function;
    }

    public void setFunction(ArrayList<Fraction> function) {
        this.function = function;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public ArrayList<Integer> getBase() {
        return base;
    }

    public void setBase(ArrayList<Integer> base) {
        this.base = base;
    }
    @Override
    public String toString(){
        String functionStr = "f = " + function.toString();
        String matrixStr = "Restrictions:\n" + matrix.toString();

        if(base != null){
            String baseStr = "Base: " + base.toString();
            return functionStr + "\n" + matrixStr + "\n" + baseStr;
        } else {
            return functionStr + "\n" + matrixStr;
        }
    }
}
