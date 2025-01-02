package dataStorage;

import java.util.Objects;

/**
 * Класс, реализующий работу с обычными дробями, а также представляющий операции для работы с этими дробями.
 */
public class Fraction {
    private long num;
    private long denom;
    private boolean isDouble;
    private String fracType;

    public static final Fraction ZERO = new Fraction(0);
    public static final Fraction ONE = new Fraction(1);
    public static final String DECIMAL = "decimal";
    public static final String ORDINARY = "ordinary";
    public static final String INTEGER = "integer";

    /**
     * Конструктор для сохранения обычной дроби
     * @param num числитель
     * @param denom знаменатель
     */
    public Fraction(long num, long denom) {
        if (denom == 0 && num != 0) {
            throw new ArithmeticException("ERROR: denominator can't be 0!");
        }
        if (denom < 0) {
            this.num = -num;
            this.denom = -denom;
        } else {
            this.num = num;
            this.denom = denom;
        }
        isDouble = false;
        fracType = ORDINARY;
    }

    /**
     * Конструктор для сохранения целого числа
     * @param num целое число
     */
    public Fraction(long num) {
        this.num = num;
        this.denom = 1;
        isDouble = false;
        fracType = INTEGER;
    }

    /**
     * Конструктор для сохранения десятичной дроби
     * @param num десятичная дробь
     */
    public Fraction(double num) {
        String[] parts = Double.toString(num).split("\\.");
        long decimalPlaces = parts[1].length();
        long denom = (long) Math.pow(10, decimalPlaces);

        this.num = (long) (num * denom);
        this.denom = denom;
        isDouble = true;
        fracType = DECIMAL;
        reducing();
    }


    public Fraction sum(Fraction other) {
        return sum(this, other);
    }


    public Fraction multiply(Fraction other) {
        return multiply(this, other);
    }


    public Fraction subtract(Fraction other) {
        return subtract(this, other);
    }


    public Fraction divide(Fraction other) {
        return divide(this, other);
    }

    /**
     * Статический метод, реализующий сложение двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code> содержащий результат сложения двух дробей.
     */
    public static Fraction sum(Fraction f1, Fraction f2) {
        long newNum = f1.num * f2.denom + f2.num * f1.denom;
        long newDenom = f1.denom * f2.denom;
        Fraction sumFraction = new Fraction(newNum, newDenom);
        if (Objects.equals(f1.fracType, ORDINARY) && Objects.equals(f2.fracType, ORDINARY)) {
            sumFraction.reducing();
        }
        return sumFraction;
    }


    /**
     * Статический метод, реализующий умножение двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code> содержащий результат перемножения двух дробей.
     */
    public static Fraction multiply(Fraction f1, Fraction f2) {
        long newNum = f1.num * f2.num;
        long newDenom = f1.denom * f2.denom;
        Fraction multiFraction = new Fraction(newNum, newDenom);
        if (Objects.equals(f1.fracType, ORDINARY) && Objects.equals(f2.fracType, ORDINARY)) {
            multiFraction.reducing();
        }
        return multiFraction;
    }

    /**
     * Статический метод, реализующий разность двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code> содержащий результат разности двух дробей.
     */
    public static Fraction subtract(Fraction f1, Fraction f2) {
        long newNum = f1.num * f2.denom - f2.num * f1.denom;
        long newDenom = f1.denom * f2.denom;
        Fraction diffFraction = new Fraction(newNum, newDenom);
        if (Objects.equals(f1.fracType, ORDINARY) && Objects.equals(f2.fracType, ORDINARY)) {
            diffFraction.reducing();
        }
        return diffFraction;
    }

    /**
     * Статический метод, реализующий деление двух дробей.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code> содержащий результат деления двух дробей.
     */
    public static Fraction divide(Fraction f1, Fraction f2) {
        if (f2.num == 0) {
            throw new ArithmeticException("Деление на 0!");
        }
        return multiply(f1, f2.getReverseFraction());
    }

