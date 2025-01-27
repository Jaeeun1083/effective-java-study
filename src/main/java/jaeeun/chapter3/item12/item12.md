## [item 12] toString을 항상 재정의하라.

- Object의 기본 toString 메서드는 단순히 클래스_이름@16진수로_표시한_해시코드를 반환한다.
  - ```
    public String toString() {
        return getClass().getName() + "@" + Integer.toHexString(hashCode());
    }
    ```
- toString의 규약은 (상위 클래스에서 알맞게 재정의 하지 않았다면) 모든 하위 클래스에서 이 메서드를 재정의하라는 것이고 재정의 시 그 객체가 가진 주요 정보 모두를 반환하는 것이 좋다.

#### 반환 값의 포맷 문서화

- toString 구현 시 반환 값의 포맷을 문서화할지 정해야 한다.
- 포맷을 한번 명시하면 (그 클래스가 많이 쓰인다면) 평생 그 포맷에 얽매이게 된다. 반대로 포맷을 명시하지 않는다면 향후 릴리스에서 정보를 더 넣거나 포맷을 개선할 수 있는 유연성을 얻게 된다.
- 포맷 명시할 경우
  - ```
    /**
    * 이 전화번호의 문자열 표현을 반환한다.
    * 이 문자열은 "XXX-YYY-ZZZZ" 형태의 12글자로 구성된다.
    * XXX는 지역코드, YYY는 프리픽스, ZZZZ는 가입자 번호다.
    * 각각의 대문자는 10진수 숫자 하나를 나타낸다.
    *
    * 전화번호의 각 부분의 값이 너무 작아서 자릿수를 채울 수 없다면,
    * 앞에서부터 0으로 채워나간다. 예컨데 가입자 번호가 123이라면
    * 전화번호의 마지막 네 문자는 "0123"이 된다.
    */
    @Override
    public String toString() {
      return String.format("%03d-%03d-%04d", areaCode, prefix, lineNum);
    }
    ```
- 포맷을 명시하지 않을 경우
  - ```
     /**
     * 이 약물에 관한 대략적인 설명을 반환한다.
     * 다음은 이 설명의 일반적인 형태이나,
     * 상세 형식은 정해지지 않았으며 향후 변경될 수 있다.
     *
     "[약물 #9: 유형-사랑, 냄새=테러빈유, 겉모습=먹물]"
     */
     @Override
     public String toString() { ... }
     ```

#### 주의 사항
- 포맷 명시 여부와 상관없이 toString이 반환한 값에 포함된 정보를 얻어올 수 있는 API를 제공하자.
- 열거 타입 클래스는 자바가 toString을 제공하므로 따로 재정의할 필요가 없다.
- 하위 클래스들이 공유해야 할 문자열 표현이 있는 추상 클래스라면 toString을 재정의해야 한다.