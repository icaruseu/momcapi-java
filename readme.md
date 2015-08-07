MOM-CA API
==========

This is an API to access and modify the data in a [MOM-CA Database](https://github.com/icaruseu/mom-ca).

Usage
-----

1. Create a new `MomcaConnection` object providing the URL of the databases' xmlrpc, e.g. `xmldb:exist://localhost:8181/xmlrpc` and admin credentials.
2. Use the various *manager* classes, e.g. `CharterManager`, provided by MomcaConnection to access and modify data.

Features
--------

Momcapi is not yet feature complete in regards of all features of MOM-CA. The following functionalities are available:

* Management of users
* Management of countries