package devfox.board.board.repository.comment;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import devfox.board.board.dto.response.CommentDto;
import devfox.board.board.entity.QComment;
import devfox.board.users.entity.QUsers;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class CommentRepositoryQueryImpl implements CommentRepositoryQuery {


    private final JPAQueryFactory queryFactory;

    @Override
    public List<CommentDto> findByBoardId(Long boardId, Long cursorId, int size) {

        QComment comment = QComment.comment;
        QUsers users = QUsers.users;

        List<CommentDto> result = queryFactory
                .select(
                        Projections.constructor(
                                CommentDto.class,
                                comment.id,
                                comment.content,
                                users.username,
                                comment.createdAt,
                                comment.updatedAt
                        )
                )
                .from(comment)
                .join(users).on(users.id.eq(comment.userId))
                .where(
                        comment.boardId.eq(boardId),
                        cursorId == null ? null : comment.id.lt(cursorId)
                ).orderBy(comment.id.desc())
                .limit(size + 1)
                .fetch();

        return result;

    }


}
