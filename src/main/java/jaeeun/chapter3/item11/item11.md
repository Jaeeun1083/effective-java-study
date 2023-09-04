## [item 11] equals를 재정의하려거든 hashCode도 재정의하라.

#### hash란?
- 해시 함수(hash function) 또는 해시 알고리즘(hash algorithm) 또는 해시함수알고리즘(hash函數algorithm)은 임의의 길이의 데이터를 고정된 길이의 데이터로 매핑하는 함수이다. 해시 함수에 의해 얻어지는 값은 해시 값, 해시 코드, 해시 체크섬 또는 간단하게 해시라고 한다. - 위키백과
- 책을 읽고 정리하기 전 hash에 대해 정리했던 글을 다시 읽어보고 진행했다.
  - [hashMap 정리]('https://github.com/Jaeeun1083/java-deep-study/blob/main/java/2주차/HashMap.md')

### Object 명세의 hashCode 규약

1. 애플리케이션이 실행되는 동안 equals 비교에 사용되는 정보가 변경되지 않았다면 hashCode는 항상 같은 값을 반환해야 한다. 
2. equals(Object)가 두 객체를 같다고 판단했다면 두 객체의 hashCode는 똑같은 값을 반환해야 한다. 
3. equals(Object)가 두 객체를 다르다고 판단했더라도 두 객체의 hashCode 가 서로 다른 값을 반환할 필요는 없다. 단 다른 객체에 대해서는 다른 값을 반환해야 해시 테이블의 성능이 좋아진다.

### 2번 규약을 어기는 예제

```
#### equals를 재정의 한 클래스

public class PhoneNumber {
    private final int areaCode;
    private final int prefix;
    private final int lineNum;

    public PhoneNumber(int areaCode, int prefix, int lineNum) {
        this.areaCode = areaCode;
        this.prefix = prefix;
        this.lineNum = lineNum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PhoneNumber that = (PhoneNumber) o;
        return areaCode == that.areaCode && prefix == that.prefix && lineNum == that.lineNum;
    }

}


public static void main(String[] args) throws Exception {
    Map<PhoneNumber, String> map =new HashMap<>();
    
    PhoneNumber number1 = new PhoneNumber(123, 456, 7890);
    PhoneNumber number2 = new PhoneNumber(123, 456, 7890);

    System.out.println(number1.equals(number2));   // true
    System.out.println(number1.hashCode());        // 258952499
    System.out.println(number2.hashCode());        // 603742814
    map.put(number1, "phone1");
    map.put(number2, "phone2");

    String s = map.get(number2);
    System.out.println(s);                          // phone2

    String s2 = map.get(new PhoneNumber(123, 456, 7890)); // 제대로 동작하지 않는다.
    System.out.println(s2);                               // null
}
```
- 해당 예재에서 PhoneNumber 클래스는 equals(Object)를 재정의 했기 때문에 물리적으로 다른 두 객체를 논리적으로는 같다고 할 수 있다.
- 그러나 Object의 기본 hashCode 메서드는 이 둘이 다르다고 판단하여 서로 다른 해시 코드 값을 반환하여 두 번째 규약을 지키지 못한다.
- HashMap은 객체의 해시 값을 통해 버킷에 객체를 저장 및 조회하기 때문이다.
  - ```
    #### hashMap의 put 메서드
    public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
    ```

### 최악의 hashCode 구현
```
public class PhoneNumber {
    ...
    
    @Override
    public int hashCode() {
        return 42;
    }
}
```
- 이 코드는 동치인 모든 객체에 같은 해시코드를 반환하니 정상 동작을 하긴하지만 모든 객체에게 똑같은 값을 반환하므로 **모든 객체가 해시테이블의 버킷 하나에 담겨 연결리스트처럼 동작**한다 
- 그 결과 평균 수행 시간이 O(1)인 해시테이블이 O(n)으로 느려진다.

### hashCode를 작성하는 방법

```
  @Override
  public int hashCode() {
    int result = Integer.hashCode(areaCode); // 1
    result = 31 * result + Integer.hashCode(prefix); // 2
    result = 31 * result + Integer.hashCode(lineNum); // 2
    return result;
  }
```
1. int 변수 result를 선언한 후 값 c (해당 객체의 첫번째 핵심 필드를 단계 2,a 방식으로 계산한 해시코드)로 초기화한다.
2. 해당 객체의 나머지 핵심 필드 f 각각에 대해 다음 작업을 수행한다.
   1. 해당 필드의 해시코드 c를 계산한다.
      1. 기본 타입 필드라면 Type.hashCode(f)를 수행한다. (Type = 해당 기본 타입의 박싱 클래스)
      2. 참조 타입 필드라면 이 필드의 표준형을 만들어 그 표준형의 hashCode를 호출한다. 필드의 값이 Null이면 0을 사용한다.
      3. 필드가 배열이라면 원소 각각을 별도 필드처럼 다룬다.
   2. 단계 `2.i` 에서 계산한 해시코드 c로 result를 갱신한다 (`result = 31 * result + c`)
3. result를 반환한다.

**파생 필드는 해시코드 계산에서 제외해도 된다. 또한 equals 비교에 사용되지 않은 필드는 반드시 제외해야한다.**

#### 31을 곱하는 이유
- 31이 홀수이면서 소수(prime)이기 때문이다. 이 숫자가 짝수이고 오버플로가 발생한다면 정보를 잃게 된다. 2를 곱하는 것은 시프트 연산과 같은 결과를 내기 때문이다.
  - ```
    0000 0101(2) = 5      ( = 5 X 1 )
    0000 1010(2) = 5 << 1 ( = 5 X 2 )
    0001 0100(2) = 5 << 2 ( = 5 X 2 X 2 )
    ...
    
    // 짝수 곱으로 인해 오버플로우가 일어날 경우 오른쪽은 모두 0으로 차게 되고 정보를 잃게 된다.
    0000 0000 = 5 << 7
    ```
- 31 * i = (i << 5) - i와 같고 곱셈을 시프트 연산과 뺄셈으로 대체해 최적화할 수 있다.
  - [lombok의 hashCode()](https://projectlombok.org/features/EqualsAndHashCode) 에서는 31이 아닌 59를 곱하는 것을 보니 최적화에 적절한 값이라면 상관 없는 것 같다.

#### 캐싱
- 클래스가 불변이고 해시코드를 계산하는 비용이 크다면 인스턴스 생성 시 캐싱하는 방식을 고려할 수 있다.
  ```
  private int hashCode;
    
  @Override
  public int hashCode() {
    int result = hashCode;
      if (result == 0) {
        result = Integer.hashCode(areaCode);
        result = 31 * result + Integer.hashCode(prefix);
        result = 31 * result + Integer.hashCode(lineNum);
        hashCode = result;
      }
    return result;
  }
  ```