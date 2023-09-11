## [item 13] clone 재정의는 주의해서 진행하라.

```
public interface Cloneable {}

public class Object {
    ...
    @IntrinsicCandidate
    protected native Object clone() throws CloneNotSupportedException;
}
```

- 메소드가 없는 Cloneable 인터페이스는 Object의 protected 메서드인 clone의 동작 방식을 결정한다.
- Cloneable을 구현한 클래스의 인스턴스에서 clone을 호출하면 그 객체의 필드들을 하나하나 복사한 객체를 반환하며, 그렇지 않은 클래스의 인스턴스에서 호출하면 CloneNotSupportedException을 던진다.

### 클래스가 가변 객체를 참조할 때 Clone

가변 객체를 참조한 Stack 클래스가 clone 메서드를 구현할 때 단순히 `super.clone`의 결과를 그대로 반환한다면 어떤 일이 발생할까?

```
public class Stack {
    private Object[] elements;
    private int size = 0;
    ...
}
```

- 반환된 Stack 인스턴스의 size 필드는 올바른 값을 갖지만 **elements 필드는 원본 Stack 인스턴스와 똑같은 배열을 참조**할 것이다. 즉 원본이나 복제본 중 하나를 수정하면 다른 하나도 수정된다.
- clone은 원복 객체에 아무런 해를 끼치지 않는 **동시에 복제된 객체의 불변식을 보장**해야 한다. 

### 복제된 객체의 불변식을 보장하는 방법

#### 재귀적 호출
- clone 메서드가 제대로 동작하려면 스택 내부 정보를 복사해야 하는데 그 방법 중 하나는 elements 배열의 clone을 재귀적으로 호출해주는 것이다.
- 따라서 배열을 복제할 때는 **배열의 clone 메서드를 사용**하라고 권장한다.
- Stack 예제
  ```
  @Override
  public Stack clone() {
      try {
          Stack result = (Stack) super.clone();
          result.elements = elements.clone();
          return result;
      } catch (CloneNotSupportedException e) {
          throw new AssertionError();
      }
  }
  ```

#### 연결리스트 복사
- 해시테이블과 같이 객체(버킷)를 배열로 갖는 경우 **각 객체(버킷)을 구성하는 연결리스트를 복사**해야한다.
- 해시테이블 예제
  ```
  public class HashTable implements Cloneable {
      private Entry[] buckets = ...;

      private static class Entry {
          final Object key;
          Object value;
          Entry next;

          Entry(Object key, Object value, Entry next) {
              this.key = key;
              this.value = value;
              this.next = next;
          }
      }

      ...

      /*
        적절한 크기의 새로운 버킷 배열을 할당한 다음 원래의 버킷 배열을 순회하며 
        비지 않은 각 버킷에 대해 깊은 복사를 수행한다.
      */
      Entry deepCopy() {
          return new Entry(key, value, next == null ? null : next.deepCopy());
      }

      @Override
      public HashTable clone() {
          try {
              HashTable result = (HashTable) super.clone();
              result.buckets = new Entry[buckets.length];
              for (int i = 0; i < buckets.length; i++) {
                  if (buckets[i] != null) result.buckets[i] = buckets[i].deepCopy();
              }
              return result;
          } catch (CloneNotSupportedException e) {
              throw new AssertionError();
          }
      }

  }
  ```
  - 해시테이블의 `clone` 메서드는 먼저 적절한 크기의 새로운 버킷 배열을 할당한 다음 원래의 버킷 배열을 순회하며 비지 않은 각 버킷에 대해 깊은 복사를 수행한다. 이는 리스트의 원소 수만큼 스택 프레임을 소비하므로 리스트가 길면 스택 오버플로를 일으킬 위험이 있다.
  - 이 문제를 피하려면 deepCopy를 재귀 호출 대신 **반복자를 써서 순회**하도록 수정할 수 있다.
- 변경된 해시테이블 deepCopy 메서드
    ```
    Entry deepCopy() {
        Entry result = new Entry(key, value, next);
        for (Entry p = result; p.next != null; p = p.next) {
            p.next = new Entry(p.next.key, p.next.value, p.next.next);
        }
        return result;
    }
    ```

### Clonable 대신 복사 팩터리와 복사 메서드를 사용하자.

- 객체를 복사할 때 특별한 경우(이미 Cloneable로 구현한 클래스로 사용하고 있을 때)를 제외하고 복사 생성자와 복사 팩토리를 사용하자
- 복사 생성자 (=변환 생성자) : 자신과 같은 클래스의 인스턴스를 인수로 받는 생성자
  - ```public X(X x) { ... };```
- 복사 팩터리 (=변환 팩터리) : 복사 생성자를 모방한 정적 팩터리
  - ```public static X newInstance(X x) { ... };```