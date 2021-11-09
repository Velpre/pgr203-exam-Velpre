insert into survey (survey_name)
values ('Client Questionnaire');

insert into questions (title, survey_id)
values ('1. How much time do you spend using facebook? (per day)', 1);

insert into options (option_name, question_id)
VALUES ('Less than a minute', 1);
insert into options (option_name, question_id)
VALUES ('About 1 - 2 minutes', 1);
insert into options (option_name, question_id)
VALUES ('Between 2 and 5 minutes', 1);
insert into options (option_name, question_id)
VALUES ('More than 5 minutes', 1);

insert into questions (title, survey_id)
values ('2. In the last month, what has been your biggest pain point?', 1);

insert into options (option_name, question_id)
VALUES ('Finding enough time for important tasks', 2);
insert into options (option_name, question_id)
VALUES ('Delegating work', 2);
insert into options (option_name, question_id)
VALUES ('Having enough to do', 2);


insert into questions (title, survey_id)
values ('3. What is your biggest priority right now?', 1);

insert into options (option_name, question_id)
VALUES ('Finding a faster way to work', 3);
insert into options (option_name, question_id)
VALUES ('Problem-solving', 3);
insert into options (option_name, question_id)
VALUES ('Staff development', 3);

insert into survey (survey_name)
values ('Website Questionnaire');

insert into questions (title, survey_id)
values ('1. How many times have you visited localhost:8080 in the past month?', 2);

insert into options (option_name, question_id)
VALUES ('None', 4);
insert into options (option_name, question_id)
VALUES ('Once', 4);
insert into options (option_name, question_id)
VALUES ('More than once', 4);
insert into options (option_name, question_id)
VALUES ('A million times', 4);

insert into questions (title, survey_id)
values ('2. What is the primary reason for your visit this page?', 2);

insert into options (option_name, question_id)
VALUES ('To make a purchase', 5);
insert into options (option_name, question_id)
VALUES ('To find more information before making a purchase in-store', 5);
insert into options (option_name, question_id)
VALUES ('To contact customer service', 5);


insert into questions (title, survey_id)
values ('3. Are you able to find what you''re looking for on the website homepage?', 2);

insert into options (option_name, question_id)
VALUES ('Yes', 6);
insert into options (option_name, question_id)
VALUES ('No', 6);


insert into survey (survey_name)
values ('Customer Satisfaction Questionnaire');

insert into questions (title, survey_id)
values ('1. How likely are you to recommend us to family, friends, or colleagues?', 3);

insert into options (option_name, question_id)
VALUES ('Extremely likely', 7);
insert into options (option_name, question_id)
VALUES ('Somewhat likely', 7);
insert into options (option_name, question_id)
VALUES ('Neutral', 7);
insert into options (option_name, question_id)
VALUES ('Somewhat unlikely', 7);
insert into options (option_name, question_id)
VALUES ('Extremely unlikely', 7);

insert into questions (title, survey_id)
values ('2. How satisfied were you with your experience?', 3);

insert into questions (title, survey_id)
values ('3. Rank the following items in terms of their priority to your purchasing process.', 3);

insert into options (option_name, question_id)
VALUES ('Helpful staff', 9);
insert into options (option_name, question_id)
VALUES ('Quality of product', 9);
insert into options (option_name, question_id)
VALUES ('Price of product', 9);
insert into options (option_name, question_id)
VALUES ('Ease of purchase', 9);
insert into options (option_name, question_id)
VALUES ('Proximity of store', 9);
insert into options (option_name, question_id)
VALUES ('Online accessibility', 9);
insert into options (option_name, question_id)
VALUES ('Current need', 9);
insert into options (option_name, question_id)
VALUES ('Appearance of product', 9);

insert into questions (title, survey_id)
values ('4. Who did you purchase these products for?', 3);

insert into options (option_name, question_id)
VALUES ('Self', 10);
insert into options (option_name, question_id)
VALUES ('Family member', 10);
insert into options (option_name, question_id)
VALUES ('Friend', 10);
insert into options (option_name, question_id)
VALUES ('Colleague', 10);
insert into options (option_name, question_id)
VALUES ('On behalf of a business', 10);


insert into questions (title, survey_id)
values ('5. Please rate our staff on the following term: Friendly __ __ __ __ __ Hostile', 3);
insert into questions (title, survey_id)
values ('6. Please rate our staff on the following term: Helpful __ __ __ __ __ Useless', 3);
insert into questions (title, survey_id)
values ('7. Please rate our staff on the following term: Knowledgeable __ __ __ __ __ Inexperienced', 3);
insert into questions (title, survey_id)
values ('8. Please rate our staff on the following term: Professional __ __ __ __ __ Inappropriate', 3);


insert into questions (title, survey_id)
values ('9. Would you purchase from our company again?', 3);
insert into options (option_name, question_id)
VALUES ('Yes', 15);
insert into options (option_name, question_id)
VALUES ('No', 15);


insert into users(user_name)
values('User1')