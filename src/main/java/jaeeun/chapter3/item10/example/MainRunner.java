package jaeeun.chapter3.item10.example;

import jaeeun.chapter3.item10.example.symmetry.CaseInsensitiveString;
import jaeeun.chapter3.item10.example.transitivity.Color;
import jaeeun.chapter3.item10.example.transitivity.IgnoreTransitivityColorPoint;
import jaeeun.chapter3.item10.example.transitivity.Point;

import java.sql.Date;
import java.sql.Timestamp;

public class MainRunner {
    public static void main(String[] args) throws Exception {
        ignoreSymmetry();
        ignoreTransitivity();
        ignoreTransitivityWithTimeStamp();
    }

    // 대칭성 예제
    private static void ignoreSymmetry() {
        CaseInsensitiveString cis = new CaseInsensitiveString("Polish");

        String s = "polish";

        System.out.println(cis.equals(s));
        System.out.println(s.equals(cis));
    }

    // 추이성 예제
    private static void ignoreTransitivity() {
        Point point = new Point(1, 2);

        // 추이성이 깨지는 예제
        IgnoreTransitivityColorPoint p1 = new IgnoreTransitivityColorPoint(1, 2, Color.RED);
        Point p2 = new Point(1, 2);
        IgnoreTransitivityColorPoint p3 = new IgnoreTransitivityColorPoint(1, 2, Color.BLUE);
        System.out.printf("%s %s %s%n",
                p1.equals(p2), p2.equals(p3), p1.equals(p3));

    }

    private static void ignoreTransitivityWithTimeStamp() {
        long time = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(time);
        Date date = new Date(time);

        // 대칭성 위배
        System.out.println(date.equals(timestamp)); // true
        System.out.println(timestamp.equals(date)); // false
    }

}
