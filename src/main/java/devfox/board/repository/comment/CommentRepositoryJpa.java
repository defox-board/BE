package devfox.board.repository.comment;

import devfox.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepositoryJpa extends JpaRepository<Comment,Long> {

}
