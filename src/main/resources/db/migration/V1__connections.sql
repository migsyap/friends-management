CREATE TABLE connections (
  id            VARCHAR(50)   PRIMARY KEY,
  email1        VARCHAR(320)  NOT NULL,
  email2        VARCHAR(320)  NOT NULL,
  created_on    TIMESTAMP     NOT NULL,
  modified_on   TIMESTAMP     NOT NULL
);

CREATE INDEX connections__email1 ON connections (email1);
CREATE INDEX connections__email2 ON connections (email2);
CREATE UNIQUE INDEX connections__link ON connections (email1, email2);