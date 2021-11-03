alter table answers
    DROP constraint fk_questions_answer;
alter table answers
    add constraint FK_questions_answer foreign key (question_id)
        references questions(id)
        ON DELETE CASCADE;
