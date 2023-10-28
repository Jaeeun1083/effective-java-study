package jaeeun.chapter4.item24.example;

public class MainRunner {
    public static void main(String[] args) {
        정적_멤버_호출_예제();
        비정적_멤버_호출_예제();
        익명_클래스_예제();
        지역_클래스_예제();
    }

    private static void 정적_멤버_호출_예제() {
        // OuterClass의 인스턴스 생성
        OuterClass outer = new OuterClass();

        // StaticInnerClass 인스턴스 생성
        OuterClass.StaticInnerClass inner = new OuterClass.StaticInnerClass();

        inner.innerMethod(); // Inner Method

        // 정적 멤버 클래스에서 외부 클래스의 private 멤버에 접근 가능
        inner.accessOuterField(outer); // Accessing outerField from inner class: 10
    }

    private static void 비정적_멤버_호출_예제() {
        // OuterClass의 인스턴스 생성
        OuterClass outerInstance = new OuterClass();

        // InnerClass 인스턴스를 OuterClass 인스턴스를 통해 생성
        OuterClass.InnerClass innerInstance = outerInstance.new InnerClass();

        // InnerClass의 innerMethod 호출
        innerInstance.innerMethod();
    }

    private static void 익명_클래스_예제() {
        // Runnable 인터페이스를 구현하는 익명 클래스
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("This is an anonymous class.");
            }
        };

        // 익명 클래스의 run 메서드 호출
        runnable.run();
    }

    private static void 지역_클래스_예제() {
        int localVar = 20;
        class LocalClass {
            public void display() {
                System.out.println("Value of localVar: " + localVar);
            }
        }

        // 지역 클래스의 인스턴스 생성 및 메서드 호출
        LocalClass local = new LocalClass();
        local.display();
    }

}
