# DataSource - DriverManager

## 기존 방식 - DriverManager 사용
```java
@Test
    void driverManager() throws SQLException {
        Connection con1 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        Connection con2 = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        log.info("Connection={}, Class={}", con1, con1.getClass());
        log.info("Connection={}, Class={}", con2, con2.getClass());
    }
```
- 기존 코드는 DriverManager 을 사용하여 커넥션을 획득할 때마다 URL, USERNAME, PASSWORD 가 필요했었다.

#### 실행 결과
<img width="807" alt="스크린샷 2023-06-01 오전 12 08 20" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/f5586a3d-99e6-4509-b430-3694ff72621f">

-----

## Spring 제공 - DataSource
```java
@Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource -> 항상 새로운 커넥션을 획득
        DataSource dataSource = new DriverManagerDataSource(URL, USERNAME, PASSWORD);
        useDataSource(dataSource);
    }

    private void useDataSource(DataSource dataSource) throws SQLException {
        Connection con1 = dataSource.getConnection();
        Connection con2 = dataSource.getConnection();
        log.info("Connection={}, Class={}", con1, con1.getClass());
        log.info("Connection={}, Class={}", con2, con2.getClass());
    }
}
```
- 기존 코드와 비슷해보이지만 큰 차이점이 존재한다.
- 우선, DriverManagerDataSource 객체지만 그 부모에 DataSource 가 있어서 DataSource 를 사용할 수 있다.
- 파라미터 차이 : DriverManager 를 사용할 때와 다르게 객체를 생성할 때만 필요한 파라미터를 주고, 커넥션을 획득할 때는 단순히 dataSource.getConnectcion() 만 호출하면 된다.

## 설정과 사용의 분리
- 설정 : DataSource 를 만들고 필요한 속성들을 사용해서 URL, USERNAME, PASSWORD 같은 부분을 설정해두는 것이다.   
- 이렇게 설정과 관련된 속성들은 한 곳에 있는 것이 향후 변경에 더 유연하게 대처할 수 있다.
- 사용 : 설정은 신경쓰지 않고, DataSource 의 getConnection() 만 호출하면 커넥션을 얻을 수 있는 것을 의미 !
- 결과적으로, 객체를 설정하는 부분과 사용하는 부분을 조금 더 명확하게 분리해야한다 !!!
