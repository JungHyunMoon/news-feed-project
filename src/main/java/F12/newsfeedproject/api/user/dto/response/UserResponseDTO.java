package F12.newsfeedproject.api.user.dto.response;

import F12.newsfeedproject.domain.user.entity.User;

public record UserResponseDTO(
    Long userId,
    String userName,
    String userEmail,
    String userImageUrl,
    String userIntroduce
) {

    public static UserResponseDTO from(User user) {
        return new UserResponseDTO(
            user.getUserId(),
            user.getUserName(),
            user.getUserEmail(),
            user.getUserImageUrl(),
            user.getUserIntroduce());
    }
}
