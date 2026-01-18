package devfox.board.repository.comment;

import devfox.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment,Long> , CommentRepositoryQuery{
}
