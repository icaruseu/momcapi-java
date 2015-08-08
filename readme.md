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

* List all countries
* Get specific country from the database
* Get archive metadata (e.g. country, address, logo Url)
* Add new country
* Add new subdivision to country
* Delete country that has no attached archives
* Delete subdivision that has no attached archives

_Hierarchy manager_

* List all archives
* List all archives belonging to a specific country
* List all archvies belonging to a specific subdivision
* List all fonds belonging to a specific archive
* Get specific archive from the database
* Add new archive
* Delete archive

_Charter Manager_

* Get all instances (`imported`, `private`, `public` and `saved`) of a charter from the database
* Get specific charter instance, e.g. `saved` from the database
* Get charter medadata like the list of all figures
* Check if charter is a valid `CEI` document and list the validation problems if it's not