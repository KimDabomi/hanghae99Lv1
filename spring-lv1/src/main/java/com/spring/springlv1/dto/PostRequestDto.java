package com.spring.springlv1.dto;

import lombok.Getter;

@Getter
public class PostRequestDto {
    private String title;
    private String author;
    private String contents;
    private String date;
    private String password;
}
