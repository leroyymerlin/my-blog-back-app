package ru.yandex.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.repository.CommentRepository;

import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public List<Comment> getCommentList(int id) {
        return commentRepository.findAll(id);
    }

    public Comment getComment(int postId, int id) {
        return commentRepository.getComment(postId, id);
    }

    public Comment addComment(Comment comment) {
        return commentRepository.addComment(comment);
    }

    public Comment updateComment(int postId, int id, Comment comment) {
        return commentRepository.updateComment(postId, id, comment);
    }

    public void deleteComment(int postId, int id) {
        commentRepository.deleteComment(postId, id);
    }

}
