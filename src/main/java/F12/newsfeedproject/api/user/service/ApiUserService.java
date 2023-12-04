package F12.newsfeedproject.api.user.service;

import F12.newsfeedproject.api.user.dto.request.UserModifyRequestDTO;
import F12.newsfeedproject.api.user.dto.request.UserSignupRequestDTO;
import F12.newsfeedproject.api.user.dto.response.UserResponseDTO;
import F12.newsfeedproject.domain.user.entity.User;
import F12.newsfeedproject.domain.user.service.UserService;
import F12.newsfeedproject.global.exception.common.ErrorCode;
import F12.newsfeedproject.global.exception.jwt.NotMisMatchedRefreshTokenException;
import F12.newsfeedproject.global.exception.jwt.NotRefreshTokenException;
import F12.newsfeedproject.global.exception.member.AlreadyUserExistException;
import F12.newsfeedproject.global.exception.user.UserNotFoundException;
import F12.newsfeedproject.global.jwt.JwtManager;
import F12.newsfeedproject.global.jwt.TokenType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApiUserService {

    private final UserService userService;

    private final JwtManager jwtManager;

    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO signupUser(UserSignupRequestDTO userSignupRequestDTO) {

        isDuplicateUser(userSignupRequestDTO);
        User savedUser = userService.saveUser(userSignupRequestDTO.toEntity(passwordEncoder));

        return UserResponseDTO.from(savedUser);
    }

    private void isDuplicateUser(UserSignupRequestDTO userSignupRequestDTO) {

        userService.findByUserName(userSignupRequestDTO.userName()).ifPresent(user -> {
            throw new AlreadyUserExistException(ErrorCode.ALREADY_EXIST_USER_NAME_EXCEPTION);
        });

        userService.findByUserEmail(userSignupRequestDTO.userEmail()).ifPresent(user -> {
            throw new AlreadyUserExistException(ErrorCode.ALREADY_EXIST_EMAIL_EXCEPTION);
        });
    }

    @Transactional
    public UserResponseDTO updateUser(UserModifyRequestDTO userModifyRequestDTO, Long userId) {
        User findUser = userService.findByUserId(userId).orElseThrow(UserNotFoundException::new);
        userService.updateUser(findUser, userModifyRequestDTO.toEntity());

        return UserResponseDTO.from(findUser);
    }

    public String reissueAccessToken(String refreshToken) {

        String userName = jwtManager.getUserNameFromToken(refreshToken);

        validateRefreshToken(refreshToken, userName);

        return jwtManager.createAccessToken(userName);
    }

    private void validateRefreshToken(String refreshToken, String userName) {

        if (!isRefreshToken(refreshToken)) {
            throw new NotRefreshTokenException();
        }

        if (!isRightRefreshToken(refreshToken, userName)) {
            throw new NotMisMatchedRefreshTokenException();
        }
    }

    private boolean isRefreshToken(String refreshToken) {

        String tokenType = jwtManager.getTokenTypeFromToken(refreshToken);

        return TokenType.REFRESH.toString().equals(tokenType);
    }

    private boolean isRightRefreshToken(String refreshToken, String userName) {

        User findUser = userService.findByUserName(userName)
            .orElseThrow(UserNotFoundException::new);

        return findUser.getRefreshToken().equals(refreshToken);
    }

    public void logoutUser(Long userId) {
        userService.logoutUser(userId);
    }
}
