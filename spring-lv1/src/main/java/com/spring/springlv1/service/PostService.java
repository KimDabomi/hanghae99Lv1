package com.spring.springlv1.service;

import com.spring.springlv1.dto.PostRequestDto;
import com.spring.springlv1.dto.PostResponseDto;
import com.spring.springlv1.entity.Post;
import com.spring.springlv1.repository.PostRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Supplier;

@Component
public class PostService {
    private static final String POST_PASSWORD_ERROR_MESSAGE = "비밀번호가 일치하지 않습니다.";
    private final PostRepository postRepository;
    private final JdbcTemplate jdbcTemplate;

    public PostService(PostRepository postRepository, JdbcTemplate jdbcTemplate) {
        this.postRepository = postRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public PostResponseDto createPost(PostRequestDto requestDto) {
        Post post = new Post(requestDto);
        Post savePost = postRepository.save(post);
        return new PostResponseDto(savePost);
    }

    public List<PostResponseDto> getPosts() {
        return postRepository.findAll();
    }

    public PostResponseDto getPost(Long id) {
        return postRepository.find(id);
    }

    public Long updatePost(Long id, PostRequestDto requestDto) {
        return executeIfPasswordMatches(id, requestDto.getPassword(), () -> {
            postRepository.update(id,requestDto);
            return id;
        });
    }

    public Long deletePost(Long id, PostRequestDto requestDto) {
        return executeIfPasswordMatches(id, requestDto.getPassword(), () -> {
            postRepository.delete(id);
            return id;
        });
    }

    private boolean checkPassword(Long id, String password) {
        String checkPasswordSql = "SELECT post_password FROM POST WHERE post_id = ?";
        List<String> passwords = jdbcTemplate.query(checkPasswordSql, new Long[]{id}, (rs, rowNum) -> rs.getString(1));
        return passwords.isEmpty() || !passwords.get(0).equals(password);
    }

    private Long executeIfPasswordMatches(Long id, String password, Supplier<Long> action) {
        if (checkPassword(id, password)) {
            throw new IllegalArgumentException(POST_PASSWORD_ERROR_MESSAGE);
        }
        return action.get();
    }
}
