insert into survey (survey_name)
values ('Client Questionnaire');

insert into questions (title, survey_id)
values ('How much time do you spend using facebook? (per day)', 1);

insert into options (option_name, question_id)
VALUES ('Less than a minute', 1);
insert into options (option_name, question_id)
VALUES ('About 1 - 2 minutes', 1);
insert into options (option_name, question_id)
VALUES ('Between 2 and 5 minutes', 1);
insert into options (option_name, question_id)
VALUES ('More than 5 minutes', 1);

insert into questions (title, survey_id)
values ('In the last month, what has been your biggest pain point?', 1);

insert into options (option_name, question_id)
VALUES ('Finding enough time for important tasks', 2);
insert into options (option_name, question_id)
VALUES ('Delegating work', 2);
insert into options (option_name, question_id)
VALUES ('Having enough to do', 2);


insert into questions (title, survey_id)
values ('What is your biggest priority right now?', 1);

insert into options (option_name, question_id)
VALUES ('Finding a faster way to work', 3);
insert into options (option_name, question_id)
VALUES ('Problem-solving', 3);
insert into options (option_name, question_id)
VALUES ('Staff development', 3);


insert into questions (title, survey_id)
values ('Please rate your knowledge on the following term: Knowledgeable __ __ __ __ __ Inexperienced', 1);
insert into questions (title, survey_id)
values ('Please rate our staff on the following term: Professional __ __ __ __ __ Inappropriate', 1);

insert into survey (survey_name)
values ('Test Questionnaire');

insert into questions (title, survey_id)
values ('How much time do you spend using facebook? (per day)', 2);

insert into options (option_name, question_id)
VALUES ('Less than a minute', 6);
insert into options (option_name, question_id)
VALUES ('About 1 - 2 minutes', 6);
insert into options (option_name, question_id)
VALUES ('Between 2 and 5 minutes', 6);
insert into options (option_name, question_id)
VALUES ('More than 5 minutes', 6);

insert into users(user_name)
values('User1')