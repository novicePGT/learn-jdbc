package hello.jdbc.service;

import hello.jdbc.domain.Member;
import hello.jdbc.repository.MemberRepositoryV2;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 트랜잭션 - 파라미터 연동, 풀을 고려한 종료
 */
@Slf4j
@RequiredArgsConstructor
public class MemberServiceV2 {

    private final DataSource dataSource;
    private final MemberRepositoryV2 memberRepository;

    public void accountTransfer(String fromId, String toId, int money) throws SQLException {

        Connection connection = dataSource.getConnection();
        try {
            //트랜잭션 시작 -> set autocommit false !! : 트랜잭션을 시작하려면 커넥션이 필요하다.
            //포인트 : 트랜잭션을 사용하는 동안 같은 커넥션을 유지해야한다 !!! -> 그래야 같은 세션을 사용할 수 있음.
            connection.setAutoCommit(false);

            //비즈니스 로직 수행
            bizLogic(connection, fromId, toId, money);

            //성공시 : 커밋
            connection.commit();
        } catch (Exception e) {
            // 실패시 : 롤백
            connection.rollback();
            throw new IllegalStateException(e);
        } finally {
            release(connection);
        }
    }

    private void bizLogic(Connection connection, String fromId, String toId, int money) throws SQLException {
        Member fromMember = memberRepository.findById(connection, fromId);
        Member toMember = memberRepository.findById(connection, toId);

        memberRepository.update(connection, fromId, fromMember.getMoney() - money);
        validation(toMember);
        memberRepository.update(connection, toId, toMember.getMoney() + money);
    }

    private void release(Connection connection) {
        if (connection != null) {
            try {
                //setAutoCommit 을 false 로 설정해두고 커넥션 풀로 돌아가면 그 커넥션은 false 를 유지함,,,
                connection.setAutoCommit(true);
                connection.close();
            } catch (Exception e) {
                log.info("error", e);
            }
        }
    }

    private static void validation(Member toMember) {
        if (toMember.getMemberId().equals("ex")) {
            throw new IllegalStateException("이체 중 예외 발생");
        }
    }
}
