package devfox.board.exception;


import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ErrorResponse {

    private final int status;
    private final String messages;
    private final LocalDateTime timestamp;

    public ErrorResponse(int status, String messages) {

        this.status = status;
        this.messages = messages;
        this.timestamp = LocalDateTime.now();

    }
}
