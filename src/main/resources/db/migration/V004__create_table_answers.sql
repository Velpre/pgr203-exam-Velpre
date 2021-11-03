create table answers
(
    id serial primary key ,
    answer varchar(1000) not null,
    question_id int not null,
    user_id int not null,
    CONSTRAINT FK_question_answer Foreign key (question_id) REFERENCES questions(id)
    on delete cascade,
    constraint fk_user_answer FOREIGN KEY (user_id) references users(id)
    on delete cascade
);
