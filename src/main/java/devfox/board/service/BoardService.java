package devfox.board.service;

import devfox.board.dto.request.CreateBoardDto;
import devfox.board.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;


    public void save(CreateBoardDto dto) {

        Long userId = 1L;
        boardRepository.save(userId,dto);

    }
}
