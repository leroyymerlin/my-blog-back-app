package ru.yandex.practicum.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class Blog {

    private int id;
    private List<Post> posts;
    private Paginate paginate;

}
