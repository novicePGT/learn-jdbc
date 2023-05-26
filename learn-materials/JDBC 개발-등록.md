# JDBC 개발-등록

## 애플리케이션 서버와 DB - 일반적인 사용법( 각각 데이터베이스 마다 사용법 다름 )

<img width="512" alt="스크린샷 2023-05-26 오전 11 41 48" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/e07ba04d-f5ba-454e-a95c-c5a942c5c0a0">

-----

## JDBC 표준 인터페이스
JDBC( Java Database Connectivity )는 자바에서 데이터베이스에 접속할 수 있도록 하는 자바 API.
JDBC는 데이터베이스에서 자료를 쿼리하거나 업데이트하는 방법을 제공함.

<img width="458" alt="스크린샷 2023-05-26 오전 11 44 56" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/77a0cce1-cabf-4628-9871-bada1477e823">

-----

## Member Class

```java
package hello.jdbc.domain;

import lombok.Data;

@Data
public class Member {

    private String memberId;
    private int money;

    public Member() {

    }

    public Member(String memberId, int money) {
        this.memberId = memberId;
        this.money = money;
    }
}
```

## Member RepositoryV0

```java
package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement preparedStatement = null;

        con = getConnection();
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate(); // Int 반환, 영향을 주는 row 수 만큼 반환
            return member;
        } catch (SQLException e) {
            log.error("[DB ERROR]", e);
            throw e;
        } finally {
            /**
             * 시작과 역순으로 close
             * 조건을 부여하지 않으면 둘 중 첫 번째가 Exception 이 터졌을 때 아래있는 메서드가 떠다닐 수 있음 -> 조건 부여하자
             */
            close(con, preparedStatement, null);
        }
    }

    private void close(Connection con, Statement statement, ResultSet resultSet) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("[Error]: ", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("[Error]: ", e);
            }
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("[Error]: ", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}

```

- con.prepareStatement(sql) : 데이터베이스에 전달할 SQL과 파라미터로 전달할 데이터들을준비한다.
   - sql : insert into member(member_id, money) values(?, ?)"pstmt.setString(1, member.getMemberId()) : SQL의 첫번째 ? 에 값을 지정한다. 문자이므로
setString 을 사용한다.
   - pstmt.setInt(2, member.getMoney()) : SQL의 두번째 ? 에 값을 지정한다. Int 형 숫자이므로
setInt 를 지정한다.

-----

## 리소스 정리
쿼리를 실행하고 나면 리소스를 정리해야 한다. 여기서는 Connection , PreparedStatement 를 사용했다. 리소스를 정리할 때는 항상 역순으로 해야한다. Connection 을 먼저 획득하고 Connection 을 통해 PreparedStatement 를 만들었기 때문에 리소스를 반환할 때는 PreparedStatement 를 먼저 종료하고, 그 다음에 Connection 을 종료하면 된다. 

#### 주의
> 리소스 정리는 꼭! 해주어야 한다. 따라서 예외가 발생하든, 하지 않든 항상 수행되어야 하므로 finally
구문에 주의해서 작성해야한다. 만약 이 부분을 놓치게 되면 커넥션이 끊어지지 않고 계속 유지되는 문제가 발생할 수 있다. 이런 것을 리소스 누수라고 하는데, 결과적으로 커넥션 부족으로 장애가 발생할 수 있다.

#### 참고
> PreparedStatement 는 Statement 의 자식 타입인데, ? 를 통한 파라미터 바인딩을 가능하게 해준다. > 참고로 SQL Injection 공격을 예방하려면 PreparedStatement 를 통한 파라미터 바인딩 방식을
사용해야 한다.
