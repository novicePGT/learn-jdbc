# DB 락 - 조회
- 일반저긴 조회는 락을 사용하지 않는다.
- 데이터베이스마다 다르지만, 보통 데이터를 조회할 때는 락을 획득하지 않고 바로 데이터를 조회할 수 있다.

## 조회와 락
- 데이터를 조회할 때도 락을 획득하고 싶을 때가 있다.
- 이럴 때, select for update 구문을 사용한다 !
- 이렇게 하면 세션 1이 조회 시점에 락을 가져가버리기 때문에 다른 세션에서 해당 데이터를 변경할 수 없다.
- 당연하게 이 경우도 트랜잭션을 커밋하면 락을 반납한다 !!

## 조회 시점에서 락을 가져오는 이유?
- 트랜잭션 종료 시점까지 해당 데이터를 다른 곳에서 변경하지 못하도록 강제로 막아야 할 때 사용한다.
- 예를 들어서 애플리케이션 로직에서 memberA의 금액을 조회한 다음에 이 금액 정보로 애플리케이션에서 어떤 로직을 실행한다.
- 근데 그 로직이 너어어어어어무 중요한 계산이어서 완료 할 때까지 memberA의 금액을 다른 곳에서 변경하면 안될 때 사용한다.

## 락 1
<img width="901" alt="스크린샷 2023-06-20 오후 1 05 31" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/6894f03b-2e48-41d7-9837-bf4f25bb58c3">   
```sql
set autocommit true;
  delete from member;
  insert into member(member_id, money) values ('memberA',10000);
```

``` sql
set autocommit false;
select * from member where member_id='memberA' for update;
```

- 세션 1이 select for update 구문을 사용하여 조회를 하면서 동시에 선택한 로우의 락도 획득한다.
- 무우우우울론 락이 없다면 락을 획득할 때까지 대기한다 !!
- 세션 1은 트랜잭션을 종료할 때 까지 memberA의 로우의 락을 보유한다.

## 락 2
```sql
set autocommit false;
update member set money=500 where member_id = 'memberA';
```

- 이후로 세션 2가 데이터를 변경하기 위해 sql을 날렸다.
- 세션 1이 락을 가지고 있기 때문에 세션 2는 락을 획득할 때 까지 대기한다.
- 똑같이 락을 사용하므로 락 타임아웃이 발생할 수 있다.

## 정리
- 트랜잭션과 락은 데이터베이스마다 실제 동작하는 방식이 조금씩 다르기 때문에, 해당 데이터베이스 매뉴얼을 확인해보고 의도한대로 동작하는지 테스트한 이후에 사용하자 !
