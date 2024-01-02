package com.spring.springlv1.controller;

import com.spring.springlv1.dto.PostRequestDto;
import com.spring.springlv1.dto.PostResponseDto;
import com.spring.springlv1.entity.Post;
import org.springframework.web.bind.annotation.*;

import java.util.*;

//@RestController
@RequestMapping("/api")
public class PostController_NotDB {
    private final Map<Long, Post> postList = new HashMap<>();

    @PostMapping("/posts")
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        Post post = new Post(requestDto);

        Long maxId = !postList.isEmpty() ? Collections.max(postList.keySet()) + 1 : 1;
        post.setId(maxId);

        postList.put(post.getId(), post);

        return new PostResponseDto(post);
    }

    @GetMapping("/posts")
    public List<PostResponseDto> getPosts() {
        return postList.values().stream()
                .sorted(Comparator.comparing(Post::getDate).reversed())
                .map(PostResponseDto::new).toList();
    }

    @GetMapping("/posts/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        if(postList.containsKey(id)) {
            return new PostResponseDto(postList.get(id));
        } else {
            throw new IllegalArgumentException("해당 게시물은 존재하지 않습니다.");
        }
    }

    @PutMapping("/posts/{id}")
    public Long updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        if(postList.containsKey(id)) {
            Post post = postList.get(id);
            if (post.getPassword().equals(requestDto.getPassword())) {
                post.update(requestDto);
                return post.getId();
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 게시물은 존재하지 않습니다.");
        }
    }

    @DeleteMapping("/posts/{id}")
    public Long deletePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        if(postList.containsKey(id)) {
            Post post = postList.get(id);
            if (post.getPassword().equals(requestDto.getPassword())) {
                postList.remove(id);
                return id;
            } else {
                throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
            }
        } else {
            throw new IllegalArgumentException("해당 게시물은 존재하지 않습니다.");
        }
    }
}

