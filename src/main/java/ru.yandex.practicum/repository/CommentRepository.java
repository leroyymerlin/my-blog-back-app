package ru.yandex.practicum.repository;

import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Comment;

import java.util.List;

/**
 * Репозиторий для работы с комментариями.
 */
@Repository
public interface CommentRepository {

    /**
     * Получение всех комментариев одного поста.
     *
     * @param id индентификатор поста
     * @return Лист с комментариями
     */
    List<Comment> findAll(int id);

    /**
     * Получение комментария.
     *
     * @param postId индентификатор поста
     * @param id индентификатор комментария
     * @return объект типа Comment
     */
    Comment getComment(int postId, int id);

    /**
     * Добавление комментария.
     *
     * @param comment объект типа Comment
     * @return объект типа Comment
     */
    Comment addComment(Comment comment);

    /**
     * Сохранение комментария.
     *
     * @param comment объект типа Comment
     */
    void saveComment(Comment comment);

    /**
     * Обновление комментария.
     *
     * @param postId индентификатор поста
     * @param id индентификатор комментария
     * @param comment объект типа Comment
     * @return объект типа Comment
     */
    Comment updateComment(int postId, int id, Comment comment);

    /**
     * Удаление комментария.
     *
     * @param postId индентификатор поста
     * @param id индентификатор комментария
     */
    void deleteComment(int postId, int id);
}
