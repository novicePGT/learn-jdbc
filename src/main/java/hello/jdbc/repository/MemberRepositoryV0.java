package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.NoSuchElementException;

/**
 * JDBC - DriverManager 사용
 */
@Slf4j
public class MemberRepositoryV0 {

    public Member save(Member member) throws SQLException {
        String sql = "insert into member(member_id, money) values (?, ?)";

        Connection con = null;
        PreparedStatement preparedStatement = null;

        con = getConnection();
        try {
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, member.getMemberId());
            preparedStatement.setInt(2, member.getMoney());
            preparedStatement.executeUpdate(); // Int 반환, 영향을 주는 row 수 만큼 반환
            return member;
        } catch (SQLException e) {
            log.error("[DB ERROR]", e);
            throw e;
        } finally {
            /**
             * 시작과 역순으로 close
             * 조건을 부여하지 않으면 둘 중 첫 번째가 Exception 이 터졌을 때 아래있는 메서드가 떠다닐 수 있음 -> 조건 부여하자
             */
            close(con, preparedStatement, null);
        }
    }

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

    private void close(Connection con, Statement statement, ResultSet resultSet) {
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                log.info("[Error]: ", e);
            }
        }

        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                log.info("[Error]: ", e);
            }
        }

        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                log.info("[Error]: ", e);
            }
        }
    }

    private Connection getConnection() {
        return DBConnectionUtil.getConnection();
    }
}