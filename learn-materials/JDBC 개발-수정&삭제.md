# JDBC 개발-수정&삭제

## 수정 UPDATE
```java
 public void update(String memberId, int money) throws SQLException {
        String sql = "update member set money=? where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            /**
             * parameterIndex 는 먼저 들어간 순서대로 -> update member set money=? where member_id=?
             * 위 sql 에서는 money 가 먼저니까 1 파라미터, member_id 는 2 파라미터.
             */
            pstmt.setInt(1, money);
            pstmt.setString(2, memberId);
            int resultSize = pstmt.executeUpdate();
            log.info("resultSize={}", resultSize);
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
```
- 수정&삭제는 등록과 비슷하게 **excuteUpdate()** 를 사용한다.
- **excuteUpdate()** 는 쿼리를 실행하고 영향받은 row 수를 반환한다.   
여기서는 하나의 데이터만 영향을 받기 때문에 반환값은 1이 된다.

## UPDATE Test
```java
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
     }
```
Update 는 멤버의 id 값를 가져와 money 를 20000 으로 변경한다.   
변경된 정보를 확인하기 위해 findById 로 MemberV7 의 이름으로 조회한다.   
마지막으로 assertThat 으로 비교하는데 업데이트된 멤버의 값이 20000과 같아서 업데이트가 잘 된 것인지 확인한다.   

-----

## 삭제 DELETE
```java
public void delete(String memberId) throws SQLException {
        String sql = "delete from member where member_id=?";

        Connection con = null;
        PreparedStatement pstmt = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            close(con, pstmt, null);
        }
    }
```
쿼리만 변경되고 내용은 비슷하다.

## DELETE Test
```java
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

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

        /**
         * 테스트는 반복이 되는 것이 매우 중요하다 !!
         * 위와 같이 CRUD 로 구성되면 반복이 가능하다 !!!
         * 하지만, 중간에 Exception 이 있으면 delete 가 호출되지 않을 수 있다.
         * 그러므로 delete 를 마지막에 호출하는 것도 궁극적인 호출 방법이 아니다.
         */
    }
}
```
회원을 삭제한 다음 findById 로 조회한다.   
조회된 회원이 없으므로 이전에 findById 에서 처리했던 throw new NoSuchElementException("member not found memberId=" + memberId); 이 터지게 되면서 asserThatThrownBy() 예외가 발생해 검증에 성공하게 된다.

#### 참고
> 마지막에 회원을 삭제하기 때문에 테스트가 정상 수행되면, 이제부터는 같은 테스트를 반복해서 실행할 수
있다. 물론 테스트 중간에 오류가 발생해서 삭제 로직을 수행할 수 없다면 테스트를 반복해서 실행할 수
없다.   
> 트랜잭션을 활용하면 이 문제를 해결할 수 있다고 한다.
