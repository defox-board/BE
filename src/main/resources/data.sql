
--댓글 PROCEDURE

DELIMITER $$

CREATE PROCEDURE insert_comments(IN total INT)
BEGIN
  DECLARE i INT DEFAULT 1;

  WHILE i <= total DO
    INSERT INTO comment (content, board_id, user_id, created_at)
    VALUES (
      CONCAT('테스트 댓글 ', i),
      1,
      1,
      NOW()
    );

    SET i = i + 1;
  END WHILE;
END$$

DELIMITER ;

--게시글 PROCEDURE


DELIMITER $$

CREATE PROCEDURE insert_board(IN total INT)
BEGIN
  DECLARE i INT DEFAULT 1;

  WHILE i <= total DO
    INSERT INTO board (title, content, user_id, created_at)
    VALUES (
      CONCAT('테스트 게시글 제목', i),
      CONCAT('테스트 게시글 내용 ', i),
      2,
      NOW()
    );

    SET i = i + 1;
  END WHILE;
END$$

DELIMITER ;