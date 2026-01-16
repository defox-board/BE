package devfox.board.service;

import devfox.board.dto.request.UserRequestDto;
import devfox.board.dto.response.UserResponseDto;
import devfox.board.entity.UserRole;
import devfox.board.entity.Users;
import devfox.board.jwt.JwtService;
import devfox.board.repository.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
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
}
