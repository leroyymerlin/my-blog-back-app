package ru.yandex.practicum.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Blog;
import ru.yandex.practicum.model.Post;

/**
 * Интерфейс для работы с блогом.
 */
@Repository
public interface BlogRepository {


    /**
     * Получение всего блога - посты и пагинация.
     *
     * @param search - строка поиска
     * @param pageNumber - номер страницы
     * @param pageSize - размер страницы
     * @return объект типа Blog
     */
    Blog findAll(String search, int pageNumber, int pageSize);

    /**
     * Удаление поста из блога.
     *
     * @param id идентификатор поста
     */
    void deletePost(int id);

    /**
     * Обновление поста.
     *
     * @param id идентификатор поста
     * @param post объект типа Post
     * @return измененный объект типа Post
     * @throws JsonProcessingException исключение выбрасываемое ObjectMapper
     */
    Post updatePost(int id, Post post) throws JsonProcessingException;

    /**
     * Получение поста.
     *
     * @param id идентификатор поста
     * @return объект типа Post
     */
    Post getPost(int id);

    /**
     * Добавление поста.
     *
     * @param post объект типа Post
     * @return объект типа Post
     * @throws JsonProcessingException исключение выбрасываемое ObjectMapper
     */
    Post addPost(Post post) throws JsonProcessingException;

    /**
     * Сохранение поста.
     *
     * @param post объект типа Post
     * @throws JsonProcessingException исключение выбрасываемое ObjectMapper
     */
    void savePost(Post post) throws JsonProcessingException;
}
