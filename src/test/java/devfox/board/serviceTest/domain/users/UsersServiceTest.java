package devfox.board.serviceTest.domain.users;

import devfox.board.dto.request.UserRequestDto;
import devfox.board.entity.UserRole;
import devfox.board.entity.Users;
import devfox.board.repository.users.UserRepository;
import devfox.board.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.BDDMockito.given;



import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;






@ExtendWith(MockitoExtension.class)
public class UsersServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Test
    void ユーザー登録時_既に存在するIDの場合_エラー発生() {

        //given
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");

        given(userRepository.existByUsername(userRequestDto.getUsername()))
                .willReturn(1);

        //when & then
        assertThatThrownBy(() -> userService.addUser(userRequestDto))
                .hasMessageContaining("すでに存在してるユーザーです。");
    }

    @Test
    void ユーザー登録時_DB呼び出し_正常確認() {


        //given
        UserRequestDto userRequestDto = new UserRequestDto();
        userRequestDto.setUsername("username");
        userRequestDto.setEmail("email");
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        given(userRepository.existByUsername(userRequestDto.getUsername()))
                .willReturn(0);

        //when
        userService.addUser(userRequestDto);

        //then
        verify(userRepository, times(1)).save(any(Users.class));
    }

    @Test
    void Users엔티티_정상생성_값확인_argumentCaptor() {
        // given
        UserRequestDto dto = new UserRequestDto();
        dto.setEmail("email");
        dto.setUsername("username");

        ArgumentCaptor<Users> usersCaptor = ArgumentCaptor.forClass(Users.class);
        given(passwordEncoder.encode(any())).willReturn("encodedPassword");
        // when
        userService.addUser(dto);


        // then
        verify(userRepository).save(usersCaptor.capture());
        Users captured = usersCaptor.getValue();

        assertThat(captured.getUsername()).isEqualTo("username");
        assertThat(captured.getEmail()).isEqualTo("email");
        assertThat(captured.getUserRole()).isEqualTo(UserRole.USER);
        assertThat(captured.getIsLock()).isFalse();
        assertThat(captured.getIsSocial()).isFalse();
    }

}
