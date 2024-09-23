DROP TABLE IF EXISTS order_coffee;
DROP TABLE IF EXISTS "order";
DROP TABLE IF EXISTS coffee;
DROP TABLE IF EXISTS barista;

CREATE TABLE IF NOT EXISTS barista(
	"id" bigserial not null primary key,
	full_name varchar(100) not null,
	tip_size float8 not null
);

CREATE TABLE IF NOT EXISTS coffee(
	"id" bigserial not null primary key,
	"name" varchar(100) not null,
	price float8 not null
);
CREATE TABLE IF NOT EXISTS "order"(
	"id" bigserial not null primary key,
	barista bigserial not null references barista("id"),
	created timestamp not null,
	completed timestamp,
	price float8 not null
);
CREATE TABLE IF NOT EXISTS order_coffee(
	order_id bigserial not null references "order"("id"),
	coffee_id bigserial not null references coffee("id")
);

CREATE UNIQUE INDEX unique_order_coffee ON order_coffee (order_id, coffee_id)