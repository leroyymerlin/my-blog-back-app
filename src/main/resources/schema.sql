
create table if not exists blog_post (
    id integer primary key,
    page_number integer
  );

create table if not exists post (
    id_post integer primary key AUTO_INCREMENT,
    blog_id integer not null,
    title text not null,
    text varchar(131),
    tags JSON,
    like_count integer,
    comment_count integer,
    page_number integer,
    image_data LONGBLOB,
    image_content_type varchar(100),
    foreign key (blog_id) references blog_post(id)
    );

create table if not exists comment (
    id integer primary key AUTO_INCREMENT,
    text text not null,
    id_post integer,
    foreign key (id_post) references post(id_post)

    );