package com.example.demo.dto.exception;

import com.example.demo.type.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResult {
    ErrorCode Code;
    String message;
}
