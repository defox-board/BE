package devfox.board.service;

import devfox.board.dto.request.CreateBoardDto;
import devfox.board.dto.response.ResponseBoardDetailDto;
import devfox.board.dto.response.ResponseBoardDto;
import devfox.board.entity.Board;
import devfox.users.entity.Users;
import devfox.board.repository.board.BoardRepositoryJpa;
import devfox.users.repository.UserRepository;
import devfox.users.repository.UserRepositoryJpa;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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
    @Transactional
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
     * ・タイトルと内容を変更
     */
    @Transactional
    public void update(CreateBoardDto dto,String username) {

        Board board = boardRepositoryJpa.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("投稿が見つかりません"));

        Users users = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        if (board.getUserId() != users.getId()) {
            throw new IllegalArgumentException("作者のみ削除可能です");
        }
        board.update(dto);
    }
    //　投稿IDを基に該当する投稿を削除
    @Transactional
    public void deleteById(Long boardId,String username) {

        Board board = boardRepositoryJpa.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("存在しない掲示板"));

        Users users = userRepositoryJpa.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("存在しないユーザー"));

        if (board.getUserId() != users.getId()) {
            throw new IllegalArgumentException("作者のみ削除可能です");
        }

        boardRepositoryJpa.delete(board);
    }
    /**
     ** 掲示板の全件取得（ページング処理）
     * 掲示板とユーザー情報の結合が必要
     * SQLのJOINは使用せず、コード上で結合処理を実装
     * 将来的にSQL JOIN方式との性能比較を行う予定
     */
    @Transactional(readOnly = true)
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

    public Page<ResponseBoardDto> getAllBoardBySql(Pageable pageable) {
        // JDBC SQL로 작업 예정
        return null;
    }


    //　掲示板一つ詳細返却
    @Transactional(readOnly = true)
    public ResponseBoardDetailDto getBoardById(Long boardId) {

        Board board = boardRepositoryJpa.findById(boardId)
                .orElseThrow(() -> new EntityNotFoundException("投稿が見つかりません"));

        Users users = userRepositoryJpa.findById(board.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("ユーザーを見つかりません"));

        return ResponseBoardDetailDto.builder()
                .id(board.getId())
                .content(board.getContent())
                .title(board.getTitle())
                .username(users.getUsername())
                .createdAt(board.getCreatedAt())
                .updatedAt(board.getUpdatedAt())
                .build();
    }
}
