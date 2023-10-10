## [item 18] 상속보다는 컴포지션을 사용하라.

- 상속은 상위 클래스 구현 방식에 따라 하위 클래스의 동작에 이상이 생길 수 있어 **캡슐화를 깨뜨린다**.
- 상속을 잘못 사용한 예시로 HashSet에 추가된 원소의 수를 저장하는 변수와 접근자 메서드를 추가한 예시를 보자.

```java
public class InstrumentedHashSet<E> extends HashSet<E> {
    // 추가된 원소의 수
    private int addCount = 0;

    public InstrumentedHashSet() {}

    public InstrumentedHashSet(int initCap, float loadFactor) {
        super(initCap, loadFactor);
    }

    @Override
    public boolean add(E e) {
        addCount++;
        return super.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        addCount += c.size();
        return super.addAll(c);
    }

    public int getAddCount() {
        return addCount;
    }
}
```

- 이 클래스는 잘 구현된 것처럼 보이지만 제대로 작동하지 않는다. 이 클래스의 인스턴스에 addAll 메서드로 원소 3개를 더하고 addCount를 확인해보자.

### 상속 후 메서드 재정의를 하여 발생하는 문제

```java
public class MainRunner {
    public static void main(String[] args) {
        InstrumentedHashSet<String> s = new InstrumentedHashSet<>();
        s.addAll(List.of("틱", "탁탁", "펑"));
        System.out.println("addCount : " + s.getAddCount());
    }
    // 결과
    // addCount : 6
}
```

  - getAddCount 메서드를 호출하면 3을 반환하리라 기대하겠지만, 실제로는 6을 반환한다.
    - 그 원인은 HashSet의 addAll 메서드가 add 메서드를 사용해 구현된 데 있다. 이런 내부 구현 방식은 HahSet 문서에는 쓰여 있지 않다. 
    - 이처럼 자신의 다른 부분을 사용하는 `자기사용(self-use)` 여부는 해당 클래스의 내부 구현 방식에 해당하며 다음 릴리즈에도 유지될 지는 알 수 없다.
  - addAll 메서드를 주어진 컬렉션을 순회하며 원소 하나당 add 메서드를 한 번만 호출하도록 하여 이 문제를 해결할 수 있지만 이는 문제가 있다.
    - 상위 클래스의 메서드 동작을 다시 구현하는 방식은 어렵고 시간도 더 들고, 자칫 오류를 내거나 성능을 떨어뜨릴 수도 있다. 또한 하위 클래스에서 접근할 수 없는 private 필드를 써야하는 상황이라면 이 방식이 불가능하다.

### 상속의 취약점을 피하기 위한 컴포지션

- 새로운 클래스(`래퍼 클래스`)를 만들고 private 필드로 기존 클래스의 인스턴스를 참조하게 하는 방식이다. 이는 기능을 덧씌운다는 뜻에서 데코레이터 패턴이라고 한다.
- 새 클래스의 인스턴스 메서드들은 기존 클래스에 대응하는 메서드를 호출 해 그 결과를 반환하는 `전달(forwarding) 방식`을 사용하며, 새 클래스의 메서드들을 `전달 메서드 (forwarding method)`라 부른다.
- 컴포지션과 전달의 조합은 넓은 의미로 `위임(delegation)`이라고 부른다.

```java
public class InstrumentedSet <E> extends ForwardingSet<E> {
    private int addCount = 0;

    @Override
    public boolean add(E element) {
        addCount++;
        return super.add(element);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        addCount += collection.size();
        return super.addAll(collection);
    }

    public int getAddCount() {
        return addCount;
    }
}

/*
 * 재사용할 수 있는 전달 클래스
 * */
public class ForwardingSet<E> implements Set<E> {
    //... 생략
}
```
- InstrumentedSet은 HashSet의 모든 기능을 정의한 Set 인터페이스를 활용해 설계되어 유연하다.
  - 구체적으로 extends 한 FordingSet 클래스에서는 Set 인터페이스를 구현했고, Set의 인스턴스를 받는 생성자를 하나 제공한다.
  - 임의의 Set에 계측 기능을 덧씌워 새로운 Set으로 만드는 것이 이 클래스의 핵심이다.

### 래퍼 클래스가 조심해야할 콜백 프레임워크
- 콜백 프레임워크에서는 자기 자신의 참조를 다른 객체에 넘겨서 다음 호출(콜백) 때 사용하도록 한다.
- 내부 객체는 자신을 감싸고 있는 래퍼의 존재를 모르니 대신 자신(this)의 참조를 넘기고, 콜백 때는 래퍼가 아닌 내부 객체를 호출하게 되는 SELF answprk qkftodgksek.

// TODO 예제와 콜백 프레임워크 찾아보기.

