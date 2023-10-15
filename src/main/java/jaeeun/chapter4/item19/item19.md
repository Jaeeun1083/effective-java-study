## [item 19] 상속을 고려해 설계하고 문서화하라. 그러지 않았다면 상속을 금지하라.

## 상속용 클래스의 내부 메커니즘 문서화
- 상속용 클래스는 재정의할 수 있는 메서드들을 내부적으로 어떻게 이용하는지 문서로 남겨야 한다.
  - 클래스의 API로 공개된 메서드에서 클래스 자신의 또 다른 메서드를 호출할 때 호출되는 메서드가 재정의 가능 메서드라면 그 사실을 호출하는 메서드의 API 설명에 적시해야 한다.
  - 어떤 순서로 호출하는지, 각각의 호출 결과가 이어지는 처리에 어떤 영향을 주는지도 담아야 한다.

### 메서드 주석에 @impleSpec 태그를 붙여 내부 동작 방식을 설명할 수 있다.

- API 문서의 메서드 설명에 "Implementation Requirements"로 시작하는 절은 그 메서드의 내부 동작 방식을 설명하는 곳이다.
- 이는 메서드 주석에 자바 8부터 도입된 `@impleSpec` 태그를 붙여 자바독 도구가 생성해준다.

#### AbstractCollection의 impleSpec 예시

```java
public abstract class AbstractCollection<E> implements Collection<E> {
    /** @implSpec
     * ...
     * 이 컬렉션의 itrerator 메서드가 반환한 반복자가 remove 메서드를 구현하지 않았다면
     * UnsupportedOperationException을 던지니 주의하자
     * */
    public boolean remove(Object o) {
        ...
    }
}
```

## 상속 허용 클래스가 지켜야 할 제약

### 1. 훅(hook)을 선별하여 protected 메서드 형태로 공개해야 할 수도 있다.

- 효율적인 하위 클래스를 큰 어려움 없이 만들 수 있게 하려면 클래스의 내부 동작 과정 중간에 끼어들 수 있는 훅(hook)을 잘 선별하여 protected 메서드 형태로 공개해야 할 수도 있다.
- 상속용 클래스를 설계할 때 어떤 메서드를 protected로 노출해야 할지는 실제 하위 클래스를 만들어 시험해보는 것이 최선이다.
  - protected 메서드 하나하나가 내부 구현에 해당하므로 그 수는 가능한 적어야 한다.

#### AbstractList의 removeRange 메서드 예시

```java
public abstract class AbstractList<E> extends AbstractCollection<E> implements List<E> {

    /*
     * 리스트 구현의 내부 구조를 활용하도록 removeRange 메서드를 재정의 하면
     * clear 연산 성능을 크게 개선할 수 있다.
     * */
    public void clear() {
      removeRange(0, size());
    }
    
    protected void removeRange(int fromIndex, int toIndex) {
        /*
         * 이 호출로 리스트는 toIndex - fromIndex 만큼 짧아진다.
         * 원소 제거 시 ListIterator의 next와 remove를 반복 호출하도록 구현되었다.
         * ListIterator.remove가 선형 시간이 걸리면 이 구현의 성능은 제곱에 비례한다/
         * */
    }
}
```
- 이 예시에서 removeRange 메서드를 protected로 제공한 이유는 하위 클래스에서 clear 메서드의 성능을 높이기 위해서 이다.

### 2. 상속용 클래스의 생성자는 재정의 가능 메서드를 호출해서는 안 된다.

- 상위 클래스의 생성자가 하위 클래스의 생성자보다 먼저 실행되므로 **하위 클래스에서 재정의한 메서드가 하위 클래스의 생성자보다 먼저 호출**된다.
- 이 때 그 재정의한 메서드가 하위 클래스의 생성자에서 초기화하는 값에 의존한다면 의도대로 동작하지 않을 것이다.

```java
/*
 * 하위 클래스(Sub)의 생성자가 인스턴스 필드를 초기화 하기 전에 상위 클래스(Super)의 생성자가 overridMe() 를 호출하게 된다.
 * */
public class Super {
    public Super() {overrideMe();} // 2

    public void overrideMe() {}
}

public class Sub extends Super {
  private final Instant instant;

  public Sub() {this.instant = Instant.now();} // 4

  @Override
  public void overrideMe() {System.out.println(instant);} // 3, 6

  public static void main(String[] args) {
    Sub sub = new Sub(); // 1
    sub.overrideMe(); // 5
  }
}
```

### 3. clone과 readObject는 재정의 가능 메서드를 호출해서는 안된다.

- clone과 readObject 메서드는 새로운 객체를 만든다는 점에서 생성자와 비슷한 효과를 낸다. 따라서 상속용 클래스에서 Cloneable이나 Serializable을 구현할지 정해야 한다면 재정의 가능 메서드를 호출해서는 안된다.
  - clone의 경우 하위 클래스의 clone 메서드가 복제본 상태를 수정하기 전에 재정의한 메서드를 호출한다.
  - readObject의 경우 하위 클래스의 상태가 역직렬화가 마무리 되기 전에 재정의한 메서드부터 호출하게 된다.

### 상속용으로 설계하지 않은 클래스는 상속을 금지하자

- 전통적으로 일반적인 구체 클래스는 final도 아니고 상속용으로 설계되거나 문서화되지도 않았다.
- 이 경우 클래스의 변화가 생길 때마다 하위 클래스를 오동작하게 할 수 있다.
- 이 문제를 해결하는 가장 좋은 방법은 **상속용으로 설계하지 않은 클래스는 상속을 금지하는 것**이다.

> **구체 클래스**란 ?
> 
> 추상 클래스나 인터페이스와 달리 구체적으로 인스턴스화 할 수 있는 클래스를 뜻한다.

#### 구체 클래스에서 상속을 금지하는 두가지 방법

1. 클래스를 **final로 선언**하는 방법
2. **생성자의 외부 접근을 막고 public 정적 팩터리**를 만드는 방법
   - 이 방법은 내부에서 다양한 하위 클래스를 만들어 쓸 수 있는 유연성을 준다.

### 구체 클래스에서 상속을 허용하려면?

- 구체 클래스가 표준 인터페이스를 구현하지 않았는데 상속을 금지하면 사용하기 불편해진다.
  - 유연성 제한 : 해당 클래스의 기능을 확장하거나 변경하기가 어려워진다.
  - 코드 중복 : 비슷한 기능을 가진 클래스 간 코드 중복이 발생할 수 있다.
- 이러한 문제로 인해 상속을 허용하려면 클래스 내부에서는 재정의 가능 메서드를 사용하지 않게 만들고 이 사실을 문서로 남기는 방법이 있다.
