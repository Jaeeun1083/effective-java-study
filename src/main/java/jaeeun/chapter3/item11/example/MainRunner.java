package jaeeun.chapter3.item11.example;

import java.util.HashMap;
import java.util.Map;

public class MainRunner {
    public static void main(String[] args) throws Exception {
        Map<PhoneNumber, String> map =new HashMap<>();

        PhoneNumber number1 = new PhoneNumber(123, 456, 7890);
        PhoneNumber number2 = new PhoneNumber(123, 456, 7890);

        System.out.println("number1.equals(number2) : " + number1.equals(number2));   // true
        System.out.println("number1 hashCode : " + number1.hashCode());        // 258952499
        System.out.println("number2 hashCode : " + number2.hashCode());        // 603742814

        map.put(number1, "phone1");
        map.put(number2, "phone2");

        String s = map.get(number2);
        System.out.println("map.get(number2) : " + s);                          // phone2

        String s2 = map.get(new PhoneNumber(123, 456, 7890));
        System.out.println("map.get(new PhoneNumber (...)) : " + s2);
    }

}
