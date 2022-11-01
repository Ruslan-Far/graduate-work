CREATE TABLE "users"
(
    "id" SERIAL PRIMARY KEY,
    "login" TEXT DEFAULT '' NOT NULL,
    "password" TEXT DEFAULT '' NOT NULL
);
CREATE TABLE "words"
(
    "id" SERIAL PRIMARY KEY,
    "userId" INTEGER REFERENCES "users" ("id"),
    "word" TEXT DEFAULT '' NOT NULL,
    "count" INTEGER NOT NULL
);
CREATE TABLE "collocations"
(
    "id" SERIAL PRIMARY KEY,
    "prevId" INTEGER REFERENCES "words" ("id"),
    "nextId" INTEGER REFERENCES "words" ("id"),
    "count" INTEGER NOT NULL
)