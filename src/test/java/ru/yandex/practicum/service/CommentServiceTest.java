package ru.yandex.practicum.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.configuration.CommentServiceTestConfig;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@Import(CommentServiceTestConfig.class)
class CommentServiceTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentService commentService;

    private Comment testComment;
    private List<Comment> testCommentList;

    @BeforeEach
    void setUp() {

        Mockito.reset(commentRepository);

        testComment = new Comment();
        testComment.setId(1);
        testComment.setText("Text");
        testComment.setPostId(100);

        Comment comment2 = new Comment();
        comment2.setId(2);
        comment2.setText("Another comment");
        comment2.setPostId(100);

        testCommentList = Arrays.asList(testComment, comment2);
    }

    @Test
    void getCommentList_ShouldReturnListFromRepository() {
        int postId = 100;
        when(commentRepository.findAll(postId)).thenReturn(testCommentList);

        List<Comment> result = commentService.getCommentList(postId);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(testCommentList, result);
        verify(commentRepository, times(1)).findAll(postId);
    }

    @Test
    void getComment_ShouldReturnCommentFromRepository() {
        int postId = 100;
        int commentId = 1;
        when(commentRepository.getComment(postId, commentId)).thenReturn(testComment);

        Comment result = commentService.getComment(postId, commentId);

        assertNotNull(result);
        assertEquals(testComment, result);
        verify(commentRepository, times(1)).getComment(postId, commentId);
    }

    @Test
    void addComment_ShouldCallRepositoryAndReturnSavedComment() {
        Comment newComment = new Comment();
        newComment.setText("New comment");
        newComment.setPostId(100);

        Comment savedComment = new Comment();
        savedComment.setId(10);
        savedComment.setText("New comment");
        savedComment.setPostId(100);

        when(commentRepository.addComment(any(Comment.class))).thenReturn(savedComment);

        Comment result = commentService.addComment(newComment);

        assertNotNull(result);
        assertEquals(10, result.getId());
        assertEquals("New comment", result.getText());
        verify(commentRepository, times(1)).addComment(newComment);
    }

    @Test
    void updateComment_ShouldCallRepositoryAndReturnUpdatedComment() {
        int postId = 100;
        int commentId = 1;
        Comment updatedData = new Comment();
        updatedData.setText("Updated text");

        Comment updatedComment = new Comment();
        updatedComment.setId(commentId);
        updatedComment.setText("Updated text");
        updatedComment.setPostId(postId);

        when(commentRepository.updateComment(postId, commentId, updatedData)).thenReturn(updatedComment);

        Comment result = commentService.updateComment(postId, commentId, updatedData);

        assertNotNull(result);
        assertEquals(commentId, result.getId());
        assertEquals("Updated text", result.getText());
        verify(commentRepository, times(1)).updateComment(postId, commentId, updatedData);
    }

    @Test
    void deleteComment_ShouldCallRepository() {
        int postId = 100;
        int commentId = 1;

        commentService.deleteComment(postId, commentId);

        verify(commentRepository, times(1)).deleteComment(postId, commentId);
    }

    @Test
    void getCommentList_WhenRepositoryReturnsEmptyList_ShouldReturnEmptyList() {
        int postId = 9;
        when(commentRepository.findAll(postId)).thenReturn(List.of());

        List<Comment> result = commentService.getCommentList(postId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(commentRepository, times(1)).findAll(postId);
    }

    @Test
    void getComment_WhenCommentNotFound_ShouldReturnNull() {
        int postId = 100;
        int commentId = 9;
        when(commentRepository.getComment(postId, commentId)).thenReturn(null);

        Comment result = commentService.getComment(postId, commentId);

        assertNull(result);
        verify(commentRepository, times(1)).getComment(postId, commentId);
    }
}