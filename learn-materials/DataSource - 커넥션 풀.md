# DataSource - 커넥션 풀
```java
@Test
    void dataSourceConnectionPool() throws SQLException, InterruptedException {
        // 커넥션 풀링: HikariCP
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setJdbcUrl(URL);
        hikariDataSource.setUsername(USERNAME);
        hikariDataSource.setPassword(PASSWORD);
        hikariDataSource.setMaximumPoolSize(10);
        hikariDataSource.setPoolName("MyPool");

        // MyPool connection adder : Thread 임 !!

        useDataSource(hikariDataSource);
        Thread.sleep(1000);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("Connection={}, Class={}", con1, con1.getClass());
        log.info("Connection={}, Class={}", con2, con2.getClass());
    }
}
```
- HikariCP 커넥션 풀을 사용한다. HikariDataSource 는 DataSource 인터페이스를 구현하고 있어 DataSource 로 받을 수 있지만 .setJdbcUrl() 같은게 없어서   
HikariDataSource 를 받아서 구현했다.

- .setMaximumPollSize() & .setPoolName() 은 최대 풀 사이즈를 결정하고, 풀의 이름을 만들 수 있다.
- 커넥션 풀에서 커넥션을 생성하는 작업은 애플리케이션 실행 속도에 영향을 주지 않기 위해 별도의 스레드에서 작동 !!
- 별도의 스레드에서 동작하는 이유가 있어서 풀의 생성을 확인하려면 sleep() 통해 대기시켜야 로그를 통해 확인 가능하다.

#### 실행결과
<img width="978" alt="스크린샷 2023-06-01 오전 12 43 38" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/4cfd524d-b148-4f6d-965b-33f15ada51e6">   

#### HikariConfig
- HikariCP 관련 설정들을 확인할 수 있다.

#### MyPool Connection adder
- 별도의 스레드를 사용해서 커넥션 풀에 커넥션을 채우고 있는 것을 확인할 수 있다.
- 왜 별도의 스레드를 사용해서 커넥션 풀에 커넥션을 채우는 것인지?
- A : 커넥션 풀에 커넥션을 채우는 것은 상대적으로 오래걸린다. 예를 들면 100개라던지... 따라서 커넥션을 풀에 채울때까지 마냥 대기하면 시간이 엄청 길어진다 !
- 그래서 별도의 스레드를 만들어서 커넥션 풀을 채워야 실행 시간에 영향을 주지 않는 거댱

#### 커넥션 풀에서 커넥션 획득
- 커넥션 풀에서 커넥션을 획득하면 로그 : "MyPool - After adding stats (total=10, active=2, idle=8, waiting=0)"
- total 총 커넥션의 개수, 활동중인 커넥션 2개, 풀에서 대기중 8개, 커넥션 요청 대기 0개 요로케 나온다 !

#### HikariCP 커넥션 풀 참고 공식 자료
-  https://github.com/brettwooldridge/HikariCP
