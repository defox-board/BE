package devfox.board.repository.comment;

import devfox.board.dto.response.ResponseComment;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class CommentRepository {


    private final NamedParameterJdbcTemplate template;


}
