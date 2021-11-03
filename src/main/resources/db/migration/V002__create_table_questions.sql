create table questions
(
    id serial primary key ,
    title varchar(100) not null,
    survey_id int not null,
    constraint FK_survey_questions foreign key (survey_id)
        references survey(id)
)