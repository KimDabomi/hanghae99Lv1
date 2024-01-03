package com.spring.springlv1.repository;

import com.spring.springlv1.dto.PostRequestDto;
import com.spring.springlv1.dto.PostResponseDto;
import com.spring.springlv1.entity.Post;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

@Component
public class PostRepository {
    private static final String POST_EXIST_ERROR_MESSAGE = "해당 게시물은 존재하지 않습니다.";
    private final JdbcTemplate jdbcTemplate;

    public PostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Post save(Post post) {
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
        return post;
    }

    public List<PostResponseDto> findAll() {
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

    public PostResponseDto find(Long id) {
        String sql = "SELECT * FROM POST WHERE post_id = ?";

        List<PostResponseDto> posts = jdbcTemplate.query(sql, new Long[]{id}, (rs, rowNum) -> {
            Long postId = rs.getLong("post_id");
            String title = rs.getString("post_title");
            String author = rs.getString("post_author");
            String contents = rs.getString("post_contents");
            String date = rs.getString("post_date");
            return new PostResponseDto(postId, title, author, contents, date);
        });

        if (posts.isEmpty()) {
            throw new IllegalArgumentException(POST_EXIST_ERROR_MESSAGE);
        }

        return posts.get(0);
    }

    public void update(Long id, PostRequestDto requestDto) {
        String sql = "UPDATE POST SET post_title = ?, post_author = ?, post_contents = ? WHERE post_id = ?";
        jdbcTemplate.update(sql, requestDto.getTitle(), requestDto.getAuthor(), requestDto.getContents(), id);
    }

    public void delete(Long id) {
        String sql = "DELETE FROM POST WHERE post_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
