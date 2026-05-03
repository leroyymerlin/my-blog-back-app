package ru.yandex.practicum.configuration;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.repository.BlogRepository;
import ru.yandex.practicum.service.BlogPostService;

@TestConfiguration
public class BlogPostServiceTestConfig {

    @Bean
    public BlogRepository blogRepository() {
        return Mockito.mock(BlogRepository.class);
    }

    @Bean
    public BlogPostService blogPostService(BlogRepository blogRepository) {
        return new BlogPostService(blogRepository);
    }
}
