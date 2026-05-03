package ru.yandex.practicum.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:test-application.properties")
class BlogPostControllerTest {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();

        // Чистим и наполняем БД перед каждым тестом
        jdbcTemplate.execute("delete from comment");
        jdbcTemplate.execute("delete from post");
        jdbcTemplate.execute("delete from blog_post");
        jdbcTemplate.execute("insert into blog_post (id, page_number) values (1, 3)");
        jdbcTemplate.execute("""
                    insert into post (id_post, blog_id, title, tags, like_count, comment_count, page_number)
                    VALUES (1, 1, 'title','["tag_1", "tag_2"]',30,20,3)
                """);
        jdbcTemplate.execute("""
                    insert into post (id_post, blog_id, title, tags, like_count, comment_count, page_number)
                    values (2, 1, 'title', '["tag_1", "tag_2"]', 25, 25, 3)
                """);
        jdbcTemplate.execute("alter table post alter column id_post restart with 3");

        jdbcTemplate.execute("insert into comment (id, text, id_post) values (1, 'Nice!', 1)");

    }

    @Test
    void getBlogPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?search=title&pageNumber=1&pageSize=5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.posts[0].idPost").value(1))
                .andExpect(jsonPath("$.posts[1].idPost").value(2));
    }

    @Test
    void getPost() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(3));
    }

    @Test
    void addPost() throws Exception {
        String json = """
                  {
                    "title": "Название поста 3",
                    "text": "Текст поста в формате Markdown...",
                    "tags": ["tag_1", "tag_2"]
                  }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/posts").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pageNumber").value(0));
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?search=Название&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(1));
    }

    @Test
    void update() throws Exception {
        String json = """
                  {
                    "id": 2,
                    "title": "Название поста 3",
                    "text": "Текст поста в формате Markdown...",
                    "tags": ["tag_1", "tag_2"]
                  }
                """;
        mockMvc.perform(MockMvcRequestBuilders.put("/posts/2").contentType(MediaType.APPLICATION_JSON).content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Текст поста в формате Markdown..."));
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?search=Название&pageNumber=0&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].text").value("Текст поста в формате Markdown..."));
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/2"))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts?search=Lalala&pageNumber=0&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts.length()").value(0));
    }

    @Test
    void incrementLikes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/2/likes").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
        mockMvc.perform(MockMvcRequestBuilders.get("/posts?search=title&pageNumber=1&pageSize=5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.posts[0].likeCount").value(30));
    }

    @Test
    void uploadAndDownloadFile() throws Exception {
        byte[] pngStub = new byte[]{(byte) 137, 80, 78, 71};
        MockMultipartFile file = new MockMultipartFile("image", "avatar.png", "image/png", pngStub);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/posts/{id}/image", 1)
                        .file(file)
                        .with(request -> {
                                    request.setMethod("PUT");
                                    return request;
                                }
                        )
                )
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{id}/image", 1))
                .andExpect(status().isOk());
    }

    @Test
    void getComments() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{post_id}/comments", 1))
                .andExpect(status().isOk());

    }

    @Test
    void getComment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{post_id}/comments/{id}", 1, 1))
                .andExpect(status().isOk());
    }

    @Test
    void addComment() throws Exception {
        String json = """
                  {
                    "text": "Комментарий к посту",
                    "postId": 1
                  }
                """;
        mockMvc.perform(MockMvcRequestBuilders.post("/posts/{id}/comments", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Комментарий к посту"));
        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{post_id}/comments/{id}", 1, 0))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Комментарий к посту"));
    }

    @Test
    void updateComment() throws Exception {
        String json = """
                  {
                    "id": 1,
                    "text": "Второй комментарий к посту 1",
                    "postId": 1
                  }
                """;
        mockMvc.perform(MockMvcRequestBuilders.put("/posts/{post_id}/comments/{id}", 1, 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(MockMvcRequestBuilders.get("/posts/{post_id}/comments/{id}", 1, 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.text").value("Второй комментарий к посту 1"));
    }

    @Test
    void deleteComment() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/posts/{post_id}/comments/{id}", 1, 1))
                .andExpect(status()
                        .isOk());

    }
}