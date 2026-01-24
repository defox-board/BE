CREATE TABLE IF NOT EXISTS users(

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 username VARCHAR(255) NOT NULL,
 password VARCHAR(255) NOT NULL,
 email VARCHAR(255),
 is_social TINYINT(1),
 is_lock TINYINT(1),
 user_role VARCHAR(20),
 social_provider_type VARCHAR(50),
 profile_image_key VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS board(
 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 content text,
 title VARCHAR(255),
 user_id BIGINT,
 created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
 updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

 FOREIGN KEY (user_id)
 REFERENCES users(id)
);


--create index idx_board_user_created on board(user_id,created_at);
--create index idx_board_created_at on board(created_at desc);


CREATE TABLE IF NOT EXISTS comment (

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 content text,
 board_id BIGINT,
 user_id BIGINT,
 created_at DATETIME DEFAULT CURRENT_TIMESTAMP ,
 updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

 FOREIGN KEY (user_id)
 REFERENCES users(id),

 FOREIGN KEY (board_id)
 REFERENCES board(id)
);

CREATE TABLE IF NOT EXISTS jwt_refresh_entity(

 id BIGINT AUTO_INCREMENT PRIMARY KEY,
 username VARCHAR(255) NOT NULL,
 refresh VARCHAR(255), NOT NULL,
 CONSTRAINT uk_username_refresh UNIQUE (username,refresh)
);



create index idx_board_user_created on board(user_id,created_at);
create index idx_board_created_at on board(created_at desc);
create fulltext index idx_board_title on board(title);