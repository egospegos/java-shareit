DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS requests;
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS comments;

CREATE TABLE IF NOT EXISTS users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  email VARCHAR(512) NOT NULL,
  UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  description VARCHAR(512) NOT NULL,
  requester_id BIGINT,
  CONSTRAINT fk_requests_to_users FOREIGN KEY(requester_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  description VARCHAR(512) NOT NULL,
  is_available BOOLEAN NOT NULL,
  owner_id BIGINT,
  --request_id BIGINT,
  CONSTRAINT fk_items_to_users FOREIGN KEY(owner_id) REFERENCES users(id)
  --CONSTRAINT fk_items_to_requests FOREIGN KEY(request_id) REFERENCES requests(id)
);


CREATE TABLE IF NOT EXISTS bookings (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  start_date timestamp,
  end_date timestamp,
  item_id BIGINT,
  booker_id BIGINT,
  status varchar(50),
  CONSTRAINT fk_bookings_to_items FOREIGN KEY(item_id) REFERENCES items(id),
  CONSTRAINT fk_bookings_to_users FOREIGN KEY(booker_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS comments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  text VARCHAR(512) NOT NULL,
  item_id BIGINT,
  author_id BIGINT,
  created timestamp,
  CONSTRAINT fk_comments_to_items FOREIGN KEY(item_id) REFERENCES items(id),
  CONSTRAINT fk_comments_to_users FOREIGN KEY(author_id) REFERENCES users(id)
);

