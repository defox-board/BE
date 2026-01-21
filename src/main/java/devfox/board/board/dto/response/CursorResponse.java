package devfox.board.board.dto.response;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@AllArgsConstructor
@Builder
@Getter
public class CursorResponse<T> {

    @Schema(description = "実際のデータ")
    private T data;
    @Schema(description = "この後データがあるかないか")
    private boolean hasNext;
    @Schema(description = "次にリクエストするデイタのID")
    private Long nextCursor;
}
