# 트랜잭션 AOP 이해
- 지금까지 트랜잭션을 편리하게 처리하기 위해서 트랜잭션 추상화도 도입하고, 추가로 반복적인 트랜잭션 로직을 해결하기 위해 트랜잭션 템플릿도 도입했다.
- 트랜잭션을 처리하는 반복적인 코드는 해결할 수 있었지만, 서비스 계층에 순수한 비즈니스 로직만 남긴다는 목표는 아직 달성하지 못했다.
- 이럴 때, 스프링 AOP를 통해 프록시를 도입하면 문제를 깔끔하게 해결할 수 있다.

## 프록시를 통한 문제 해결

#### 프록시 도입 전
<img width="442" alt="스크린샷 2023-07-20 오후 2 55 35" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/57da2793-23a6-45c9-9039-caaf54352a98">
- 프록시를 도입하기 전에는 기존처럼 서비스 로직에서 트랜잭션을 직접 시작한다.

```java
//트랜잭션 시작
TransactionStatus status = transactionManager.getTransaction(new DefaultTransactionDefinition());

try {
  //비즈니스 로직
  bizLogic(fromId, toId, money); transactionManager.commit(status); //성공시 커밋
} catch (Exception e) {
    transactionManager.rollback(status); //실패시 롤백
    throw new IllegalStateException(e);
}
```


#### 프록시 도입 후
<img width="434" alt="스크린샷 2023-07-20 오후 2 58 08" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/6578e962-d31d-4cd3-88cb-deae4c71137f">

<img width="445" alt="스크린샷 2023-07-20 오후 2 59 05" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/a63fa071-edf8-4b39-b90a-8e4dc4d348dc">

- 프록시 도입 전 : 서비스에 비즈니스 로직과 트랜잭션 처리 로직이 함께 섞여있다.
- 프록시 도입 후 : 트랜잭션 프록시가 트랜잭션 처리 로직을 모두 가져간다. 그리고 트랜잭션을 시작한 후에 실제 서비스를 대신 호출한다.
- 트랜잭션 프록시 덕분에 서비스 계층에는 순수한 비즈니스 로직만 남길 수 있다.
