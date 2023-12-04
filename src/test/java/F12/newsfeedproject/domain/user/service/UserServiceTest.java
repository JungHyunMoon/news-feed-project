package F12.newsfeedproject.domain.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import F12.newsfeedproject.domain.user.constant.UserRole;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserService userService;

    private User user;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.user = new User(1L, "testuser", passwordEncoder.encode("password"), "user@useremail.com", "userimageurl.com",
            "helloworld", UserRole.USER, null);
    }

    @Test
    @DisplayName("User-service : 회원가입 테스트")
    void test1() {
        // given
        given(userService.saveUser(user)).willReturn(user);

        // when
        User result = userService.saveUser(user);

        // then
        assertEquals(user, result);
    }

    @Test
    @DisplayName("User-service : 사용자 이름으로 조회 테스트")
    void test2() {

        String userName = "testuser";

        // given
        given(userService.findByUserName(userName)).willReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findByUserName(userName);

        // then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }
    @Test
    @DisplayName("User-service : 사용자 이메일로 조회 테스트")
    void test3() {

        String useremail = "user@useremail.com";

        // given
        given(userService.findByUserEmail(useremail)).willReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findByUserEmail(useremail);

        // then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("User-service : 사용자 ID로 조회 테스트")
    void test4() {

        Long userId = 1L;

        // given
        given(userService.findByUserId(userId)).willReturn(Optional.of(user));

        // when
        Optional<User> result = userService.findByUserId(userId);

        // then
        assertTrue(result.isPresent());
        assertEquals(user, result.get());
    }

    @Test
    @DisplayName("User-service : 사용자 정보 수정 테스트")
    void test5() {
        // given
        User findUser = this.user;

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User modifiedUser = User.builder()
            .userEmail("modifyUser")
            .userPassword(passwordEncoder.encode("modifyPassword"))
            .userEmail("modifyEmail@email.com")
            .userIntroduce("modifyIntroduce")
            .userRole(UserRole.USER)
            .build();

        // when
        findUser.updateUser(modifiedUser);

        // then
        assertEquals(findUser.getUserImageUrl(), modifiedUser.getUserImageUrl());
        assertEquals(findUser.getUserIntroduce(), modifiedUser.getUserIntroduce());
    }
}