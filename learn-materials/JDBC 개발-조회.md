#JDBC 개발-조회

## MemberRepositoryV0
```java
public Member findById(String memberId) throws SQLException {
        String sql = "select * from member where member_id = ?";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = getConnection();
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, memberId);

            rs = pstmt.executeQuery();
            if (rs.next()) { // .next() 가 데이터가 있는지 없는지 검증하는 것.
                Member member = new Member();
                member.setMemberId(rs.getString("member_id"));
                member.setMoney(rs.getInt("money"));
                return member;
            } else { // 데이터가 없을 때.
                // memberId와 같이 key 값을 넣어주어야 어떤 member 에서 문제가 발생했는지 알 수 있다. * 중요 *
                throw new NoSuchElementException("member not found memberId=" + memberId);
            }

        } catch (SQLException e) {
            log.error("db error", e);
            throw e;
        } finally {
            // 종료는 항상 역순으로 !!
            close(con, pstmt, rs);
        }
    }
```
- sql : 데이터 조회를 위한 select SQL
- re = pstmt.excuteQuery() : 데이터를 변경할 때는 **executeUpdate()** 를 사용했었다. 이와 같이 조회일 때는 **excuteQuery()** 를 사용한다.   
- excuteQuery 는 결과를 ResultSet 에 담아서 반환한다.

-----

## ResultSet 
<img width="442" alt="스크린샷 2023-05-29 오후 11 32 12" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/155ff253-c990-4964-837f-106329b226f9">

ResultSet은 위 사진과 같은 구조로 되어있다.
- ResultSet 내부에 있는 커서( cursor )를 이동해 다음 데이터를 조회할 수 있다.
- rs.next() : 이것을 호출하면 커서가 다음으로 이동한다. **최초의 커서는 데이터를 가리키지 않아서 rs.next() 를 최초 한 번은 호출해야 데이터를 조회할 수 있다.**
-----

## findById()
- findById() 에서는 회원 하나를 조회하는 것이 목적이다. 따라서 조회 결과가 항상 1건이므로 While 문이 아닌 ! if 를 사용한 것이다.
-----

## Test MemberRepository
```java
@Slf4j
class MemberRepositoryV0Test {

    MemberRepositoryV0 repository = new MemberRepositoryV0();

    @Test
    void crud() throws SQLException {
        // save
        Member member = new Member("memberV2", 10000);
        repository.save(member);

        // findById
        Member findMember = repository.findById(member.getMemberId());
        log.info("findMember = {}", findMember);
        log.info("member == findMember {}", member == findMember);
        log.info("member equals findMember {}", member.equals(findMember));
        /**
         * @Data Annotation 이 equals and hashcode 를 만들어준다.
         * -> 3번 째 equals() 가 true 로 나오는 이유.
         * assertThat().isEqualTo(); 내부에서 equals() 사용하기 때문에 테스트가 통과하게 된다.
         */
        assertThat(findMember).isEqualTo(member);
    }
}
```
#### 실행결과
<img width="990" alt="스크린샷 2023-05-29 오후 11 12 10" src="https://github.com/novicePGT/learn-jdbc/assets/91667488/b032f1bb-b1bf-448a-b521-3ae91ce0194a">

- 실행결과 1 : Member 객체의 참조 값이 아니라 실제 데이터가 보여지는 이유는 @Data 가 toString() 을 적절히 오버라이딩 해서 보여주기 때문이다.
- 실행결과 2 : isEqualsTO() 로 findMember.equals(member)를 비교하는데, 이 결과가 참인 이유는 롬복의 @Data 는 해당 객체의 모든 필드를 사용하도록 equals() 를 오버라이딩 하기 때문이다.
-----

## Java equals and hashcode
equals 와 hashcode 는 Object 클래스에 정의되어 있다. 따라서 Java의 모든 객체는 Object 클래스에 정의된 equals 와 hashcode 를 상속받고 있다.   

#### 참고
> 2번째 log의 == 는 false 가 나온다. 그 이유는 서로 다른 메모리에 할당됨으로 동일하지 않다.
> 하지만 member 와 findMember 는 동일한 값을 가진다.
> 결과적으로 동일성을 비교하는 equals 를 사용하면 3번째 log는 true 가 된다.

#### hashCode()
hashCode() 는 실행 중( Runtime )에 객체의 유일한 integer 값을 반환한다.   
hashCode는 hashTable 과같은 자료구조를 사용할 때 데이터가 저장되는 위치를 결정하기 위해 사용된다.

#### eqauls() 와 hashCode의 관계
- 동일한 객체는 동일한 메모리 주소를 갖는다는 것을 의미하므로, 동일한 객체는 동일한 해시코드를 가져야 한다. 그렇기 때문에 우리가 equals() 메소드를 오버라이드 한다면, hashCode() 메소드도 함께 오버라이드 되어야 한다.
