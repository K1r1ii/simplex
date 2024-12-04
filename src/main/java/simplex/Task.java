package simplex;


import java.util.ArrayList;

public class Task {
    private ArrayList<Fraction> function;
    private Matrix matrix;
    private ArrayList<Integer> base;
    private boolean isDecide;

    public Task(ArrayList<Fraction> function, Matrix matrix, boolean isDecide,ArrayList<Integer> base){
        this.function = function;
        this.matrix = matrix;
        this.isDecide = isDecide;
        this.base = base;
    }

    public Task(ArrayList<Fraction> function, Matrix matrix, boolean isDecide){
        this.function = function;
        this.matrix = matrix;
        this.isDecide = isDecide;
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

    public boolean isDecide() {
        return isDecide;
    }

    public void setDecide(boolean decide) {
        isDecide = decide;
    }

    @Override
    public String toString(){
        String functionStr = "f = " + function.toString();
        String matrixStr = "Restrictions:\n" + matrix.toString();

        if(base != null){
            String baseStr = "Base: " + base.toString();
            return functionStr + "\n" + matrixStr + baseStr + "\n";
        } else {
            return functionStr + "\n" + matrixStr;
        }
    }
}
