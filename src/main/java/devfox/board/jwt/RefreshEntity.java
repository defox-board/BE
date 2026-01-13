package devfox.board.jwt;

import devfox.board.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "jwt_refresh_entity",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_username_refresh",
                        columnNames = {"username", "refresh"}
                )

        })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@AllArgsConstructor

public class RefreshEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "refresh", nullable = false)
    private String refresh;


}
