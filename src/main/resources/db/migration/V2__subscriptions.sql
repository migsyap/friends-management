CREATE TABLE subscriptions (
  id            VARCHAR(50)   PRIMARY KEY,
  requestor     VARCHAR(320)  NOT NULL,
  target        VARCHAR(320)  NOT NULL,
  created_on    TIMESTAMP     NOT NULL,
  modified_on   TIMESTAMP     NOT NULL
);

CREATE INDEX subscriptions__requestor ON subscriptions (requestor);
CREATE INDEX subscriptions__target ON subscriptions (target);
CREATE UNIQUE INDEX subscriptions__link ON subscriptions (requestor, target);