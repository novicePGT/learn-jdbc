package hello.jdbc.repository;

import hello.jdbc.connection.DBConnectionUtil;
import hello.jdbc.domain.Member;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;

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
