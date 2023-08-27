## [item 8] finalizer와 cleaner 사용을 피하라.

- 자바는 `finalizer`와 `Cleaner` 라는 두가지 객체 소멸자를 제공하지만 둘다 **예측이 불가능하고 위험하며 대부분 불필요하다.**
- finalizer는 자바 9 이후로 deprecated 
### 왜 사용을 피해야할까?

#### 1. 언제 실행될 지 알 수 없다.
- 어떤 객체가 더 이상 필요 없어진 시점에 finalizer와 cleaner가 즉시 수행된다는 보장이 없어 **타이밍이 중요한 작업은 절대 할수 없다.**
#### 2. 인스턴스 반납을 지연 시킬 수도 있다.
- finalizer 쓰레드는 우선 순위가 낮아서 언제 실행될 지 모르므로 이 작업이 계속해서 대기 상태에 남아 있다면 해당 인스턴스는 GC가 되지 않고 계속 쌓이다가 OutOfMemoryException이 발생할 수도 있다.
#### 3. 수행 여부 또한 보장되지 않는다.
- 자바 언어 명세는  finalizer와 cleaner의 수행 여부를 보장하지 않는다. 따라서 프로그램 생애 주기와 관련 없는 상태를 영구적으로 수정하는 일(데이터베이스 같은 자원의 락을 반환하는 작업 같은 일)을 하면 안된다. 
- System.gc나 System.runFinalization 메서드는 finalizer와 cleaner가 실행될 가능성을 높여줄 수는 있으나 보장해주지는 않는다.
#### 4. 동작 중 발생한 예외 처리가 안된다.
- finalizer 동작 중 발생한 예외는 무시되며, 처리할 작업이 남았더라도 종료되기 때문에 잡지 못한 예외로 인해 해당 객체는 마무리가 덜 된 상태로 남을 수 있다.
- cleaner 사용 라이브러리는 자신의 스레드를 통제하기 때문에 이런 문제가 발생하지는 않는다.
#### 5. 심각한 성능 문제를 동반한다.
- AutoCloseable 객체를 생성하고 GC가 수거하는 작업과 finalizer와 cleaner를 사용한 객체를 생성하고 파괴하는 작업을 비교했을 때 성능이 현저히 떨어짐을 확인할 수 있다.

### 언제 사용하는 걸까?
- 네이티브 피어와 연결된 객체이다. (네이티브 피어는 네이티브 메서드를 통해 기능을 위임한 네이티브 객체로 자바 객체가 아니니 GC는 그 존재를 알지 못한다. 즉 GC의 대상이 되지 못한다.)
  - 자원의 소유자가 close 메서드를 호출하지 않는 것에 대비한 안전망 역할이다.
    - finalize 안전망 예제
      ```
      public class SampleResource implements AutoCloseable {
        private boolean closed;

        @Override
        public void close() {
          if (this.closed) {
            throw new IllegalStateException();
          }
          closed = true;
          System.out.println("close");
        }
  
        @Override
        protected void finalize() throws Throwable {
          if (!this.closed) close();
        }
      }
      ```
    - cleaner 안전망 예제
      ```
      public class SampleResource implements AutoCloseable {
        private boolean closed;
    
        private static final Cleaner CLEANER = Cleaner.create(); // Cleaner 객체 생성
        private final Cleaner.Cleanable cleanable; // Cleaner clean 할 때는 cleanable을 사용한다.
        private final ResourceCleaner resourceCleaner;
    
        public SampleResource() {
          this.resourceCleaner = new ResourceCleaner();
          this.cleanable = CLEANER.register(this, resourceCleaner); // Cleaner에 클린 작업을 할 객체와 그 작업 스레드를 등록한다.
        }

        private static class ResourceCleaner implements Runnable { // 클린 작업을 할 별도의 스레드
          @Override
          public void run() {
            System.out.println("Clean");
          }
        }
      
        @Override
        public void close() {
          if (this.closed) {
            throw new IllegalStateException();
          }
          closed = true;
          cleanable.clean();
        }
      }
      ```

### 자원을 반납하는 방법
- 자원 반납이 필요한 클래스는 `AutoCloseable` 인터페이스를 구현하고 
- `try-with-resource`를 사용하거나 클라이언트가 인스턴스를 다 쓰면 `close` 메소드를 명시적으로 호출하면 된다.
  ```
  public class SampleResource implements AutoCloseable {
    public void hello() {
        System.out.println("hello");
    }

    @Override
    public void close() {
        System.out.println("close");
    }
  }
  
  
  ## 클라이언트가 close 메소드를 명시적으로 호출
  public static void main(String[] args) {
    SampleResource resource = new SampleResource();
    resource.hello();
    resource.close();
  }
  
  ## try-with-resource 사용
  public static void main(String[] args) {
    try (SampleResource resource = new SampleResource()) {
      resource.hello();
    }
  }
  ```

