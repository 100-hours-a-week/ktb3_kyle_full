package com.kyle.week4.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    INVALID_EMAIL(401, HttpStatus.UNAUTHORIZED, "이메일이 일치하지 않습니다."),
    INVALID_PASSWORD(401, HttpStatus.UNAUTHORIZED, "비밀번호가 일치하지 않습니다."),
    FAILED_AUTHENTICATE(401, HttpStatus.UNAUTHORIZED, "인증이 만료되었거나, 인증에 실패했습니다."),
    PERMISSION_DENIED(403, HttpStatus.FORBIDDEN, "권한이 존재하지 않습니다."),
    NOT_FOUND_END_POINT(404, HttpStatus.NOT_FOUND, "존재하지 않는 API입니다."),
    USER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 사용자입니다."),
    POST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 게시글입니다."),
    COMMENT_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),
    POST_LIKE_NOT_FOUND(404, HttpStatus.NOT_FOUND, "좋아요를 누르지 않았습니다."),
    DUPLICATE_EMAIL_ERROR(409, HttpStatus.CONFLICT, "이미 가입된 이메일입니다."),
    DUPLICATE_NICKNAME_ERROR(409, HttpStatus.CONFLICT, "이미 가입된 닉네임입니다."),
    ALREADY_LIKED_ERROR(409, HttpStatus.CONFLICT, "이미 좋아요를 눌렀습니다."),
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류입니다.");

    private final Integer code;
    private final HttpStatus httpStatus;
    private final String message;
}
