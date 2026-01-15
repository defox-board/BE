package devfox.board.entity;

import devfox.board.dto.request.CreateBoardDto;
import jakarta.persistence.*;
import lombok.*;

@Table(name = "board")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Builder
public class Board extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;
    @Column(name = "content")
    private String content;
    @Column(name = "title")
    private String title;
    @Column(name = "user_id")
    private Long userId;


    public void update(CreateBoardDto dto) {

        if (dto.getContent() != null) {
            this.content = dto.getContent();
        }

        if (dto.getTitle() != null) {
            this.title = dto.getTitle();
        }

    }
}
