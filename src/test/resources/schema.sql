drop schema if exists loader cascade;
create schema loader; 
set search_path to loader;


CREATE TABLE "exotic_types"(
 "login" Varchar NOT NULL,
 "countries" Bit(7) NOT NULL,
 "authorizations" Character varying(20)[] NOT NULL,
 "scores" Bigint[] NULL,
 "gpa" Bigint[] NULL,
 "status" Character(2) NOT NULL,
 "custom" varchar NOT NULL
)
WITH (OIDS=FALSE)
;

ALTER TABLE "exotic_types" ADD CONSTRAINT "Key1" PRIMARY KEY ("login")
;

CREATE TABLE "simple_type" (
"id" serial NOT NULL ,
"amount" integer NOT NULL
)
WITH (OIDS=FALSE)
;

ALTER TABLE "simple_type" ADD CONSTRAINT "Key2" PRIMARY KEY ("id")
;

CREATE TABLE "widget" (
    "id" serial not null ,
    "name" varchar not null,
    "login_type" varchar not null
) WITH (OIDS = FALSE)
;
    

ALTER TABLE "widget" ADD CONSTRAINT "Key4" PRIMARY KEY ("id")
;
