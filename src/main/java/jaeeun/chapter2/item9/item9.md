## [item 9] try-finally 보다는 try-with-resources를 사용하라.

- 자바 라이브러리에는 InputStream, OutputStream, java.sql.Connection과 같이 정리(close)가 가능한 리소스가 많다.
- 전통적으로는 (예외가 발생하거나 메서드에서 반환되는 경우를 포함하여 )자원을 닫기 위해 try-finally를 사용하여 finally에서 정리(close)를 호출을 한다.

#### try-finally의 문제점
- 이 때 예외는 try 블록과 finally 블록 모두에서 발생할 수 있는데 그 경우 마지막에 발생한 예외 (finally 블록 안에서 발생한 예외)가 처음 발생한 예외를 집어 삼키게 된다.
  - 즉 첫번째 예외에 관한 정보가 남지 않아 디버깅이 어려워진다.

#### try-with-resources
- try-finally의 문제점을 해결하기 위해 자바 7부터 도입된 try-with-resources를 사용할 수 있다.
- **AutoCloseable 인터페이스를 구현하고 있는 자원**에 대해 적용이 가능하다.
- 숨겨진 예외 들을 스택 추적 내역에 숨겨졌다 (suppressed)는 태그와 함께 출력되며 첫번째 예외에 관한 정보부터 확인할 수 있다.
