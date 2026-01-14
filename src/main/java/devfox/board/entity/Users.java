package devfox.board.entity;

import devfox.board.dto.request.UserRequestDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Table(name = "users")
@Entity
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Users extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "username",unique = true,nullable = false)
    private String username;


    @Column(name = "password")
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "social_provider_type")
    private SocialProviderType socialProviderType;

    @Column(name = "is_social", nullable = false)
    private Boolean isSocial;

    @Column(name = "is_lock")
    private Boolean isLock;

    @Enumerated(EnumType.STRING)
    @Column(name = "role",nullable = false)
    private UserRole userRole;

    private String email;

    @Column(name = "profile_image_key")
    private String profileImageKey;


    public void updateUser(UserRequestDto dto) {
        if (dto.getEmail() != null && !dto.getEmail().isEmpty()) {
            this.email = dto.getEmail();
        }
        }
}
