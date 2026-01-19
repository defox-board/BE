package devfox.board.entity;

import devfox.board.dto.request.CreateComment;
import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Entity
@Table(name = "comment")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content")
    private String content;
    @Column(name = "board_id")
    private Long boardId;
    @Column(name = "user_id")
    private Long userId;

    public void update(CreateComment dto) {

        if (dto.getContent() != null) {
            this.content = dto.getContent();
        }
    }
}
