CREATE TABLE databreach.news(id BIGINT, content TEXT);
GRANT SELECT ON databreach.* TO 'db_databreach'@'%';

INSERT INTO databreach.news(id, content) VALUES (0417, 'gemastik{web_databreach_d02a99f039325ed77e13c5e3e3211f4e3a6d70cfacae1923a}');