package com.keisenpai.authservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Кастомное исключение для обработки случаев, когда login уже зарегистрирован.
@ResponseStatus(HttpStatus.CONFLICT)
public class LoginAlreadyExistsException extends RuntimeException {

    public LoginAlreadyExistsException(String message) {
        super(message);
    }
}
