create table options
(
    id serial primary key ,
    option_name varchar(1000) not null,
    question_id int not null,
    CONSTRAINT FK_question_options Foreign key (question_id) REFERENCES questions(id)
    on delete cascade
);
