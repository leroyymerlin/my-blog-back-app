package ru.yandex.practicum.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Blog;
import ru.yandex.practicum.model.Comment;
import ru.yandex.practicum.model.Paginate;
import ru.yandex.practicum.model.Post;

import java.util.Collections;
import java.util.List;

@Repository
public class JdbcNativeBlogPostRepository implements BlogRepository, CommentRepository, FileRepository {

    private static final int SECOND_PAGE = 2;
    private static final int LAST_PAGE = 3;

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public JdbcNativeBlogPostRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public Blog findAll(String search, int pageNumber, int pageSize) {
        pageNumber = Math.max(pageNumber, 1);
        int offset = (pageNumber - 1) * pageSize;

        String sql = """
        SELECT p.id_post, p.title, p.text, p.tags, p.like_count, p.comment_count, p.page_number
        FROM post p
        JOIN blog_post b ON p.blog_id = b.id
        WHERE p.blog_id = ?
        AND (p.title LIKE ? OR p.text LIKE ?)
        LIMIT ? OFFSET ?
    """;

        String countSql = "SELECT COUNT(*) FROM post WHERE blog_id = ? AND (title LIKE ? OR text LIKE ?)";

        String like = "%" + search + "%";

        List<Post> posts = jdbcTemplate.query(sql, (rs, rowNum) ->
                new Post(
                        rs.getInt("id_post"),
                        rs.getString("title"),
                        rs.getString("text"),
                        Collections.singletonList(rs.getString("tags")),
                        rs.getInt("like_count"),
                        rs.getInt("comment_count"),
                        rs.getInt("page_number")
                ), 1, like, like, pageSize, offset);

        int total = jdbcTemplate.queryForObject(countSql, Integer.class, 1, like, like);

        boolean hasPrev = pageNumber > 1;
        boolean hasNext = offset + pageSize < total;
        int lastPage = (int) Math.ceil((double) total / pageSize);

        Blog blogPost = new Blog();
        blogPost.setPosts(posts);
        blogPost.setPaginate(Paginate.builder()
                .hasPrev(hasPrev)
                .hasNext(hasNext)
                .lastPage(lastPage)
                .build());
        return blogPost;
    }

    @Override
    public void savePost(Post post) throws JsonProcessingException {
        Integer blogId = jdbcTemplate.queryForObject(
                "select id from blog_post limit 1",
                Integer.class
        );

        String tagsJson = objectMapper.writeValueAsString(post.getTags());
        jdbcTemplate.update("insert into post(title, text, tags, like_count, comment_count, page_number, blog_id) values(?, ?, ?, ?, ?, ?, ?)",
                post.getTitle(),
                post.getText(),
                tagsJson,
                post.getLikeCount(),
                post.getCommentCount(),
                post.getPageNumber(),
                blogId);

    }

    @Override
    public Post addPost(Post post) throws JsonProcessingException {
        savePost(post);
        return post;
    }

    @Override
    public void deletePost(int id) {
        jdbcTemplate.update("delete from post where id_post = ?", id);
    }

    @Override
    public Post updatePost(int id, Post post) throws JsonProcessingException {
        String tagsJson = objectMapper.writeValueAsString(post.getTags());
        jdbcTemplate.update(
                "update post set title = ?, text = ?, tags = ?, like_count = ?, comment_count = ?, page_number = ? where id_post = ?",
                post.getTitle(),
                post.getText(),
                tagsJson,
                post.getLikeCount(),
                post.getCommentCount(),
                post.getPageNumber(),
                id
        );
        return getPost(id);
    }

    @Override
    public Post getPost(int id) {
        String sqlPost = """
                select id_post, title, text, tags, like_count, comment_count, page_number from post where id_post = ? 
                """;
        return jdbcTemplate.queryForObject(sqlPost, (rs, rowNum) -> new Post(
                rs.getInt("id_post"),
                rs.getString("title"),
                rs.getString("text"),
                Collections.singletonList(rs.getString("tags")),
                rs.getInt("like_count"),
                rs.getInt("comment_count"),
                rs.getInt("page_number")),
                id);
    }

    //------------------------------------------------------------------------------------//

    @Override
    public List<Comment> findAll(int postId) {
        String sqlComment = """
                select id, text from comment where id_post = ?
                """;
        return jdbcTemplate.query(sqlComment, (rs, rowNum) -> new Comment(
                        rs.getInt("id"),
                        rs.getString("text"),
                        postId),
                (long) postId
        );
    }

    @Override
    public void saveComment(Comment comment) {
        jdbcTemplate.update("insert into comment(id, text, id_post) values(?, ?, ?)",
                comment.getId(), comment.getText(), comment.getPostId());
    }

    @Override
    public Comment updateComment(int postId, int id, Comment comment) {
        jdbcTemplate.update("update comment set text = ? where id = ? and id_post = ?",
                comment.getText(), id, postId);

        return getComment(postId, id);
    }

    @Override
    public void deleteComment(int postId, int id) {
        jdbcTemplate.update("delete from comment where id = ?", id);
    }

    @Override
    public Comment getComment(int postId, int id) {
        String sqlPost = """
                select id, text, id_post from comment where id = ? and id_post = ? 
                """;
        return jdbcTemplate.queryForObject(sqlPost, (rs, rowNum) -> new Comment(
                        rs.getInt("id"),
                        rs.getString("text"),
                        rs.getInt("id_post")),
                id, postId
        );
    }

    @Override
    public Comment addComment(Comment comment) {
        saveComment(comment);
        return comment;
    }

    //------------------------------------------------------------------------------------//

    @Override
    public void addImage(int id, byte[] imageData, String contentType) {
        jdbcTemplate.update(
                "update post set image_data = ?, image_content_type = ? where id_post = ?",
                imageData,
                contentType,
                id
        );
    }

    @Override
    public byte[] download(int id) {
        return jdbcTemplate.queryForObject(
                "select image_data from post where id_post = ?",
                (rs, rowNum) -> rs.getBytes("image_data"),
                id
        );
    }
}
