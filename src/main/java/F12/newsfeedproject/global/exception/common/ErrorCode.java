package F12.newsfeedproject.global.exception.common;

import lombok.Getter;

@Getter
public enum ErrorCode {

  // JWT
  INVALID_JWT_SIGNATURE_EXCEPTION(401, "잘못된 JWT 서명입니다."),
  EXPIRED_JWT_TOKEN_EXCEPTION(401, "만료된 JWT 토큰입니다."),
  UNSUPPORTED_JWT_TOKEN_EXCEPTION(401, "지원되지 않는 JWT 토큰입니다."),
  INVALID_JWT_TOKEN_EXCEPTION(401, "JWT 토큰이 잘못되었습니다"),
  NOT_REFRESH_TOKEN_EXCEPTION(401, "Refresh Token이 아닙니다."),
  NOT_MISMATCHED_REFRESH_TOKEN_EXCEPTION(401, "DB의 리프레쉬 토큰 값과 다릅니다."),

  // 회원
  NOT_FOUND_MEMBER_EXCEPTION(401, "회원 정보를 찾을 수 없습니다."),
  FAILED_AUTHENTICATION_EXCEPTION(401, "인증에 실패하였습니다."), 
  ALREADY_EXIST_USER_NAME_EXCEPTION(409, "이미 존재하는 이름입니다."),
  ALREADY_EXIST_EMAIL_EXCEPTION(409, "이미 존재하는 이메일입니다."),
  UNAUTHORIZED_MODIFY_EXCEPTION(401, "수정할 권한이 없습니다."),

  // follow
  NOT_FOUND_FOLLOW_EXCEPTION(401, "팔로우 내역을 찾을 수 없습니다."),
  DUPLICATED_FOLLOW_EXCEPTION(401, "팔로우 내역이 이미 존재합니다."),
  
  // 게시글
  BOARD_NOT_FOUND_EXCEPTION(401, "게시글을 찾을 수 없습니다.");

  
  private final int status;

  private final String message;

  ErrorCode(int status, String message) {
    this.status = status;
    this.message = message;
  }
}
