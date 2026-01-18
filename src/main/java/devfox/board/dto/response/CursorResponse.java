package devfox.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CursorResponse<T> {

    private T data;
    private boolean hasNext;
    private Long nextCursor;
}
