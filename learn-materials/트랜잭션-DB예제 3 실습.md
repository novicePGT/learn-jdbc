# 트랜잭션 - DB 예제 3( 실습 )
- H2 데이터베이스 웹 콘솔 창을 2개를 열어 세션 2개를 준비한다.
- 열어진 2개의 url 이 서로 달라야 2개의 세션이 준비된 상태이다.

## 신규 데이터 추가 - 커밋 전
<img width="443" alt="스크린샷 2023-06-13 오후 11 33 55" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/145fd0f4-d06d-44f2-a15a-a72b59223c6a">   

```sql
//트랜잭션 시작
set autocommit false; //수동 커밋 모드
insert into member(member_id, money) values ('newId1',10000); 
insert into member(member_id, money) values ('newId2',10000);
```
- 두 개의 콘솔 창에서 테이블을 확인해보면 세션 1 에서 입력 후 커밋을 하지 않은 상태이다.
- 세션 1 에서 쿼리를 날린 후 커밋하지 않은 상태임에 세션 1 에서 테이블을 조회하면 입력한 데이터가 보인다.
- 하지만, commit 을 하지 않은 상태임에 세션 2 에서는 데이터가 보이지 않는다.

## 신규 데이터 추가 - 커밋 후
<img width="441" alt="스크린샷 2023-06-13 오후 11 36 13" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/ea1586f7-c9bd-4eb3-b2e9-19a2511bbc26">   
- 세션 1에서 신규 데이터를 입력하고 commit 을 하게 되면 데이터베이스에 반영하게 된다.
- 결과적으로 세션 2에서도 입력된 정보를 확인할 수 있다.

## Rollback
<img width="457" alt="스크린샷 2023-06-13 오후 11 37 22" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/8e56e94d-54fc-45af-a14a-8943704a3a53">     

- 예제를 먼저 초기화 해주고, 아래 쿼리를 다시 날려준다.

```sql
//트랜잭션 시작
set autocommit false; //수동 커밋 모드
insert into member(member_id, money) values ('newId1',10000); 
insert into member(member_id, money) values ('newId2',10000)
```
- 결과를 보면 아직 세션 1에서 commit 하지 않았기 때문에 세션 2에서는 조회해도 보이지 않을 것이다.
- 그 이후, 세션 1에서 rollback 을 호출해보면 세션 1에서도 날렸던 쿼리는 정보를 반영하지 않고, 세션 2에서도 정보는 변동이 없을 것이다.
- 결과적으로 rollback 을 호출하여 DB에 데이터가 반영되지 않은 모습을 볼 수 있을 것이다.