[//]: # (### Cleaner의 동작 원리)

[//]: # ()
[//]: # (- cleaner 사용법을 다시 상기 해보자.)

[//]: # (  1. `Cleaner 객체` 생성 및 초기화.)

[//]: # (  2. Cleaner에 클린 작업 할 객체와 작업 스레드 &#40;ResourceCleaner&#41;를 등록.)

[//]: # (  3. Cleanable 의 clean&#40;&#41; 호출)

[//]: # ()
[//]: # (#### 1. Cleaner 객체 생성 및 초기화)

[//]: # (- Cleaner.create&#40;&#41;; 를 호출하면 어떤 작업이 이루어질까?)

[//]: # ()
[//]: # (```)

[//]: # (public final class Cleaner {)

[//]: # (  final CleanerImpl impl;)

[//]: # (  ...)

[//]: # (  public static Cleaner create&#40;&#41; {)

[//]: # (    Cleaner cleaner = new Cleaner&#40;&#41;;)

[//]: # (    cleaner.impl.start&#40;cleaner, null&#41;;              // 1)

[//]: # (    return cleaner;)

[//]: # (   })

[//]: # (})

[//]: # ()
[//]: # (public final class CleanerImpl implements Runnable {)

[//]: # (  ...)

[//]: # (  public void start&#40;Cleaner cleaner, ThreadFactory threadFactory&#41; {)

[//]: # (    if &#40;getCleanerImpl&#40;cleaner&#41; != this&#41; {)

[//]: # (      throw new AssertionError&#40;"wrong cleaner"&#41;;)

[//]: # (    })

[//]: # ()
[//]: # (    new CleanerCleanable&#40;cleaner&#41;;                  // 2)

[//]: # ()
[//]: # (    if &#40;threadFactory == null&#41; {                    // 3)

[//]: # (      threadFactory = CleanerImpl.InnocuousThreadFactory.factory&#40;&#41;;)

[//]: # (    })

[//]: # ()
[//]: # (    Thread thread = threadFactory.newThread&#40;this&#41;;  // 4)

[//]: # (    thread.setDaemon&#40;true&#41;;)

[//]: # (    thread.start&#40;&#41;;)

[//]: # (  })

[//]: # (  ...)

[//]: # (  static final class CleanerCleanable extends PhantomCleanable<Cleaner> {)

[//]: # (    CleanerCleanable&#40;Cleaner cleaner&#41; {)

[//]: # (      super&#40;cleaner, cleaner&#41;;)

[//]: # (    })

[//]: # ()
[//]: # (    @Override)

[//]: # (    protected void performCleanup&#40;&#41; {)

[//]: # (      // no action)

[//]: # (    })

[//]: # (  })

[//]: # (})

[//]: # ()
[//]: # (```)

[//]: # (1. Cleaner는 자기 자신과 threadFactory 값은 null을 인자로 CleannerImpl의 start를 호출한다.)

[//]: # (2. CleanerCleanable 객체를 생성한다. 이 객체는 cleaner에 대한 작업을 나타내며 cleaner 객체가 수명을 유지할 수 있도록 한다. )

[//]: # (3. threadFactory는 스레드 생성을 관리하는 팩토리 메서드이며 이를 사용하여 스레드의 동작을 조정할 수 있다.)

[//]: # ()
[//]: # ()
[//]: # (#### Cleaner에 클린 작업 할 객체와 작업 스레드 &#40;ResourceCleaner&#41;를 등록)

[//]: # (- CLEANER.register&#40;this, resourceCleaner&#41;;)

[//]: # (```)

[//]: # (public final class Cleaner {)

[//]: # (  ...)

[//]: # (  public Cleanable register&#40;Object obj, Runnable action&#41; {)

[//]: # (    Objects.requireNonNull&#40;obj, "obj"&#41;;)

[//]: # (    Objects.requireNonNull&#40;action, "action"&#41;;)

[//]: # (    return new CleanerImpl.PhantomCleanableRef&#40;obj, this, action&#41;;)

[//]: # (  })

[//]: # (})

[//]: # ()
[//]: # (public final class CleanerImpl implements Runnable {)

[//]: # (  ...)

[//]: # (  public static final class PhantomCleanableRef extends PhantomCleanable<Object> {)

[//]: # (    private final Runnable action;)

[//]: # (    ...)

[//]: # (    public PhantomCleanableRef&#40;Object obj, Cleaner cleaner, Runnable action&#41; {)

[//]: # (      super&#40;obj, cleaner&#41;;)

[//]: # (      this.action = action;)

[//]: # (     })

[//]: # (  })

[//]: # (})

[//]: # (```)

[//]: # (#### Cleanable 의 clean&#40;&#41; 호출)