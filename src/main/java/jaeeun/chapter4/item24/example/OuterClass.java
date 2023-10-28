package jaeeun.chapter4.item24.example;

public class OuterClass {
    private int outerField = 10;

    public void outerMethod() {
        System.out.println("Outer Method");
    }

    /* 정적 멤버 클래스 */
    public static class StaticInnerClass {
        public void innerMethod() {
            System.out.println("StaticInnerClass Inner Method");
        }

        public void accessOuterField(OuterClass outer) {
            System.out.println("Accessing outerField from inner class: " + outer.outerField);
        }
    }

    /* 비정적 멤버 클래스 */
    public class InnerClass {
        public void innerMethod() {
            System.out.println("InnerClass Inner Method");

            // 비정적 멤버 클래스에서 바깥 클래스의 메서드 호출
            outerMethod();

            // 정규화된 this를 통한 바깥 클래스 참조
            OuterClass.this.outerMethod();
        }

        // 바깥 클래스의 필드에 접근
        public void accessOuterField() {
            System.out.println("Accessing outerField from inner class: " + OuterClass.this.outerField);
        }
    }
}
