package dataStorage;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Objects;

/**
 * Класс, реализующий работу с обычными дробями, а также представляющий операции для работы с этими дробями.
 */
public class Fraction {
    private BigDecimal num;
    private BigDecimal denom;
    private Boolean isDouble;
    public static Fraction ZERO = new Fraction(0);

    /**
     * Конструктор для сохранения обычной дроби
     * @param num числитель
     * @param denom знаменатель
     */
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

    /**
     * Конструктор для сохранения целого числа
     * @param num целое число
     */
    public Fraction(int num){
        this.num = new BigDecimal(num);
        this.denom = BigDecimal.ONE;
        isDouble = false;
    }

    /**
     * Конструктор для сохранения десятичной дроби
     * @param num десятичная дробь
     */
    public Fraction(double num){
        this.num = new BigDecimal(num);
        this.denom = BigDecimal.ONE;
        isDouble = true;
    }

    /**
     * Метод для конвертации числа в тип <code>BigDecimal</code>
     * @return объект класса <code>BigDecimal</code> содержащий входное число.
     */
    private BigDecimal toDecimalValue() {
        return this.num.divide(this.denom, MathContext.DECIMAL128);
    }

    /**
     * Статический метод, реализующий сложение двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code>  содержащий результат сложения двух дробей.
     */
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

    /**
     * Статический метод, реализующий умножение двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code>  содержащий результат перемножения двух дробей.
     */
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

    /**
     * Статический метод, реализующий разность двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code>  содержащий результат разности двух дробей.
     */
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

    /**
     * Статический метод, реализующий поиск минимальной дроби из двух.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code>  содержащий минимальную дробь.
     */
    public static Fraction min(Fraction f1, Fraction f2){
        BigDecimal newF1Num = f1.num.multiply(f2.denom);
        BigDecimal newF2Num = f2.num.multiply(f1.denom);
        return newF1Num.min(newF2Num).equals(newF1Num) ? f1 : f2;
    }

    /**
     * Метод, реализующий проверку: является ли дробь больше переданной.
     * @param other вторая дробь для сравнения
     * @return <code>true</code> если текущая дробь больше
     *         <code>false</code> если текущая дробь меньше
     */
    public Boolean isMore(Fraction other){
        BigDecimal newThisNum = this.num.multiply(other.denom);
        BigDecimal newOtherNum = other.num.multiply(this.denom);
        return newThisNum.subtract(newOtherNum).doubleValue() > 0;
    }

    /**
     * Метод, реализующий конвертацию строки с обычной дробью в объект класса <code>Fraction</code>
     * @param fractionStr строка с дробью
     * @return объект класса <code>Fraction</code>  содержащий результат считывания строки.
     * @throws NumberFormatException исключение в случае, когда в строке оказалось некорректная запись.
     */
    public static Fraction parseFraction(String fractionStr) throws NumberFormatException {
        if (fractionStr.matches("-?\\d+")){
            return new Fraction(Integer.parseInt(fractionStr));
        }
        if (fractionStr.matches("[+-]?\\d*\\.?\\d+")) {
            return new Fraction(Double.parseDouble(fractionStr));
        }
        String[] fractionArr = fractionStr.split("/");
        if (fractionArr.length != 2){
            throw new NumberFormatException("ERROR: incorrect fraction in file! Error string: " + fractionStr);
        }
        try{
            return new Fraction(Integer.parseInt(fractionArr[0]), Integer.parseInt(fractionArr[1]));
        } catch (NumberFormatException ex){
            throw new NumberFormatException("ERROR: incorrect fraction in file! Error string: " + fractionStr);
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

    /**
     * Метод для сокращения дроби.
     */
    public void reducing(){
        BigDecimal reducFrac = gcd(num, denom);
        num = num.divide(reducFrac);
        denom = denom.divide(reducFrac);
    }

    /**
     * Метод, реализующий "переворот" дроби
     * @return объект класса <code>Fraction</code>  содержащий перевернутую дробь
     */
    public Fraction getReverseFraction(){
        if(num.equals(BigDecimal.ZERO)) {
            return this;
        }
        return new Fraction(denom.intValue(), num.intValue());
    }

    /**
     * Метод, реализующий поиск наибольшего общего делителя для двух чисел.
     * @param num1 первое число
     * @param num2 второе число
     * @return объект класса <code>BigDecimal</code> содержащий наибольший общий делитель двух чисел.
     */
    private static BigDecimal gcd(BigDecimal num1, BigDecimal num2){
        if (Objects.equals(num1, BigDecimal.ZERO) && Objects.equals(num2, BigDecimal.ZERO)) return BigDecimal.ONE;
        if (Objects.equals(num1, num2)) return num1;
        if (Objects.equals(num1, BigDecimal.ZERO)) return num2;
        if (Objects.equals(num2, BigDecimal.ZERO)) return num1;

        return gcd(num1.min(num2).abs(), num1.subtract(num2).abs());
    }

    public BigDecimal getNum() {
        return num;
    }

    public void setNum(BigDecimal num) {
        this.num = num;
    }

    public BigDecimal getDenom() {
        return denom;
    }

    public void setDenom(BigDecimal denom) {
        this.denom = denom;
    }

    public Boolean getDouble() {
        return isDouble;
    }

    public void setDouble(Boolean aDouble) {
        isDouble = aDouble;
    }

    @Override
    public String toString(){
        if (this.isDouble){
            return String.format("%f", num.doubleValue());
        }
        return denom.equals(BigDecimal.ONE) || num.equals(BigDecimal.ZERO) ? String.format("%d", num.intValue())
                : String.format("%d/%d", num.intValue(), denom.intValue());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true; // Сравнение ссылок
        if (obj == null || getClass() != obj.getClass()) return false; // Проверка типа

        Fraction other = (Fraction) obj;

        // Сравнение числителя и знаменателя
        return Objects.equals(this.num, other.num) && Objects.equals(this.denom, other.denom);
    }
}
