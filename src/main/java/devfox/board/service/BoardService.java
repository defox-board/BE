package devfox.board.service;

import devfox.board.dto.request.CreateBoardDto;
import devfox.board.dto.response.ResponseBoardDto;
import devfox.board.entity.Board;
import devfox.board.entity.Users;
import devfox.board.repository.BoardRepositoryJpa;
import devfox.board.repository.UserRepository;
import devfox.board.repository.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepositoryJpa boardRepositoryJpa;
    private final UserRepository userRepository;
    private final UserRepositoryJpa userRepositoryJpa;
    /**
     * 掲示板作成処理
     * ・ユーザー名でユーザーを取得
     * ・存在しない場合は例外をスロー
     * ・掲示板エンティティを生成して保存
     */
    public void save(CreateBoardDto dto,String username) {

        Users user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません。"));

        Board build = Board.builder()
                .title(dto.getTitle())
                .userId(user.getId())
                .content(dto.getContent())
                .build();

        boardRepositoryJpa.save(build);
    }
    /**
     * 掲示板更新処理
     * ・掲示板の存在確認
     * ・タイトルと内容を更新
     */
    public void update(CreateBoardDto dto) {
        //작업중

    }

    public void delete(Long boardId,String username) {
            //작업중
    }


    public Page<ResponseBoardDto> getAllBoard(Pageable pageable) {


        Page<Board> all = boardRepositoryJpa.findAll(pageable);
        List<Long> userIdList = all.stream().map(Board::getUserId).toList();

        List<Users> usersList = userRepositoryJpa.findAllByUserIdOfBoard(userIdList);
        Map<Long, Users> usersMap = usersList.stream().collect(Collectors.toMap(
                Users::getId,
                u -> u
        ));

        Page<ResponseBoardDto> result = all.map(ent -> {

            Users users = usersMap.get(ent.getUserId());
            return
                    ResponseBoardDto.builder()
                            .id(ent.getId())
                            .createdAt(ent.getCreatedAt())
                            .updatedAt(ent.getUpdatedAt())
                            .title(ent.getTitle())
                            .content(ent.getContent())
                            .username(users.getUsername())
                            .userId(users.getId())
                            .build();

        });
        return result;

    }


    public void getBoardById(Long boardId) {


    }
}
