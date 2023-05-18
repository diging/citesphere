Using Citesphere from a Resource Provider
=========================================

.. autosummary::
   :toctree: generated

If you do not need users to directly logging into your application but you do want to authenticate (and potentially authorize) users using Citesphere token, you should follow below steps.

Also refer to the corresponding RFC 7662: ``https://tools.ietf.org/html/rfc7662``

Set up your Application with Citesphere
---------------------------------------

First you will need to register your application as described in **OAuth2 Documentation.**

Make sure to select “Client Credentials” as application type and note down client id and client secret.

Checking a Token
----------------

Once you receive a token from an application to access your service, you can let Citesphere check that token. The endpoint for token introspection is as follows.

``ENDPOINT`` ``POST`` ``/api/oauth/check_token``

**PARAMETERS**

    ``token`` The token to check.

You will need to provide Authorization client id and secret of your app as Basic Auth (Authorization header) to access this endpoint.

Citesphere will respond with some metadata (as JSON) about the passed token. In case of a valid token, the response will look something like this:

    {
    "active": true,
    "exp": 1603293618,
    "user_name": "myuser",
    "authorities": [
        "ROLE_USER"
    ],
    "client_id": "OAUTHCLIENT007",
    "scope": [
        "read"
    ]
    }


where 

``active`` if the token is currently active

``exp`` time when token will expire (according to RFC 7662 “measured in the number of seconds since January 1 1970 UTC”).

``user_name`` username of the user authenticated with the passed token. 

``authorities`` list of all roles the user has.

``client_id`` the id of the client that authorized the user.

``scope`` list of scopes that token has been authorized for.

If a token has expired, you will get a response like this:

    ``{
    "error": "invalid_token",
    "error_description": "Token has expired"
    }``