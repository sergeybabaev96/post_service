package faang.school.postservice.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class NewsFeedJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public void batchInsertFeeds(Map<Long, List<Long>> userFeeds) {
        String sql = """
            INSERT INTO feed (user_id, post_ids)
            VALUES (?, ?::bigint[])
            ON CONFLICT (user_id) DO UPDATE
            SET post_ids = feed.post_ids || EXCLUDED.post_ids,
                updated_at = CURRENT_TIMESTAMP
            """;

        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Map.Entry<Long, List<Long>> entry = (Map.Entry<Long, List<Long>>) userFeeds.entrySet().toArray()[i];
                Long userId = entry.getKey();
                List<Long> postIds = entry.getValue();

                ps.setLong(1, userId);
                ps.setString(2, toSqlArray(postIds));
            }

            @Override
            public int getBatchSize() {
                return userFeeds.size();
            }
        });
    }

    public List<Long> findColdFeed(long userId, int limit) {
        String sql = """
        WITH user_feed AS (
            SELECT post_ids
            FROM news_feed
            WHERE user_id = ?
        )
        SELECT unnest(post_ids) AS post_id
        FROM user_feed
        LIMIT ?
    """;

        return jdbcTemplate.query(
                sql,
                (rs, rowNum) -> rs.getLong("post_id"),
                userId,
                limit
        );
    }

    private String toSqlArray(List<Long> list) {
        return list.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(",", "{", "}"));
    }


}
