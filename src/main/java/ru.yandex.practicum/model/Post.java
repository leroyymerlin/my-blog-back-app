package ru.yandex.practicum.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    private int idPost;
    private String title;
    private String text;
    private List<String> tags;
    private int likeCount;
    private int commentCount;
    private int pageNumber;
}
