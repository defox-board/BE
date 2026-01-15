package devfox.board.repository;

import devfox.board.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepositoryJpa extends JpaRepository<Users,Long> {


    @Query("""
            SELECT u
            FROM Users u
            WHERE u.id in :userIdList
            """)
    List<Users> findAllByUserIdOfBoard(@Param("userIdList") List<Long> userIdList);


    @Query("""
            SELECT u
            FROM Users u
            WHERE u.username = :username
            """)
    Optional<Users> findByUsername(@Param("username") String username);

}
