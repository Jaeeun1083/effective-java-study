package jaeeun.chapter4.item19.example;

public class Super {
    public Super() {
        helpMethod(); // 도우미 메서드를 생성자에서 호출
    }

    // private 도우미 메서드로 이동하여 초기화를 수행
    private void helpMethod() {
        overrideMe();
    }

    public void overrideMe() {
    }
}
