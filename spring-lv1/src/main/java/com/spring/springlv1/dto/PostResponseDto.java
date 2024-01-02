package com.spring.springlv1.dto;

import com.spring.springlv1.entity.Post;
import lombok.Getter;

@Getter
public class PostResponseDto {
    private Long id;
    private String title;
    private String author;
    private String contents;
    private String date;

    public PostResponseDto(Long id, String title, String author, String contents, String date) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.contents = contents;
        this.date = date;
    }

    public PostResponseDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.author = post.getAuthor();
        this.contents = post.getContents();
        this.date = post.getDate();
    }
}
