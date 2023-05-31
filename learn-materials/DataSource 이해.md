# DataSource 이해
커넥션을 획득하는 방법으로는 앞에서 배웠던 것처럼 다양한 방법이 있다.   
<img width="909" alt="스크린샷 2023-05-31 오전 10 23 24" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/c38ad75f-abc3-4bba-adfb-aa71ed3c9f01">

## DriverManager 를 통해 커넥션 획득
<img width="903" alt="스크린샷 2023-05-31 오전 10 24 25" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/57446dbc-3a28-4965-ad62-fc11296c73be">   
앞서 JDBC로 개발된 애플리케이션처럼 DriverManager 를 통해 커넥션을 획득하다가 커넥션 풀을 사용하는 방법으로 변경하려면 어떻게 해야하는지 궁금하다 !

-----

## DriverManager 를 통해 획득하다가 커넥션 풀로 변경 시 문제
<img width="897" alt="스크린샷 2023-05-31 오전 10 26 25" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/40bfbb62-099c-4363-be1a-02c2beda7f02">   
DriverManager 를 사용해서 커넥션을 획득하다가 HikariCP 같은 커넥션 풀을 사용하도록 변경하려면 ** 애플리케이션 코드도 변경해야 한다. **   
의존관계가 DriverManager -> HikariCP 로 변경되고, 둘의 사용법 또한 다를 것이기 때문이다.

-----

## JAVA 커넥션을 획득하는 방법을 추상화
<img width="898" alt="스크린샷 2023-05-31 오전 10 29 57" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/37be32e5-9a2c-4249-9026-9134c9b023d4">   
커넥션 풀로 획득하다가 HikariCP 로 변경하려고 할 때, 애플리케이션 코드도 변경되는 것을 줄이고자 등장한게 **커넥션을 획득하는 방법을 추상화** 하는 것이다 !!   
자바에서는 이런 문제를 해결하기 위해 javax.sql.DataSource 라는 인터페이스를 제공한다.   
DataSource 는 커넥션을 획득하는 방법을 추상화 하는 인터페이스이다.

-----

## DataSource 핵심 기능만 축약
```java
public interface DataSource {
    Connection getConnection() throws SQLException;
}
```

-----

## 정리
> 대부분의 커넥션 풀은 DataSource 인터페이스를 이미 구현해두었다.   
> 따라서 개발자는 DBCP2 커넥션 풀 , HikariCP 커넥션 풀 의 코드를 직접 의존하는 것이 아니라 DataSource 인터페이스에만 의존하도록 애플리케이션 로직을 작성하면 된다.
> 커넥션 풀 구현 기술을 변경하고 싶으면 해당 구현체로 갈아끼우기만 하면 된다.
> DriverManager 는 DataSource 인터페이스를 사용하지 않는다.  
> 스프링은 DriverManager 도 DataSource 를 통해서 사용할 수 있도록 DriverManagerDataSource 라는 DataSource 를 구현한 클래스를 제공한다.
> 자바는 DataSource 를 통해 커넥션을 획득하는 방법을 추상화했다. 이제 애플리케이션 로직은 DataSource 인터페이스에만 의존하면 된다.
