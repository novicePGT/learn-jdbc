# DB 락 - 변경

## 락 1
<img width="896" alt="스크린샷 2023-06-19 오후 2 52 45" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/f1584f39-7054-4f26-89bb-7b8394893dde">   

```sql
set autocommit false;
update member set money=500 where member_id = 'memberA';
```

- 세션 1이 트랜잭션을 시작하고, memberA의 데이터를 500원으로 업데이트 했다. 하지만 커밋하지 않은 상태다.
- 이 때, memberA 로우의 락은 세션 1이 가지게 된다.

## 락 2
<img width="892" alt="스크린샷 2023-06-19 오후 2 54 06" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/eaf4fade-0c45-48e5-9db8-db2b40e4206d">   

```sql
  SET LOCK_TIMEOUT 60000;
  set autocommit false;
  update member set money=1000 where member_id = 'memberA';
```

- 세션 2는 memberA의 데이터를 1000원으로 수정하려고 한다.
- 세션 1이 트랜잭션을 커밋하거나, 롤백하지 않아서 세션 1이 락을 가지고 있다.
- 따라서 세션 2는 락을 획득하지 못해 데이터를 수정할 수 없는 상태로 60000ms 동안 대기하게 된다.

## 세션 2 : 락 획득
- 세션 1이 커밋하거나 롤백하면 기다리던 세션 2가 락을 획득한다.
- 락을 획득한 세션 2는 데이터를 수정할 수 있다.
- 세션 2에서도 커밋이나 롤백하면 락을 반납한다.

## 락 타임아웃
- SET LOCK_TIMEOUT <milliseconds> : 락 타임아웃 시간을 설정한다.
- SET LOCK_TIMEOUT 10000 10초, 세션2에 설정하면 세션2가 10초 동안 대기해도 락을 얻지 못하면 락 타임아웃 오류가 발생한다.
  
## 락 타임 아웃 발생 시 실행 결과
<img width="910" alt="스크린샷 2023-06-19 오후 2 57 51" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/453fe9a6-9261-4719-9cd3-60642a8ed93c">
  
  
  - 세션 1이 memeberA의 데이터를 변경하고 커밋하지 않은 상태로 지속되면 락 타임아웃이 발생하고 위는 락 타임 아웃의 실행 결과를 보여준다.
