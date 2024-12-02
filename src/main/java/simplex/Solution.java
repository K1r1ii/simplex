package simplex;

import java.util.ArrayList;

public class Solution {
    private Fraction functionValue;
    private ArrayList<Fraction> vectorSolution;

    public Solution(Fraction functionValue, ArrayList<Fraction> vectorSolution){
        this.functionValue = functionValue;
        this.vectorSolution = vectorSolution;
    }

    public Fraction getFunctionValue() {
        return functionValue;
    }

    public void setFunctionValue(Fraction functionValue) {
        this.functionValue = functionValue;
    }

    public ArrayList<Fraction> getVectorSolution() {
        return vectorSolution;
    }

    public void setVectorSolution(ArrayList<Fraction> vectorSolution) {
        this.vectorSolution = vectorSolution;
    }

    @Override
    public String toString(){
        return "f = " + functionValue.toString() + "\n" + "x = " + vectorSolution.toString();
    }
}

