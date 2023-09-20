package jaeeun.chapter3.item14.example;

public class PersonWithAge {
    private Person person;
    private int age;

    public PersonWithAge(Person person, int age) {
        this.person = person;
        this.age = age;
    }

    // 클래스 인스턴스를 사용하여 기존 Comparable 구현 클래스를 반환하는 뷰 메서드
    public Person getPerson() {
        return person;
    }
}
