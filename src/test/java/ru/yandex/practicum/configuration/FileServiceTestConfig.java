package ru.yandex.practicum.configuration;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import ru.yandex.practicum.repository.FileRepository;
import ru.yandex.practicum.service.FileService;

@TestConfiguration
public class FileServiceTestConfig {

    @Bean
    public FileRepository fileRepository() {
        return Mockito.mock(FileRepository.class);
    }

    @Bean
    public FileService fileService(FileRepository fileRepository) {
        return new FileService(fileRepository);
    }
}
