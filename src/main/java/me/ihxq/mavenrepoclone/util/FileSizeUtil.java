package me.ihxq.mavenrepoclone.util;

/**
 * @author xq.h
 * 2019/12/1 22:33
 **/
public class FileSizeUtil {
    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        //noinspection SpellCheckingInspection
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format("%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

    public static int factorialRecursion(final int number) {
        if (number == 1) return number;
        else return number * factorialRecursion(number - 1);
    }

    public static int factorialTailRecursion(final int factorial, final int number) {
        if (number == 1) return factorial;
        else return factorialTailRecursion(factorial * number, number - 1);
    }

    public static TailRecursion<Integer> factorialTailRecursion1(final int factorial, final int number) {
        if (number == 1)
            return TailInvoke.done(factorial);
        else
            return TailInvoke.call(() -> factorialTailRecursion1(factorial + number, number - 1));
    }

    public static void main(String[] args) {
        System.out.println(factorialTailRecursion1(1, 1000_000).invoke());
    }
}
