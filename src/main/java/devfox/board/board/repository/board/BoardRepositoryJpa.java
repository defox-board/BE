package devfox.board.board.repository.board;

import devfox.board.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepositoryJpa extends JpaRepository<Board,Long> {




}
