DROP TABLE IF EXISTS comments CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS items CASCADE;
DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users (
  id_user BIGINT GENERATED ALWAYS AS IDENTITY,
  name_user VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  CONSTRAINT pk_user PRIMARY KEY (id_user),
  CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS items (
  id_item BIGINT GENERATED ALWAYS AS IDENTITY,
  name_item VARCHAR(255) NOT NULL,
  description VARCHAR(255) NOT NULL,
  available BOOL NOT NULL,
  owner_id INTEGER NOT NULL REFERENCES users(id_user),
  request_id INTEGER,
  CONSTRAINT pk_item PRIMARY KEY (id_item)
);

CREATE TABLE IF NOT EXISTS booking (
  id_booking BIGINT GENERATED ALWAYS AS IDENTITY,
  start_time TIMESTAMP WITHOUT TIME ZONE,
  end_time TIMESTAMP WITHOUT TIME ZONE,
  item_id INTEGER NOT NULL REFERENCES items(id_item),
  booker_id INTEGER NOT NULL REFERENCES users(id_user),
  status VARCHAR(255) NOT NULL,
  CONSTRAINT pk_booking PRIMARY KEY (id_booking)
);

create table if not exists comments (
    id_comment BIGINT GENERATED ALWAYS AS IDENTITY,
    text VARCHAR(512) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_comments PRIMARY KEY (id_comment),
    CONSTRAINT fk_comments_items FOREIGN KEY (item_id) REFERENCES items (id_item) ON DELETE CASCADE,
    CONSTRAINT fk_comments_users FOREIGN KEY (author_id) REFERENCES users (id_user)
);