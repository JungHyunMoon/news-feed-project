package F12.newsfeedproject.api.user.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import F12.newsfeedproject.api.user.dto.request.UserModifyRequestDTO;
import F12.newsfeedproject.api.user.dto.request.UserSignupRequestDTO;
import F12.newsfeedproject.api.user.dto.response.UserResponseDTO;
import F12.newsfeedproject.domain.user.constant.UserRole;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.domain.user.service.UserService;
import F12.newsfeedproject.global.exception.common.BusinessException;
import F12.newsfeedproject.global.exception.common.ErrorCode;
import F12.newsfeedproject.global.jwt.JwtManager;
import F12.newsfeedproject.global.jwt.TokenType;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Nested
@ExtendWith(MockitoExtension.class)
class ApiUserServiceTest {

    @Mock
    UserService userService;
    @Mock
    JwtManager jwtManager;
    @Mock
    private ApiUserService apiUserService;
    private User user;

    @BeforeEach
    void setUp() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        this.user = new User(1L, "testuser", passwordEncoder.encode("password"),
            "user@useremail.com", "userimageurl.com",
            "helloworld", UserRole.USER, null);

    }

    @Test
    @DisplayName("user : 사용자 회원가입 테스트")
    void signUpTest() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Given
        UserSignupRequestDTO userSignupRequestDTO = new UserSignupRequestDTO("testuser",
            passwordEncoder.encode("password"), "useremail@email.com", "userimageurl.com",
            "introduce");
        UserResponseDTO userResponseDTO = UserResponseDTO.from(
            userSignupRequestDTO.toEntity(passwordEncoder));

        // When
        when(apiUserService.signupUser(userSignupRequestDTO)).thenReturn(
            UserResponseDTO.from(userSignupRequestDTO.toEntity(passwordEncoder)));
        UserResponseDTO result = apiUserService.signupUser(userSignupRequestDTO);

        // Then
        assertEquals(userResponseDTO, result);
    }

    @Test
    @DisplayName("user : 중복 유저 예외처리 테스트")
    void isDuplicateUser() {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // Given
        UserSignupRequestDTO userSignupRequestDTO1 = new UserSignupRequestDTO("testuser",
            passwordEncoder.encode("password"), "useremail@email.com", "userimageurl.com",
            "introduce");
        UserSignupRequestDTO userSignupRequestDTO2 = new UserSignupRequestDTO("testuser",
            passwordEncoder.encode("password"), "useremail@email.com", "userimageurl.com",
            "introduce");
        apiUserService.signupUser(userSignupRequestDTO1);

        // When
        when(apiUserService.signupUser(userSignupRequestDTO2)).thenThrow(
            new BusinessException(ErrorCode.ALREADY_EXIST_USER_NAME_EXCEPTION));

        // Then
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            apiUserService.signupUser(userSignupRequestDTO2);
        });
        Assertions.assertEquals(exception.getMessage(),
            ErrorCode.ALREADY_EXIST_USER_NAME_EXCEPTION.getMessage());
    }

    @Test
    @DisplayName("user : 유저 정보 수정 테스트")
    void updateUser() {
        // Given
        UserModifyRequestDTO userModifyRequestDTO = new UserModifyRequestDTO("newimageurl.com",
            "newintroduce");
        User modifyUser = userModifyRequestDTO.toEntity();
        this.user.updateUser(modifyUser);
        UserResponseDTO userResponseDTO = UserResponseDTO.from(this.user);

        // When
        when(apiUserService.updateUser(userModifyRequestDTO, 1L)).thenReturn(userResponseDTO);

        // Then
        UserResponseDTO result = apiUserService.updateUser(userModifyRequestDTO, 1L);

        assertEquals(result, userResponseDTO);
    }

    @Test
    @DisplayName("리프레쉬 토큰을 통하여 액세스 토큰을 재발급 받는다.")
    void reissueAccessToken() {
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        //given
        User loginUser = (User) authentication.getPrincipal();
        String refreshToken = loginUser.getRefreshToken();

        given(jwtManager.getUserNameFromToken(any())).willReturn(loginUser.getUserName());
        given(jwtManager.getTokenTypeFromToken(refreshToken)).willReturn(
            TokenType.REFRESH.toString());
        given(userService.findByUserName(loginUser.getUserName())).willReturn(
            Optional.of(loginUser));

        // when
        String accessToken = apiUserService.reissueAccessToken(refreshToken);

        // then
        String userNameFromRefreshToken = jwtManager.getUserNameFromToken(refreshToken);
        String userNameFromNewAccessToken = jwtManager.getUserNameFromToken(accessToken);

        assertEquals(userNameFromRefreshToken, userNameFromNewAccessToken);
    }

    @Test
    @DisplayName("회원 로그아웃 테스트")
    void logoutUser() {
        //given
        Long userId = 1L;

        // when
        apiUserService.logoutUser(userId);

        // then
        verify(userService).logoutUser(userId);
    }
}

