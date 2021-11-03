create table answers
(
    id serial primary key ,
    answer varchar(1000) not null,
    question_id int not null,
    CONSTRAINT FK_QUESTIONS_ANSWER Foreign key (question_id) REFERENCES questions(id)
)