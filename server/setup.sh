#!/usr/bin/env bash

knex migrate:rollback
knex migrate:latest
knex seed:run
