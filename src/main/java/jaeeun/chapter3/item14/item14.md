## [item 14] Comparable을 구현할지 고려하라.

```java
public interface Comparable<T> {
    public int compareTo(T o);
}
```

- compareTo는 동치성 비교 및 순서를 비교할 수 있으며 제네릭 타입을 갖고 있어 컴파일 타임에 체크가 가능하다.
- Comparable을 구현했다는 것은 그 클래스의 인스턴스들에는 순서가 있음을 뜻한다.

### compareTo 일반 규약

이 객체가 주어진 객체보다 **작으면 음수 (-sgn)** 를, **같으면 0**을, **크면 양수(sgn)** 로 반환한다.

[//]: # (이 객체와 비교할 수 없는 타입의 객체가 주어지면 ClassCastException을 던진다.)

#### Long 클래스 예시

```java
public final class Long extends Number
        implements Comparable<Long>, Constable, ConstantDesc {

    private final long value;
    
    ...
    
    public int compareTo(Long anotherLong) {
        return compare(this.value, anotherLong.value);
    }
    
    public static int compare(long x, long y) {
        return (x < y) ? -1 : ((x == y) ? 0 : 1);
    }        
}
```

#### 대칭성
`sgn(x.compartTo(y)) == -sgn.(y.compareTo(x))`
- 두 객체 참조의 순서를 바꿔 비교해도 예상한 결과가 나와야 한다.

#### 추이성
`(x.compareTo(y) > 0 && y.compareTo(z) > 0)이면 x.compareTo(z) > 0`
- 첫 번째가 두 번째보다 크고 두 번째가 세 번째보다 크면 첫 번째는 세 번째보다 커야 한다.

#### 반사성
`x.compareTo(y) == 0이면 sgn(x.compareTo(z)) == sgn(y.compareTo(z))`
- 크기가 같은 객체들끼리는 어던 객체와 비교하더라도 항상 같아야 한다.

#### 동치성 결과시 equals 규약 충족 (필수 X)
`(x.compareTo(y) == 0) == (x.equals(y))` 는 필수는 아니지만 지키는 것이 좋다.
- compareTo 메서드로 수행한 동치성 테스트의 결과가 equals와 같아야 한다. (필수는 아니지만 지키는게 좋다.)

### Comparable을 구현한 클래스를 확장해 값 컴포넌트를 추가하고 싶을 경우

```java
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
```

- 확장하는 대신 독립된 클래스를 만들고 이 클래스에 원래 클래스의 인스턴스를 가리키는 필드를 둔다.
- 그 다음 내부 인스턴스를 반환하는 뷰 메서드를 제공하면 된다.

### compareTo와 equals가 일관되지 않는 BigDecimal 클래스

- compareTo 메서드로 수행한 동치성 테스트 결과와 equals의 결과가 일관되지 않는 클래스 중 하나는 BigDecimal 클래스가 있다.
- BigDecimal의 compareTo 메서드는 객체의 수치값을 비교하지만 equals 메서드는 객체 내부 구조(숫자 비교가 아닌 객체 자체의 동등성)를 비교하기 때문이다.

```java
public class MainRunner {
    public static void bigDecimalExample() {
        BigDecimal onePointZero = new BigDecimal("1.0");
        BigDecimal onePointZeroZero = new BigDecimal("1.00");
        System.out.println(onePointZero.equals(onePointZeroZero)); // false
        System.out.println(onePointZero.compareTo(onePointZeroZero)); // 0
    }
}
```

### compareTo 메서드 작성 요령

- Comparable은 타입을 인수로 받는 제네릭 인터페이스라서 compareTo 메서드의 인수 타입은 컴파일 타임에 정해진다.
  - 입력 인수의 타입을 확인 & 형변환 할 필요가 없다는 의미이다.
- compareTo 메서드는 각 필드의 동치관계를 보는게 아니라 그 순서를 비교한다.
- 객체 참조 필드를 비교하려면 compareTo 메서드를 재귀적으로 호출한다.
- Comparable을 구현하지 않은 필드나 표준이 아닌 순서로 비교해야 할 경우 Comparator를 쓰면 된다.
- 클래스의 핵심 필드가 여러 개라면 핵심 필드부터 비교해 나가자
  ```java
  public class PhoneNumber implements Comparable<PhoneNumber> {
    private short areaCode, prefix, lineNum;
  
    @Override
    public int compareTo(PhoneNumber pn) {
        int result = Short.compare(areaCode, pn.areaCode);      // 가장 중요한 필드
        if (result == 0)  {
            result = Short.compare(prefix, pn.prefix);          // 두 번째로 중요한 필드
            if (result == 0)
                result = Short.compare(lineNum, pn.lineNum);    // 세 번째로 중요한 필드
        }
        return result;
    }
  }
  ```
  
### 비교자 생성 메서드

- 자바 8부터는 메서드 연쇄 방식으로 비교자를 생성할 수 있다. 간결하지만 약간의 성능 저하가 있다.

```java
public class PhoneNumber implements Comparable<PhoneNumber> {
    private short areaCode, prefix, lineNum;
    private static final Comparator<PhoneNumber> COMPARATOR =
            comparingInt((PhoneNumber pn) -> pn.areaCode)
                    .thenComparingInt(pn -> pn.prefix)
                    .thenComparingInt(pn -> pn.lineNum);

    @Override
    public int compareTo(PhoneNumber pn) {
        return COMPARATOR.compare(this, pn);
    }
}
```
  - Comparator는 보조 생성 메서드가 있다. 즉  int, long, double 로 값을 매핑하여 객체를 비교할 수 있다.
    - `Comparator.comparingInt(ToIntFunction keyExtractor)`
    - `Comparator.comparingLong(ToLongFunction keyExtractor)`
    - `Comparator.comparingDouble(ToDoubleFunction keyExtractor)`
