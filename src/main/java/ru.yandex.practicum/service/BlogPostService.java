package ru.yandex.practicum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Blog;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.BlogRepository;

@RequiredArgsConstructor
@Service
public class BlogPostService {

    private static final int MAX_TEXT_LENGTH = 128;
    private static final String ELLIPSIS = "…";

    private final BlogRepository blogRepository;

    public Blog find(String search, int pageNumber, int pageSize) {
        return blogRepository.findAll(search, pageNumber, pageSize);
    }

    public Post getPost(int id) {
        return blogRepository.getPost(id);
    }

    public Post addPost(Post post) throws JsonProcessingException {
        String originalText = post.getText();
        post.setText(truncateText(originalText));

        post.setLikeCount(0);
        post.setCommentCount(0);
        return blogRepository.addPost(post);
    }

    public void deletePost(int id) {
        blogRepository.deletePost(id);
    }

    public Post updatePost(int id, Post post) throws JsonProcessingException {
        return blogRepository.updatePost(id, post);
    }

    public Post incrementLikes(int id) throws JsonProcessingException {
        Post post = getPost(id);
        int like = post.getLikeCount();
        post.setLikeCount(like + 1);
        blogRepository.updatePost(id, post);
        return post;
    }

    private String truncateText(String text) {
        if (text == null) {
            return null;
        }
        if (text.length() <= MAX_TEXT_LENGTH) {
            return text;
        }
        return text.substring(0, MAX_TEXT_LENGTH) + ELLIPSIS;
    }
}
