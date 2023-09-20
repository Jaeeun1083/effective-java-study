package jaeeun.chapter3.item14.example;

import java.math.BigDecimal;

public class MainRunner {
    public static void main(String[] args) throws Exception {
        extendedExample();
        bigDecimalExample();
    }

    /**
     * Comparable 구현 클래스를 확장한 클래스에서 새로운 값 컴포넌트를 추가했을 때 예제
     * */
    public static void extendedExample() {
        Person personA = new Person("A");
        PersonWithAge personWithAgeA = new PersonWithAge(personA, 1);

        Person personB = new Person("B");
        PersonWithAge personWithAgeB = new PersonWithAge(personB, 2);

        int result = personWithAgeA.getPerson().compareTo(personWithAgeB.getPerson());
        System.out.println(result);
    }

    /**
     * compareTo와 equals가 일관되지 않는 BigDecimal 예제
     * */
    public static void bigDecimalExample() {
        BigDecimal onePointZero = new BigDecimal("1.0");
        BigDecimal onePointZeroZero = new BigDecimal("1.00");
        System.out.println(onePointZero.equals(onePointZeroZero)); // false
        System.out.println(onePointZero.compareTo(onePointZeroZero)); // 0
    }

}
