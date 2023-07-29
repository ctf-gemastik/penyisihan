CREATE TABLE databreach.news(id BIGINT, content TEXT);
GRANT SELECT ON databreach.* TO 'db_databreach'@'%';

INSERT INTO databreach.news(id, content) VALUES (0417, 'flagnya bukan disini bang');