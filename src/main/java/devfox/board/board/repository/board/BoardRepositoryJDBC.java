package devfox.board.board.repository.board;

import devfox.board.board.dto.response.ResponseBoardDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class BoardRepositoryJDBC {


    private final NamedParameterJdbcTemplate template;


    public List<ResponseBoardDto> findAll(Pageable pageable) {


        int size = pageable.getPageSize();
        long offset = pageable.getOffset();
        String sql = """
                
                SELECT 
                b.id, b.title, b.content, u.id as user_id, u.username as username, b.created_at, b.updated_at
                FROM board b
                JOIN users u
                on u.id = b.user_id
                ORDER BY created_at DESC
                LIMIT :size OFFSET :offset
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("size", size);
        params.addValue("offset", offset);
        List<ResponseBoardDto> result
                = template.query(sql, params, (rs, roNum) ->

                ResponseBoardDto.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content"))
                        .userId(rs.getLong("user_id"))
                        .username(rs.getString("username"))
                        .createdAt(

                                rs.getTimestamp("created_at") == null
                                        ? null : rs.getTimestamp("created_at").toLocalDateTime()

                        )
                        .updatedAt(

                                rs.getTimestamp("updated_at") == null
                                        ? null : rs.getTimestamp("updated_at").toLocalDateTime()
                        )

                        .build()


        );

        return result;

    }

    public List<ResponseBoardDto> findByUser(Pageable pageable, Long userId) {

        int size = pageable.getPageSize();
        long offset = pageable.getOffset();


        String sql = """
                
                SELECT 
                b.id, b.title, b.content, u.id as user_id, u.username as username, b.created_at, b.updated_at
                FROM board b
                JOIN users u
                on u.id = b.user_id
                WHERE u.id = :userId
                ORDER BY created_at DESC
                LIMIT :size OFFSET :offset
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("size", size);
        params.addValue("offset", offset);

        List<ResponseBoardDto> result
                = template.query(sql, params, (rs, roNum) ->

                ResponseBoardDto.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content"))
                        .userId(rs.getLong("user_id"))
                        .username(rs.getString("username"))
                        .createdAt(

                                rs.getTimestamp("created_at") == null
                                        ? null : rs.getTimestamp("created_at").toLocalDateTime()

                        )
                        .updatedAt(

                                rs.getTimestamp("updated_at") == null
                                        ? null : rs.getTimestamp("updated_at").toLocalDateTime()
                        )

                        .build()


        );

        return result;
    }

    public List<ResponseBoardDto> findBySearchByLike(Pageable pageable, String keyword) {


        int size = pageable.getPageSize();
        long offset = pageable.getOffset();
        String sql = """
                
                SELECT 
                b.id, b.title, b.content, u.id as user_id, 
                u.username as username, b.created_at, b.updated_at
                FROM board b
                JOIN users u
                on u.id = b.user_id
                WHERE title LIKE :keyword
                ORDER BY created_at DESC
                LIMIT :size OFFSET :offset
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("size", size);
        params.addValue("offset", offset);
        params.addValue("keyword", "%" + keyword + "%");
        List<ResponseBoardDto> result
                = template.query(sql, params, (rs, roNum) ->

                ResponseBoardDto.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content"))
                        .userId(rs.getLong("user_id"))
                        .username(rs.getString("username"))
                        .createdAt(

                                rs.getTimestamp("created_at") == null
                                        ? null : rs.getTimestamp("created_at").toLocalDateTime()

                        )
                        .updatedAt(

                                rs.getTimestamp("updated_at") == null
                                        ? null : rs.getTimestamp("updated_at").toLocalDateTime()
                        )

                        .build()
        );

        return result;

    }

    public List<ResponseBoardDto> findBySearchByMatchAgainst(Pageable pageable, String keyword) {

        int size = pageable.getPageSize();
        long offset = pageable.getOffset();


        String sql = """
                
                SELECT b.id, b.title, b.content, u.id as user_id,
                FROM board b
                JOIN users u
                on u.id = b.user_id
                WHERE MATCH(title)
                AGAINST(:keyword IN BOOLEAN MODE)
                """;

        Map<String, String> param = Map.of("keyword", "+" + keyword + "*");

        List<ResponseBoardDto> result = template.query(sql, param, (rs, roNum) ->

                ResponseBoardDto.builder()
                        .id(rs.getLong("id"))
                        .title(rs.getString("title"))
                        .content(rs.getString("content"))
                        .userId(rs.getLong("user_id"))
                        .build()

        );
        return result;

    }
}