    /**
     * Метод для сокращения дроби.
     */
    public void reducing() {
        long gcd = gcd(num, denom);
        num /= gcd;
        denom /= gcd;
    }


    /**
     * Метод, реализующий конвертацию строки с обычной дробью в объект класса <code>Fraction</code>
     * @param fractionStr строка с дробью
     * @return объект класса <code>Fraction</code>  содержащий результат считывания строки.
     * @throws NumberFormatException исключение в случае, когда в строке оказалось некорректная запись.
     */
    public static Fraction parseFraction(String fractionStr) throws NumberFormatException {
        if (fractionStr.matches("-?\\d+")){
            return new Fraction(Long.parseLong(fractionStr));
        }
        if (fractionStr.matches("[+-]?\\d*\\.?\\d+")) {
            return new Fraction(Double.parseDouble(fractionStr));
        }
        String[] fractionArr = fractionStr.split("/");
        if (fractionArr.length != 2){
            throw new NumberFormatException("ERROR: incorrect fraction in file! Error string: " + fractionStr);
        }
        try{
            return new Fraction(Long.parseLong(fractionArr[0]), Long.parseLong(fractionArr[1]));
        } catch (NumberFormatException ex){
            throw new NumberFormatException("ERROR: incorrect fraction in file! Error string: " + fractionStr);
        }
    }


    /**
     * Метод, реализующий "переворот" дроби
     * @return объект класса <code>Fraction</code> содержащий перевернутую дробь
     */
    public Fraction getReverseFraction() {
        if (num == 0) {
            throw new ArithmeticException("Cannot reverse a fraction with a numerator of 0.");
        }
        return new Fraction(denom, num);
    }

    /**
     * Метод, реализующий поиск наибольшего общего делителя для двух чисел.
     * @param a первое число
     * @param b второе число
     * @return наибольший общий делитель двух чисел.
     */
    private static long gcd(long a, long b) {
        if (b == 0) return Math.abs(a);
        return gcd(b, a % b);
    }


    /**
     * Статический метод, реализующий поиск минимальной дроби из двух.
     * @param f1 первая дробь
     * @param f2 вторая дробь
     * @return объект класса <code>Fraction</code>  содержащий минимальную дробь.
     */
    public static Fraction min(Fraction f1, Fraction f2){
        long newF1Num = f1.num * f2.denom;
        long newF2Num = f2.num * f1.denom;
        return newF1Num < newF2Num ? f1 : f2;
    }


    /**
     * Метод, переводящий обычную дробь в десятичную
     * @return результат деления числителя на знаменатель
     */
    public double toDecimal() {
        return (double) this.num / this.denom;
    }


    /**
     * Метод для проверки: является ли дробь больше заданной
     * @param other значение другой дроби
     * @return <code>true</code> если исходная дробь больше
     *         <code>false</code> иначе
     */
    public boolean isMore(Fraction other) {
        long newF1Num = this.num * other.denom;
        long newF2Num = other.num * this.denom;
        return newF1Num > newF2Num;
    }

    public String getFracType() {
        return fracType;
    }

    public void setFracType(String fracType) {
        this.fracType = fracType;
    }

    public long getNum() {
        return num;
    }

    public void setNum(long num) {
        this.num = num;
    }

    public long getDenom() {
        return denom;
    }

    public void setDenom(long denom) {
        this.denom = denom;
    }

    public boolean isDouble() {
        return isDouble;
    }

    public void setDouble(boolean aDouble) {
        isDouble = aDouble;
    }

    @Override
    public String toString() {
        if (this.isDouble) {
            return String.valueOf((double) num / denom);
        }
        return denom == 1 || num == 0 ? String.valueOf(num) : num + "/" + denom;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Fraction other = (Fraction) obj;
        return this.num * other.denom == other.num * this.denom;
    }
}
