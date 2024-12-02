package simplex;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

public class Fraction {
    BigDecimal num;
    BigDecimal denom;
    Boolean isDouble;


    public Fraction(int num, int denom){
        if (denom == 0 && num != 0){
            System.out.println("ERROR: denominator can't be 0!");
            return;
        }
        if (denom < 0){
            this.num = new BigDecimal(-num);
            this.denom = new BigDecimal(-denom);
        } else{
            this.num = new BigDecimal(num);
            this.denom = new BigDecimal(denom);
        }
        isDouble = false;
    }

    public Fraction(int num){
        this.num = new BigDecimal(num);
        this.denom = BigDecimal.ONE;
        isDouble = false;
    }

    public Fraction(double num){
        this.num = new BigDecimal(num);
        this.denom = BigDecimal.ONE;
        isDouble = true;
    }

    private BigDecimal toDecimalValue() {
        return this.num.divide(this.denom, MathContext.DECIMAL128);
    }

    public static Fraction sum(Fraction f1, Fraction f2){
        if(f1.isDouble || f2.isDouble){
            BigDecimal res = f1.toDecimalValue().add(f2.toDecimalValue());
            return new Fraction(res.doubleValue());
        } else{
            BigDecimal newNum = f1.num.multiply(f2.denom).add(f2.num.multiply(f1.denom));
            BigDecimal newDenom = f1.denom.multiply(f2.denom);
            Fraction sumFraction =  new Fraction(newNum.intValue(), newDenom.intValue());
            sumFraction.reducing();
            return sumFraction;
        }
    }

    public static Fraction multiply(Fraction f1, Fraction f2){
        Fraction multiFraction;
        BigDecimal newNum = f1.num.multiply(f2.num);
        BigDecimal newDenum = f1.denom.multiply(f2.denom);

        if(f1.isDouble || f2.isDouble){
            multiFraction = new Fraction(newNum.doubleValue());
        } else {
            multiFraction = new Fraction(newNum.intValue(), newDenum.intValue());
        }
        multiFraction.reducing();
        return multiFraction;
    }

    public static Fraction difference(Fraction f1, Fraction f2){
        if(f1.isDouble || f2.isDouble){
            BigDecimal res = f1.toDecimalValue().subtract(f2.toDecimalValue());
            return new Fraction(res.doubleValue());
        } else {
            BigDecimal newNum = f1.num.multiply(f2.denom).subtract(f2.num.multiply(f1.denom));
            BigDecimal newDenom = f1.denom.multiply(f2.denom);
            Fraction diffFraction = new Fraction(newNum.intValue(), newDenom.intValue());
            diffFraction.reducing();
            return diffFraction;
        }
    }

    public static Fraction min(Fraction f1, Fraction f2){
        BigDecimal newF1Num = f1.num.multiply(f2.denom);
        BigDecimal newF2Num = f2.num.multiply(f1.denom);
        return newF1Num.min(newF2Num).equals(newF1Num) ? f1 : f2;
    }

    public static Fraction parseFraction(String fractionStr){
        //TODO: Не обрабатывать исключение, а выбрасывать!
        if (fractionStr.matches("-?\\d+")){
            return new Fraction(Integer.parseInt(fractionStr));
        }
        if (fractionStr.matches("[+-]?\\d*\\.?\\d+")) {
            return new Fraction(Double.parseDouble(fractionStr));
        }
        String[] fractionArr = fractionStr.split("/");
        if (fractionArr.length != 2){
            System.out.println("ERROR: incorrect fraction in file! Error string: " + fractionStr);
            return null;
        }
        try{
            return new Fraction(Integer.parseInt(fractionArr[0]), Integer.parseInt(fractionArr[1]));
        } catch (NumberFormatException ex){
            System.out.println("ERROR: incorrect fraction in file! Error string: " + fractionStr);
            return null;
        }
    }

    public Fraction sum(Fraction otherFraction){
        return sum(this, otherFraction);
    }

    public Fraction difference(Fraction otherFraction){
        return difference(this, otherFraction);
    }

    public Fraction multiply(Fraction otherFraction){
        return multiply(this, otherFraction);
    }

    public void reducing(){
        BigDecimal reducFrac = gcd(num, denom);
        num = num.divide(reducFrac);
        denom = denom.divide(reducFrac);
    }

    public Fraction getReverseFraction(){
        if(num.equals(BigDecimal.ZERO)) {
            return this;
        }
        return new Fraction(denom.intValue(), num.intValue());
    }

    public String toString(){
        if (this.isDouble){
            return String.format("%f", num.doubleValue());
        }
        return denom.equals(BigDecimal.ONE) || num.equals(BigDecimal.ZERO) ? String.format("%d", num.intValue())
                : String.format("%d/%d", num.intValue(), denom.intValue());
    }

    public static BigDecimal gcd(BigDecimal num1, BigDecimal num2){
        if (Objects.equals(num1, BigDecimal.ZERO) && Objects.equals(num2, BigDecimal.ZERO)) return BigDecimal.ONE;
        if (Objects.equals(num1, num2)) return num1;
        if (Objects.equals(num1, BigDecimal.ZERO)) return num2;
        if (Objects.equals(num2, BigDecimal.ZERO)) return num1;

        return gcd(num1.min(num2).abs(), num1.subtract(num2).abs());
    }
}
