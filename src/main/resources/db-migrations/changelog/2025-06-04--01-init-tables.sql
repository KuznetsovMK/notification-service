--liquibase formatted sql

--changeset Kuznetsov.Mikhail:1

CREATE SCHEMA IF NOT EXISTS notification_service AUTHORIZATION postgres;

CREATE TABLE IF NOT EXISTS notification_service.events (
	id int8 NOT NULL,
	message varchar(255) NULL,
	occurred_at timestamptz DEFAULT CURRENT_TIMESTAMP NOT NULL,
	CONSTRAINT event_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS notification_service.users (
	id int8 NOT NULL,
	full_name varchar NOT NULL,
	notification_schedule jsonb NULL,
	CONSTRAINT user_pk PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS notification_service.user_events (
	user_id int8 NOT NULL,
	event_id int8 NOT NULL,
	CONSTRAINT user_events_pk PRIMARY KEY (user_id, event_id),
	CONSTRAINT user_events_events_fk FOREIGN KEY (event_id) REFERENCES notification_service.events(id) ON DELETE CASCADE,
	CONSTRAINT user_events_users_fk FOREIGN KEY (user_id) REFERENCES notification_service.users(id) ON DELETE CASCADE
);