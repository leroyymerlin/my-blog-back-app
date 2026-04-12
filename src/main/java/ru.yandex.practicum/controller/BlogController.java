package ru.yandex.practicum.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.practicum.model.Blog;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Post;
import ru.yandex.practicum.service.BlogPostService;
import ru.yandex.practicum.service.CommentService;
import ru.yandex.practicum.service.FileService;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/posts")
public class BlogController {

    private final BlogPostService postService;
    private final CommentService commentService;
    private final FileService fileService;

    @GetMapping
    public Blog getBlog(@RequestParam("search") String search,
                            @RequestParam("pageNumber") int pageNumber,
                            @RequestParam("pageSize") int pageSize) {
        return postService.find(search, pageNumber, pageSize);
    }

    @PostMapping("/{id}")
    public Post getPost(@PathVariable(name = "id") int id) {
        return postService.getPost(id);
    }

    @PostMapping
    public Post addPost(@RequestBody Post post) throws JsonProcessingException {
        return postService.addPost(post);
    }

    @PutMapping("/{id}")
    public Post update(@PathVariable(name = "id") int id, @RequestBody Post post) throws JsonProcessingException {
        return postService.updatePost(id, post);
    }

    @DeleteMapping(value = "/{id}")
    public void delete(@PathVariable(name = "id") int id) {
        postService.deletePost(id);
    }

    //-----------------------------------------------------------------------------------------------------//

    @PostMapping("/{id}/likes")
    public Post incrementLikes(@PathVariable(name = "id") int id) throws JsonProcessingException {
        return postService.incrementLikes(id);
    }

    @PutMapping("/{id}/image")
    public String uploadFile(@RequestParam("image") MultipartFile file, @PathVariable(name = "id") int id) {
        return fileService.upload(id, file);
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<Resource> downloadFile(@PathVariable(name = "id") int id) {
        Resource file = fileService.download(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(file);
    }

    //-----------------------------------------------------------------------------------------------------//

    @GetMapping(value = "/{post_id}/comments")
    public List<Comment> getCommentList(@PathVariable(name = "post_id") int id) {
        return commentService.getCommentList(id);
    }

    @GetMapping(value = "/{post_id}/comments/{id}")
    public Comment getComment(@PathVariable(name = "post_id") int postId, @PathVariable(name = "id") int id) {
        return commentService.getComment(postId, id);
    }

    @PostMapping("{id}/comments")
    public Comment addComment(@RequestBody Comment comment) {
        return commentService.addComment(comment);
    }

    @PutMapping("/{post_id}/comments/{id}")
    public Comment updateComment(@PathVariable(name = "post_id") int postId,
                                 @PathVariable(name = "id") int id,
                                 @RequestBody Comment comment) {
        return commentService.updateComment(postId, id, comment);
    }

    @DeleteMapping("/{post_id}/comments/{id}")
    public void deleteComment(@PathVariable(name = "post_id") int post_id, @PathVariable(name = "id") int id) {
        commentService.deleteComment(post_id, id);
    }

}
