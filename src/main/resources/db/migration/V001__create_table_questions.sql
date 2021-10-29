create table questions
(
    id serial primary key ,
    title varchar(100) not null,
    question_text varchar(100) not null,
    category varchar(100) not null
)