alter table questions
    DROP constraint FK_survey_questions;
alter table questions
    add constraint FK_survey_questions foreign key (survey_id)
        references survey(id)
        ON DELETE CASCADE;
