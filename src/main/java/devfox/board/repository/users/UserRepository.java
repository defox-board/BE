package devfox.board.repository.users;

import devfox.board.dto.request.UserRequestDto;
import devfox.board.entity.UserRole;
import devfox.board.entity.Users;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate template;


    public int existByUsername(String username) {

        String sql = """
                SELECT 
                CASE WHEN
                COUNT(*) > 0 THEN 1
                ELSE 0
                END
                FROM users u
                WHERE u.username = :username
                """;
        Map<String, String> param = Map.of("username", username);
       return template.queryForObject(sql, param, Integer.class);

    }

    public long save(Users users) {

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = """
                INSERT INTO users 
                (username,password,email,is_lock,is_social,role,created_at)
                VALUES(
                :username,
                :password,
                :email,
                :is_lock,
                :is_social,
                :role,
                now()
                )
                """;
        MapSqlParameterSource params = new MapSqlParameterSource();

        params.addValue("username", users.getUsername());
        params.addValue("password", users.getPassword());
        params.addValue("email", users.getEmail());
        params.addValue("is_lock", users.getIsLock());
        params.addValue("is_social", users.getIsSocial());
        params.addValue("role", users.getUserRole().name());

        template.update(sql, params, keyHolder, new String[]{"id"});
        return  keyHolder.getKey().longValue();


    }

    public Optional<Users> findByUsernameAndIsLockAndIsSocial
            (String username, boolean isLock, boolean isSocial) {


        String sql = """
                SELECT 
                id, username, email, is_lock, is_social, role, password
                                            FROM users
                                            WHERE username = :username
                                              AND is_lock = :isLock
                                              AND is_social = :isSocial
                                            LIMIT 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        params.addValue("isLock", isLock);
        params.addValue("isSocial", isSocial);

        List<Users> list = template.query(sql, params, (rs, roNum) ->

                Users.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getNString("username"))
                        .email(rs.getNString("email"))
                        .isLock(rs.getBoolean("is_lock"))
                        .isSocial(rs.getBoolean("is_social"))
                        .userRole(UserRole.valueOf(rs.getNString("role")))
                        .password(rs.getNString("password"))
                        .build()
        );

        return list.stream().findFirst();

    }


    public Optional<Users> findByUsernameAndIsLock
            (String username, boolean isLock) {


        String sql = """
                SELECT id, username, email, is_lock, is_social, role
                                            FROM users
                                            WHERE username = :username
                                              AND is_lock = :isLock
                                            LIMIT 1
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("username", username);
        params.addValue("is_lock", isLock);

        List<Users> list = template.query(sql, params, (rs, roNum) ->

                Users.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getNString("username"))
                        .email(rs.getNString("email"))
                        .isLock(rs.getBoolean("is_lock"))
                        .userRole(UserRole.valueOf(rs.getNString("role")))
                        .build()


        );
        return list.stream().findFirst();

    }



    public int updateUser(UserRequestDto dto, Long id) {

        String sql = """
                
                UPDATE users
                SET
                email = COALESCE(:email, email)
                WHERE u.id = :id
                """;

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("email", dto.getEmail());
        params.addValue("id", id);
        int result = template.update(sql, params);
        return result;

    }

    public int deleteByUsername(String username) {

        String sql = """
                
                DELETE 
                FROM users
                WHERE users.username = :username
                """;
        Map<String, String> param = Map.of("username", username);

        int result = template.update(sql, param);
        return result;

    }

    public Optional<Users> findByUsername(String username) {

        String sql = """
                
                SELECT *
                FROM users u
                WHERE u.username = :username
                """;

        Map<String, String> param = Map.of("username", username);
        Optional<Users> result = template.query(sql, param, (rs, roNum) ->

                Users.builder()
                        .id(rs.getLong("id"))
                        .username(rs.getNString("username"))
                        .email(rs.getNString("email"))
                        .userRole(UserRole.valueOf(rs.getNString("role")))
                        .build()

        ).stream().findFirst();
        return result;
    }
}
