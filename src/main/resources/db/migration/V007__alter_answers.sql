alter table answers
    DROP constraint answers_question_id_fkey;
alter table answers
    add constraint FK_questions_answer foreign key (question_id)
        references questions(id)
        ON DELETE CASCADE;
