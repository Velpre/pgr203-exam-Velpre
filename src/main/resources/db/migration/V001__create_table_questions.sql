create table questions
(
    id serial primary key ,
    title varchar(100) not null,
    question_text varchar(100) not null,
    survey_id int not null,
    Foreign key (survey_id) REFERENCES questions(id)
);

