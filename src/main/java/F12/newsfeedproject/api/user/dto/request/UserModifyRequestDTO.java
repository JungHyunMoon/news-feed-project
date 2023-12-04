package F12.newsfeedproject.api.user.dto.request;

import F12.newsfeedproject.domain.user.entity.User;
import jakarta.validation.constraints.NotBlank;

public record UserModifyRequestDTO(
    @NotBlank(message = "프로필 사진 URL은 필수 입력 값입니다.") String userImageUrl,
    @NotBlank(message = "자기소개는 필수 입력 값입니다.") String userIntroduce
) {

    public User toEntity() {
        return User.builder()
            .userImageUrl(userImageUrl)
            .userIntroduce(userIntroduce)
            .build();
    }
}
