package ru.yandex.practicum.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.repository.CommentRepository;
import ru.yandex.practicum.service.CommentService;

@TestConfiguration
public class CommentServiceTestConfig {

    @Bean
    public CommentRepository commentRepository() {
        return Mockito.mock(CommentRepository.class);
    }

    @Bean
    public CommentService commentService(CommentRepository commentRepository) {
        return new CommentService(commentRepository);
    }
}
