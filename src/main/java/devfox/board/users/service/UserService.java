package devfox.board.users.service;

import devfox.board.users.dto.UserRequestDto;
import devfox.board.security.dto.CustomOAuth2User;
import devfox.board.users.dto.UserResponseDto;
import devfox.board.users.entity.SocialProviderType;
import devfox.board.users.entity.UserRole;
import devfox.board.users.entity.Users;
import devfox.board.users.repository.UserRepository;
import devfox.board.security.jwt.JwtService;
import devfox.board.users.repository.UserRepositoryJpa;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService extends DefaultOAuth2UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserRepositoryJpa userRepositoryJpa;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    //ユーザー存在有無確認
    @Transactional(readOnly = true)
    public Boolean existUser(UserRequestDto dto) {

        if (userRepository.existByUsername(dto.getUsername()) == 1) {
            return true;
        } else return false;
    }

    //ユーザー登録、登録前にusernameがすでにデータベースにあるか確認
    @Transactional
    public Long addUser(UserRequestDto dto) {

        if (userRepository.existByUsername(dto.getUsername()) == 1) {
            throw new IllegalStateException("すでに存在してるユーザーです。");
        }


        Users users = Users.builder()
                .email(dto.getEmail())
                .userRole(UserRole.USER)
                .password(passwordEncoder.encode(dto.getPassword()))
                .username(dto.getUsername())
                .isSocial(false)
                .isLock(false)
                .build();

        Long result = userRepository.save(users);
        if (result == null) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }
        return result;
    }

    // ユーザー情報更新
    // 本人のみが自分の情報を修正可能
    @Transactional
    public void updateUser(UserRequestDto dto, String username) throws AccessDeniedException {

        if (!username.equals(dto.getUsername())) {
            throw new AccessDeniedException("本人のアカウントのみ修正可能");
        }

        Users users = userRepository.findByUsernameAndIsLockAndIsSocial
                        (dto.getUsername(), false, false)
                .orElseThrow(() -> new UsernameNotFoundException(dto.getUsername()));


        int result = userRepository.updateUser(dto, users.getId());
        if (result == 0) {
            throw new RuntimeException("サーバーエラーが発生しました。");
        }


    }

    // Spring Security 認証用ユーザー取得
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {


        Users users = userRepository.findByUsernameAndIsLockAndIsSocial
                        (username, false, false)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        return User.builder()
                .username(username)
                .password(users.getPassword())
                .roles(users.getUserRole().name())
                .accountLocked(users.getIsLock())
                .build();

    }

    // ユーザー削除
    // 本人のみ削除可能、削除後 refreshToken も削除
    @Transactional
    public void deleteUser(UserRequestDto dto, String username) throws AccessDeniedException {

        if (!(dto.getUsername().equals(username))) {
            throw new AccessDeniedException("本人のみ削除可能");
        }

        int result = userRepository.deleteByUsername(username);
        if (result == 0) {
            throw new RuntimeException("サーバーエラー発生");
        }
        //ユーザーのrefreshToken削除
        jwtService.deleteRefreshByUsername(dto.getUsername());
    }

    // ユーザー情報取得
    // マイページなどで使用するユーザー基本情報取得
    @Transactional(readOnly = true)
    public UserResponseDto getUserInfo(String username) {

        Users users = userRepository.findByUsernameAndIsLock(username, false)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーの情報が見つけないです"));

        return new UserResponseDto(username, users.getIsSocial(), users.getEmail());
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {


        // 부모 메소드 호출
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // 데이터
        Map<String, Object> attributes;
        List<GrantedAuthority> authorities;

        String username;
        String role = UserRole.USER.name();
        String email;
        String nickname;

        // provider 제공자별 데이터 획득
        String registrationId = userRequest.getClientRegistration().getRegistrationId().toUpperCase();
        if (registrationId.equals(SocialProviderType.NAVER.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            username = registrationId + "_" + attributes.get("id");
            email = attributes.get("email").toString();

        } else if (registrationId.equals(SocialProviderType.GOOGLE.name())) {

            attributes = (Map<String, Object>) oAuth2User.getAttributes();
            username = registrationId + "_" + attributes.get("sub");
            email = attributes.get("email").toString();

        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }

        // 데이터베이스 조회 -> 존재하면 업데이트, 없으면 신규 가입
        Optional<Users> entity = userRepository.findByUsernameAndIsSocial(username, true);
        if (entity.isPresent()) {
            // role 조회
            role = entity.get().getUserRole().name();

            // 기존 유저 업데이트
            UserRequestDto dto = new UserRequestDto();
            dto.setEmail(email);
            entity.get().updateUser(dto);

            userRepositoryJpa.save(entity.get());
        } else {
            // 신규 유저 추가
            Users newUserEntity = Users.builder()
                    .username(username)
                    .password("")
                    .isLock(false)
                    .isSocial(true)
                    .socialProviderType(SocialProviderType.valueOf(registrationId))
                    .userRole(UserRole.USER)
                    .email(email)
                    .build();

            userRepositoryJpa.save(newUserEntity);
        }

        authorities = List.of(new SimpleGrantedAuthority(role));

        return new CustomOAuth2User(attributes, authorities, username);

    }
}