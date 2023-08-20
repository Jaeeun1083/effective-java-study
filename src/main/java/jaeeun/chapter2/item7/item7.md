## [item 7] 다 쓴 객체 참조를 해제하라

### 메모리 직접 관리
#### 자기 메모리를 직접 관리할 경우 메모리 누수를 주의해야 한다.
- 잘못된 Stack 예제
  ```
  class Stack {
    private Object[] elements;
    private int size = 0;
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    public Stack() {
      elements = new Object[DEFAULT_INITIAL_CAPACITY];
    }

    public void push(Object object) {
      ensureCapacity();
      elements[size++] = object;
    }

    public Object pop() {
      if (size == 0) {
        throw new EmptyStackException();
      }
      return elements[--size];  // 여기서 메모리 누수가 발생할 수 있다.
    }

    /**
     * 원소를 위한 공간을 적어도 하나 이상 확보한다.
     * 배열 크기를 늘려야 할 때 마다 대략 2 배씩 늘린다.
     */
    private void ensureCapacity() {
      if (elements.length == size) {
        elements = Arrays.copyOf(elements, 2 * size + 1);
      }
    }
  }
  ```
  - 이 예제의 경우 스택이 커졌다가 줄어들었을 때에도 스택이 차지하고 있는 메모리는 줄어들지 않는다. 
  - 스택의 구현체는 `pop()`을 호출하더라도 필요없는 객체에 대한 레퍼런스를 그대로 가지고있기 때문이다.

- 다음 예제는 참조를 해제하여 해결할 수 있다.
  ```
  public Object pop() {
    if (size == 0)
      throw new EmptyStackException();
    Object result = elements[--size];
    elements[size] = null; // 다 쓴 참조 해제
    return result;
  }
  ```
  - 스택에서 꺼낼 때 그 위치의 객체를 꺼내고 그 자리를 null로 설정해서 다음 GC가 발생할 때 레퍼런스가 정리되게 한다.

#### 레퍼런스를 null로 설정하는 것은 예외적인 상황인 경우여야한다.

- 다 쓴 참조를 해제하는 좋은 방법은 그 참조를 담은 변수를 유효 범위 (scope) 밖으로 밀어내는 것이다.
- 지역 변수로 사용되는 객체는 그 영역을 넘어가면 알아서 정리가 되니까 변수를 가장 최소의 스콥으로 사용하는 것이 좋다.

### 캐시

- 객체의 레퍼런스를 캐시에 넣어놓고 캐시를 비우는 것을 잊는다면 이 역시도 메모리 누수 문제를 일으킬 수 있다.
- 이는 여러가지 해결책 중 캐시의 키에 대한 레퍼런스가 캐시 밖에서 필요 없어지면 해당 엔트리를 캐시에서 자동으로 비워주는 `WeakHashMap`을 사용할 수 있다.
  ```
  Object key1 = new Object(); // hard Reference
  Object value1 = new Object();

  Map<Object, Object> cache = new WeakHashMap<>();
  cache.put(key1, value1);
  ```
  - WeakHashMap을 사용할 경우 key1의 값이 WeakReference로 감싸서 들어가게 되고 이는 HardReference의 대상이 없어지면 GC의 대상이 된다.
- 캐시를 만들 때 캐시 엔트리 유효 기간을 정확히 정의하기 어렵다면 `백그라운드 스레드`(ScheduledThreadPoolExecutor)를 활용하거나 새로운 엔트리를 추가할 때 부가적으로 기존 캐시를 비울 수 있다. (LinkedHashMap 은 removeEldestEntry 메서드를 제공한다.)

### 콜백

- 클라이언트가 콜백을 등록만 하고 명확히 해지하지 않는다면 콜백이 계속해서 쌓이기만 할 것이다. 이는 `WeakHashMap`에 키로 저장하여 콜백을 약한 참조로 저장하면 GC에 의해 정리되게 할 수 있다.

### 참고) Reference의 종류

- 자바의 Reference 타입은 `Strong References`, `Weak References` `Soft References`, `Phantom References` 로 나뉜다.
- Java GC는 객체가 가비지인지 판별하기 위해서 reachability라는 개념을 사용한다. 어떤 객체에 유효한 참조가 있으면 'reachable'로, 없으면 'unreachable'로 구별하고, unreachable 객체를 가비지로 간주해 GC를 수행한다.
- 원래 GC 대상 여부는 reachable인가 unreachable인가로만 구분하였고 이를 사용자 코드에서는 관여할 수 없었다. 그러나 java.lang.ref 패키지를 이용하여 reachable 객체들을 strongly reachable, softly reachable, weakly reachable, phantomly reachable로 더 자세히 구별하여 GC 때의 동작을 다르게 지정할 수 있게 되었다. 
- 다시 말해, GC 대상 여부를 판별하는 부분에 사용자 코드가 개입할 수 있게 되었다.

#### Strong References
- 객체를 참조하는 일반적인 인스턴스 변수

#### Weak Reference

- java.lang.ref.WeakReference 클래스는 참조 대상인 객체를 캡슐화(encapsulate)한 WeakReference 객체를 생성한다.
- weak references를 사용하면, GC가 reachability를 판단하는데 힌트를 줄수 있다.

``` 
WeakReference<Sample> wr = new WeakReference<Sample>( new Sample());  
Sample ex = wr.get();  
ex = null;
```

<img src ="https://d2.naver.com/content/images/2015/06/helloworld-329631-3.png">
  
<img src ="https://d2.naver.com/content/images/2015/06/helloworld-329631-4.png">

- 첫 번째 줄에서 생성한 WeakReference 클래스의 객체는 new() 메서드로 생성된 Sample 객체를 캡슐화한 객체이다. 
- 참조된 Sample 객체는 두 번째 줄에서 get() 메서드를 통해 다른 참조에 대입된다.
- weak reference 객체 내의 참조와 ex 참조, 두 개의 참조가 처음 생성한 Sample 객체를 가리킨다.
- weak references를 사용하면, GC가 reachability를 판단하는데 힌트를 줄수 있다.

#### Soft Reference
- 힙에 남아 있는 메모리의 크기와 해당 객체의 사용 빈도에 따라 GC 여부가 결정된다. 
- softly reachable 객체는 weakly reachable 객체와는 달리 GC가 동작할 때마다 회수되지 않으며 자주 사용될수록 더 오래 살아남게 된다.

#### Phantom Reference
- GC 대상 객체를 처리하는 작업, 즉 객체의 파이널라이즈 작업이 이루어진 후에 GC 알고리즘에 따라 할당된 메모리를 회수한다.