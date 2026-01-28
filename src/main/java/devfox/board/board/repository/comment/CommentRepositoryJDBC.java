package devfox.board.board.repository.comment;

import devfox.board.board.dto.response.ResponseComment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryJDBC {

    private final NamedParameterJdbcTemplate template;


    public List<ResponseComment> findByBoardIdByNoQueryDsl(Long boardId, Long cursorId, int size) {


        String sql = """
                SELECT
                 c.id as id
                 c.content as content, 
                 u.username as username
                FROM comment c
                JOIN users u
                ON u.id = c.user_id
                WHERE c.board_id = :boardId
                AND c.id < :cursorId
                ORDER BY c.id DESC
                LIMIT :size
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("size", size);
        params.addValue("boardId", boardId);
        params.addValue("cursorId", cursorId);

        List<ResponseComment> result = template.query(sql, params, (rs, roNum) ->

                ResponseComment.builder()
                        .commentId(rs.getLong("id"))
                        .comment(rs.getString("content"))
                        .username(rs.getString("username"))
                        .build()
        );
        return result;
    }
}
