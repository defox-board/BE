package devfox.board.repository;

import devfox.board.dto.CreateBoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BoardRepository {

    private final NamedParameterJdbcTemplate template;


    public long save(Long userId,CreateBoardDto dto) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();


        String sql = """
                INSERT INTO board (content,user_id,created_at)
                VALUE
                (:content,
                :user_id,
                :NOW()
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("content", dto.getContent());
        params.addValue("user_id", userId);

        template.update(sql, params, keyHolder);
        long boardId = keyHolder.getKey().longValue();
        return boardId;
    }
}
