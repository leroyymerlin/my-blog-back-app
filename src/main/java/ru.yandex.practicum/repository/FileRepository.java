package ru.yandex.practicum.repository;

/**
 * Репозиторий для работы с файлами.
 */
public interface FileRepository {

    /**
     * Добавление изображения.
     *
     * @param id - идентификатор изображения
     * @param imageData изображение типа массив байт
     * @param contentType заголовок
     */
    void addImage(int id, byte[] imageData, String contentType);

    /**
     * Скачивание изображения.
     *
     * @param id идентификатор для скачивания изображения
     * @return изображение типа массив байт
     */
    byte[] download(int id);

}
