# DataSource 적용
```java
/**
 * JDBC - DataSource 사용, jdbcUtils 사용
 */
@Slf4j
public class MemberRepositoryV1 {

    private final DataSource dataSource;

    public MemberRepositoryV1(DataSource dataSource) {
        this.dataSource = dataSource;
    }
    
    //save()
    //findById()
    //update()
    //delete()

    private void close(Connection con, Statement statement, ResultSet resultSet) {

        JdbcUtils.closeResultSet(resultSet);
        JdbcUtils.closeStatement(statement);
        JdbcUtils.closeConnection(con);
    }

    private Connection getConnection() throws SQLException {
        Connection con = dataSource.getConnection();
        log.info("get connection={}, class={}", con, con.getClass());
        return DBConnectionUtil.getConnection();
    }
}
```

#### DataSource 의존 관계 주입
- 외부에서 DataSourcefmf 주입 받아서 사용한다 : 직접만든 DBConnectionUtil 을 사용하지 않아도 됨.
- DataSource 는 표준 인터페이스로 DriverManagerDataSource 에서 HikariDataSource 로 변경되어도 해당 코드를 변경하지 않아도 됨.
- JdbcUtils 를 사용하면 커넥션은 편리하게 닫을 수 있다 : static 으로 선언되어 있어서 조합 과정없이 간편하게 !

## DriverManagerDataSource 사용
```java
@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach()
    void beforeEach() {
        // 기본 DriverManager - 항상 새로운 커넥션을 획득
        DriverManagerDataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
    }

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV7", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(findMember).isEqualTo(member);

        // update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

- DriverManager 을 사용하면 conn0, conn1... 과 같이 항상 새로운 커네션이 생성되어서 사용되는 것을 확인할 수 있다.

## HikariDataSource 사용
```java
@Slf4j
class MemberRepositoryV1Test {

    MemberRepositoryV1 repository;

    @BeforeEach()
    void beforeEach() {
        // 커넥션 풀링
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(URL);
        dataSource.setUsername(USERNAME);
        dataSource.setPassword(PASSWORD);
        repository = new MemberRepositoryV1(dataSource);
    }

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV7", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        assertThat(findMember).isEqualTo(member);

        // update: money: 10000 -> 20000
        repository.update(member.getMemberId(), 20000);
        Member updatedMember = repository.findById(member.getMemberId());
        assertThat(updatedMember.getMoney()).isEqualTo(20000);

        // delete
        repository.delete(member.getMemberId());
        assertThatThrownBy(() -> repository.findById(member.getMemberId()))
                .isInstanceOf(NoSuchElementException.class);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<img width="410" alt="스크린샷 2023-06-04 오후 11 40 09" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/4e127fc4-97c5-4757-af68-20716eecce89">   

- 커넥션 풀 사용 시 conn0 커넥션이 재사용 된 것을 확인할 수 있다.
- 테스트는 순서대로 실행되기 때문에 커넥션을 사용하고 다시 돌려주는 것을 반복한다.

## DI
- DriverManagerDataSource HikariDataSource 로 변경해도 MemberRepositoryV1 의 코드는 전혀 변경하지 않아도 된다. MemberRepositoryV1 는 DataSource 인터페이스에만 의존하기 때문이다. 이것이 DataSource 를 사용하는 장점이다.(DI + OCP)
