package com.spring.springlv1.controller;

import com.spring.springlv1.dto.PostRequestDto;
import com.spring.springlv1.dto.PostResponseDto;
import com.spring.springlv1.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

@RestController
@RequestMapping("/api")
public class PostController {
    private final JdbcTemplate jdbcTemplate;

    public PostController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        Post post = new Post(requestDto);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO POST (post_title, post_author, post_contents, post_date, post_password) VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(con -> {
                    PreparedStatement preparedStatement = con.prepareStatement(sql,
                            Statement.RETURN_GENERATED_KEYS);

                    preparedStatement.setString(1, post.getTitle());
                    preparedStatement.setString(2, post.getAuthor());
                    preparedStatement.setString(3, post.getContents());
                    preparedStatement.setString(4, post.getDate());
                    preparedStatement.setString(5, post.getPassword());

                    return preparedStatement;
                },
                keyHolder);
        Long maxId = Objects.requireNonNull(keyHolder.getKey()).longValue();
        post.setId(maxId);

        return new PostResponseDto(post);
    }

    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        String sql = "SELECT * FROM POST ORDER BY post_date DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Long id = rs.getLong("post_id");
            String title = rs.getString("post_title");
            String author = rs.getString("post_author");
            String contents = rs.getString("post_contents");
            String date = rs.getString("post_date");
            return new PostResponseDto(id, title, author, contents, date);
        });
    }

    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        String sql = "SELECT * FROM POST WHERE post_id = ?";

        List<PostResponseDto> posts = jdbcTemplate.query(sql, new Long[]{id}, (rs, rowNum) -> {
            Long postId = rs.getLong("post_id");
            String title = rs.getString("post_title");
            String author = rs.getString("post_author");
            String contents = rs.getString("post_contents");
            String date = rs.getString("post_date");
            return new PostResponseDto(postId, title, author, contents, date);
        });

        System.out.println("posts" + posts);

        if (posts.isEmpty()) {
            throw new IllegalArgumentException("해당 게시물은 존재하지 않습니다.");
        }

        return posts.get(0);
    }

    @PutMapping("/posts/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        String checkPasswordSql = "SELECT post_password FROM POST WHERE post_id = ?";
        List<String> passwords = jdbcTemplate.query(checkPasswordSql, new Long[]{id}, (rs, rowNum) -> rs.getString(1));

        if (!passwords.isEmpty() && passwords.get(0).equals(requestDto.getPassword())) {
            String sql = "UPDATE POST SET post_title = ?, post_author = ?, post_contents = ? WHERE post_id = ?";
            jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getAuthor(), requestDto.getContents(), id);
            return id;
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않거나 게시물이 존재하지 않습니다.");
        }
    }

    @DeleteMapping("/posts/{id}")
    public Long deletePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        String checkPasswordSql = "SELECT post_password FROM POST WHERE post_id = ?";
        List<String> passwords = jdbcTemplate.query(checkPasswordSql, new Long[]{id}, (rs, rowNum) -> rs.getString(1));

        if (!passwords.isEmpty() && passwords.get(0).equals(requestDto.getPassword())) {
            String sql = "DELETE FROM POST WHERE post_id = ?";
            jdbcTemplate.update(sql, id);
            return id;
        } else {
            throw new IllegalArgumentException("비밀번호가 일치하지 않거나 게시물이 존재하지 않습니다.");
        }
    }

}
