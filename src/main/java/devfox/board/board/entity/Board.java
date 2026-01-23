package devfox.board.board.entity;

import devfox.board.board.dto.request.CreateBoardDto;
import devfox.board.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "board")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class Board extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "content")
    private String content;
    @Column(name = "title")
    private String title;
    @Column(name = "user_id")
    private Long userId;



    /*
    掲示板修正 Method
     */
    public void update(CreateBoardDto dto) {

        if (dto.getContent() != null) {
            this.content = dto.getContent();
        }

        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }

    }
}
