MOM-CA API
==========

This is an API to access and modify the data in a [MOM-CA Database](https://github.com/icaruseu/mom-ca).

Usage
-----

1. Create a new `MomcaConnection` object providing the URL of the databases' xmlrpc, e.g. `xmldb:exist://localhost:8181/xmlrpc` and admin credentials.
2. Use the various *manager* classes, e.g. `CharterManager`, provided by MomcaConnection to access and modify data.

To run the unit tests, a new MOM-CA instance must be created and the backup data in `/src/test/resources/test_database_backup.zip` needs to be restored.

Features
--------

Momcapi is not yet feature complete in regards of all features of MOM-CA. The following functionalities are available:

_User manager_

* List all users
* Get specific user from the database
* Add new user
* Delete existing user
* Change moderator
* Change password
* Initialize user (for instance if the user didn't get the confirmation link after sign-up

_Country manager_

* List all countries (used in archival fonds and collections)
* Get specific country from the database
* Add new country to static hierarchy (used for archival fonds)
* Add new region to hierarchical countries
* Delete countries from the hierarchy
* Delete regions from hierarchical countries

_Archive manager_

* List all archives
* List all archives belonging to a specific country
* List all archvies belonging to a specific region
* Get specific archive from the database
* Get archive metadata (e.g. country, address, logo Url)
* Add new archive
* Delete archive

_Fond manager_

* List all fonds belonging to a specific archive
* Get specific fond from the database
* Add new fond
* Delete fond

_Collection manager_

* List all collections
* List all collections belonging to a specific country
* List all collections belonging to a specific region
* Get a specific collection from the database

_Charter Manager_

* List all charter instances (`imported`, `private`, `public` and `saved`) for fonds, collections, mycollections and users
* Get all instances (`imported`, `private`, `public` and `saved`) of a charter from the database
* Get specific charter instance, e.g. `saved` from the database
* Get charter medadata like the list of all figures
* Check if charter is a valid `CEI` document and list the validation problems if it's not