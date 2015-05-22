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

* create a database for the app: `$ heroku addons:create heroku-postgresql`
* connect to the database with `heroku pg:psql`
* add Postgres backups with `$ heroku addons:add pgbackups`
* adding a domain `$ heroku domains:add www.ghettote.ch`

Install postgresql on OS X from http://postgresapp.com

Postgress DB maintenance:

* heroku addons:add pgbackups # To install the addon
* curl -o latest.dump `heroku pgbackups:url` # To download a dump
* pg_restore --data-only --file=out.sql latest.dump
* psql -f ./out.sql


TODO:

* how do you apply DB Migrations?



Regarding the integration between IntelliJ and Play:

* It is no longer necessary to create the module from play/activator.
* Delete all existing idea related directories
* Then in IntelliJ IDEA click File -> Open and choose your build.sbt file. That's all.

(http://stackoverflow.com/questions/16135716/how-to-use-intellij-with-play-framework-and-scala)