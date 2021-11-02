create table questions
(
    id serial primary key ,
    question varchar(1000) not null,
    survey_id int not null,
    Foreign key (survey_id) REFERENCES questions(id)
)