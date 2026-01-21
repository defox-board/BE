package devfox.board.board.repository.comment;

import devfox.board.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepositoryJpa extends JpaRepository<Comment,Long> {

}
