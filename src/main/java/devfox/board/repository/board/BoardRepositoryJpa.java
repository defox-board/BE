package devfox.board.repository.board;

import devfox.board.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepositoryJpa extends JpaRepository<Board,Long> {




}
