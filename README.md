This is your new Play application
=====================================

This file will be packaged with your application, when using `play dist`.

Play commands:

* Enter the play CLI: `$ play`
* Run the app: `[ghettotech] $ run`
* You can create an idea project with  `$ play idea`
* Debug the app with `$ play debug`
* To start with automatic recompile after save (notice the tilde): `$ play debug \~run` or `$ play \~run`
* Manual compilation with: `$ play compile`
* To create a new project: `$ play new <name>`


Heroku commands:

1. create a database for the app: `$ heroku addons:create heroku-postgresql`
2. connect to the database with `heroku pg:psql`
3. Add Postgres backups with `$ heroku addons:add pgbackups`


Install postgresql on your mack: `$ brew install postgresql`

Postgress DB maintenance:

* heroku addons:add pgbackups # To install the addon
* curl -o latest.dump `heroku pgbackups:url` # To download a dump
* pg_restore --data-only --file=out.sql latest.dump
* psql -f ./out.sql