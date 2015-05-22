This is your new Play application
=====================================

This file will be packaged with your application, when using `play dist`.

`$ play`
`[ghettotech] $ run`

Play commands:

* You can create an idea project with  `$ play idea`
* Debug the app with `$ play debug`
* To start with automatic recompile after save (notice the tilde): `$ play debug \~run` or `$ play \~run`
* Manual compilation with: `$ play compile`
* To create a new project: `$ play new <name>`


Heroku commands:

1. create a database for the app: `$ heroku addons:create heroku-postgresql`
2. connect to the database with `heroku pg:psql`
3. Add Postgres backups with `$ heroku addons:add pgbackups`