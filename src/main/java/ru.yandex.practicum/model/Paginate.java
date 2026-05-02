package ru.yandex.practicum.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Paginate {

    private boolean hasPrev;
    private boolean hasNext;
    private int lastPage;
}
