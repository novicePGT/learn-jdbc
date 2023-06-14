# DB 락 - 개념 이해

- 만약 세션 1이 트랜잭션을 시작하고 데이터를 수정하는 동안 아직 커밋을 수행하지 않았는데, 세션 2에서 동시에 같은 데이터를 수정하게 되면 여러가지 문제가 발생한다.
- 트랜잭션의 원자성이 깨지는 것이다.
- 여기에서 세션 1이 중간에 rollback 하게 되면 세션 2는 잘못된 데이터를 수정하는 문제가 발생한다.
- 이런 커밋이나 롤백 전에 데이털르 수정할 수 없게 막는 것이 DB 락의 개념이다 !!

## 락 0
<img width="861" alt="스크린샷 2023-06-14 오후 1 59 41" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/b5fb1f94-07be-4f84-9f93-b516953ff908">   

- 세션 1은 A의 금액을 500원으로 변경하고 싶고, 세션 2는 같은 A의 금액을 1000원으로 변경하고 싶다.
- 데이터베이스는 이런 문제를 해결하기 위해 DB 락 개념을 제공한다.

## 락 1
<img width="846" alt="스크린샷 2023-06-14 오후 2 00 47" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/285e85b8-ee06-4dee-95b6-e2716eeee737">   

1. 세션 1은 트랜잭션을 시작한다. ( 0.001 초라도 먼저 들어온 세션이 우선권을 가진다. )
2. 세션 1은 A의 money 를 500으로 변경을 시도한다. ( 이 때, Low의 락을 먼저 획득해야 한다. )
3. 세션 1은 락을 획득했으므로 해당 로우에 update sql 을 수행한다.

## 락 2
<img width="851" alt="스크린샷 2023-06-14 오후 2 02 22" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/3d02fc50-2497-412b-adcf-5de6fcae3b38">   

4. 세션 2는 트랜잭션을 시작한다.
5. 세션 2도 A의 money 데이터를 변경하려고 시도한다. ( 이 때, 락이 없으므로 락이 돌아올 때까지 대기한다. )
6. 무한정 대기하는 것은 아니다. 락 대기 시간을 넘어가면 락 타임아웃 오류가 발생한다.
7. 락 대기시간은 설정할 수 있다.

## 락 3
<img width="846" alt="스크린샷 2023-06-14 오후 2 04 11" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/8486889b-a8b0-4d84-a665-5102c89671f2">   

8. 세션 1은 commit 을 수행한다. commit 으로 트랜잭션이 종료되었으므로 락도 반납한다.

## 락 4
<img width="846" alt="스크린샷 2023-06-14 오후 2 05 07" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/06070b7d-abe7-4516-b0c2-c8dc53ad153e">   

9. 락을 획득하기 위해 대기하던 세션 2가 락을 획득한다.

## 락 5
<img width="847" alt="스크린샷 2023-06-14 오후 2 05 40" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/19f84c02-31c8-4318-a5ed-868745bc7e56">   

10. 락을 획득한 세션 2는 update sql 을 수행한다.

## 락 6
<img width="852" alt="스크린샷 2023-06-14 오후 2 06 15" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/5ddb9441-c4e3-4b9d-bbff-e532dd90de4c">   

11. 세션 2는 commit 을 수행하고 트랜잭션이 종료되었으므로 락을 반납한다.




