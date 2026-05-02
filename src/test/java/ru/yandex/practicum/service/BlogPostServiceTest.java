package ru.yandex.practicum.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.model.Blog;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.repository.BlogRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlogPostServiceTest {

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BlogPostService blogPostService;

    private Post testPost;
    private Blog testBlog;

    @BeforeEach
    void setUp() {
        testPost = new Post();
        testPost.setIdPost(1);
        testPost.setTitle("Test Title");
        testPost.setLikeCount(5);
        testPost.setCommentCount(3);
        List<Post> posts = new ArrayList<>();
        posts.add(testPost);

        testBlog = new Blog();
        testBlog.setId(1);
        testBlog.setPosts(posts);
    }

    @Test
    void find() {
        String search = "test";
        int pageNumber = 0;
        int pageSize = 10;
        when(blogRepository.findAll(search, pageNumber, pageSize)).thenReturn(testBlog);

        Blog result = blogPostService.find(search, pageNumber, pageSize);

        assertNotNull(result);
        assertSame(testBlog, result);
        verify(blogRepository, times(1)).findAll(search, pageNumber, pageSize);
    }

    @Test
    void getPost() {
        int postId = 1;
        when(blogRepository.getPost(postId)).thenReturn(testPost);

        Post result = blogPostService.getPost(postId);

        assertNotNull(result);
        assertEquals(testPost, result);
        verify(blogRepository, times(1)).getPost(postId);
    }

    @Test
    void addPost_ShouldSetCountsToZeroAndCallRepository() throws JsonProcessingException {
        Post newPost = new Post();
        newPost.setTitle("New Post");
        newPost.setLikeCount(100);  // исходное значение, должно быть перезаписано
        newPost.setCommentCount(50);

        Post savedPost = new Post();
        savedPost.setIdPost(10);
        savedPost.setTitle("New Post");
        savedPost.setLikeCount(0);
        savedPost.setCommentCount(0);

        when(blogRepository.addPost(any(Post.class))).thenReturn(savedPost);

        Post result = blogPostService.addPost(newPost);

        assertNotNull(result);
        assertEquals(0, result.getLikeCount());
        assertEquals(0, result.getCommentCount());
        verify(blogRepository, times(1)).addPost(argThat(post ->
                post.getLikeCount() == 0 && post.getCommentCount() == 0
        ));
    }

    @Test
    void addPost_ShouldPropagateJsonProcessingException() throws JsonProcessingException {
        Post newPost = new Post();
        when(blogRepository.addPost(any(Post.class))).thenThrow(JsonProcessingException.class);

        assertThrows(JsonProcessingException.class, () -> blogPostService.addPost(newPost));
        verify(blogRepository, times(1)).addPost(newPost);
    }

    @Test
    void deletePost() {
        int postId = 5;
        blogPostService.deletePost(postId);

        verify(blogRepository, times(1)).deletePost(postId);
    }

    @Test
    void updatePost_ShouldCallRepositoryAndReturnUpdatedPost() throws JsonProcessingException {
        int postId = 2;
        Post updatedPost = new Post();
        updatedPost.setIdPost(postId);
        updatedPost.setTitle("Updated");

        when(blogRepository.updatePost(eq(postId), eq(updatedPost))).thenReturn(updatedPost);

        Post result = blogPostService.updatePost(postId, updatedPost);

        assertSame(updatedPost, result);
        verify(blogRepository, times(1)).updatePost(postId, updatedPost);
    }

    @Test
    void updatePost_ShouldPropagateJsonProcessingException() throws JsonProcessingException {
        int postId = 2;
        Post updatedPost = new Post();
        when(blogRepository.updatePost(eq(postId), eq(updatedPost))).thenThrow(JsonProcessingException.class);

        assertThrows(JsonProcessingException.class, () -> blogPostService.updatePost(postId, updatedPost));
        verify(blogRepository, times(1)).updatePost(postId, updatedPost);
    }

    @Test
    void incrementLikes_ShouldIncreaseLikeCountAndUpdateRepository() throws JsonProcessingException {
        int postId = 1;
        Post existingPost = new Post();
        existingPost.setIdPost(postId);
        existingPost.setLikeCount(5);
        existingPost.setCommentCount(10);

        when(blogRepository.getPost(postId)).thenReturn(existingPost);
        when(blogRepository.updatePost(eq(postId), any(Post.class))).thenAnswer(invocation -> invocation.getArgument(1));

        Post result = blogPostService.incrementLikes(postId);

        assertNotNull(result);
        assertEquals(6, result.getLikeCount());  // 5 + 1
        assertEquals(10, result.getCommentCount()); // осталось без изменений
        verify(blogRepository, times(1)).getPost(postId);
        verify(blogRepository, times(1)).updatePost(eq(postId), argThat(post ->
                post.getLikeCount() == 6
        ));
    }

    @Test
    void incrementLikes_ShouldPropagateJsonProcessingException() throws JsonProcessingException {
        int postId = 1;
        when(blogRepository.getPost(postId)).thenReturn(testPost);
        when(blogRepository.updatePost(eq(postId), any(Post.class))).thenThrow(JsonProcessingException.class);

        assertThrows(JsonProcessingException.class, () -> blogPostService.incrementLikes(postId));
        verify(blogRepository, times(1)).getPost(postId);
        verify(blogRepository, times(1)).updatePost(eq(postId), any(Post.class));
    }

    @Test
    void incrementLikes_WhenGetPostReturnsNull_ShouldThrowException() throws JsonProcessingException {
        int postId = 999;
        when(blogRepository.getPost(postId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> blogPostService.incrementLikes(postId));
        verify(blogRepository, times(1)).getPost(postId);
        verify(blogRepository, never()).updatePost(anyInt(), any());
    }
}