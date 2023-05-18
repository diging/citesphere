OAuth2 Documentation
====================

.. autosummary::
   :toctree: generated

Citesphere provides OAuth2 for handling authentication and authorization. You can use any form of OAuth2 Client to get a token.

If you are developing an application and would like to use Citesphere to authenticate your users, please refer to  ``Using Citesphere to Authenticate Users for Single Sign-On.``

Creating an App
---------------

Any application that would like to use Citesphere for authentication and authorization first needs to be registered with Citesphere.

1. Login to Citesphere as admin.

2. Go to Apps and add a new app.

3. You should see a form like this:

.. image:: sigin.png
  :width: 800
  :alt: Alternative text

4. Provide the requested information:

   * App Name: a descriptive name of the app to be registered (e.g. the name of the app such as “Vogon”).

   * Description: a short description of the apps (does not have to be longer than a sentence).

   * Application Type:
      * Apps need user information (Authorization Code): for any app that needs to authenticate specific users (e.g. if you want to use 
      Citesphere for user authentication).

      * Apps do not need user information (Client Credentials): for any app that do not want to log in users but need a way to talk to Citesphere.

   * Redirect URL: a url to redirect to after Citesphere has processed a users authentication.

5. Click “Add”

6. You should see something like this:

.. image:: doctest.png
  :width: 800
  :alt: Alternative text

Make sure to copy the client secret and keep it in a safe place. Once you navigate away from this page, there is no way to retrieve 
this information.

Regenerating Client Secrets
---------------------------
If for some reason you loose a client secret or you want to revoke access to the app using the current client id and 
secret, you can regenerate a client secret. To do this navigate to the page of the app in Citesphere and click “Regenerate Secret.” A new 
secret will be created and shown.

   *Note* that once you have regenerated a secret, any app that uses the old secret will not be able to use 
   Citesphere’s API (including OAuth) any longer.


Using Citesphere to Authenticate Users for Single Sign-On
---------------------------------------------------------

You can use any form of OAuth Client to get the token, which needs to be passed to other APIs as a header.

1. **Authorizing Application**

**Prerequisites**

* You should have created an application in Citesphere. If not, create one! Make sure to choose “Apps need user information (Authorization Code)” as application type and to provide a url to which Citesphere should redirect after authorizing a user in “Redirect URL.”

* You should have the following information for the application with you:
    * client_id - A unique identifier for your application. This is auto-generated for you during the application creation.
    
    * Example: OAUTHCLIENT007
    
    * client_secret - An auto-generated secret identifier which will be visible only right after you create the application. If you had lost it, go back and regenerate the secret.
    
    * redirect_url - You should have given a callback URL while creating the application in Citesphere. If you forgot this, you can check it back in Citesphere.

**Authorization Flow**

From your application, redirect your user to the following URL with specified parameters. Maybe have a button that says  
``LOGIN VIA CITESPHERE`` which has the hyperlink.

``ENDPOINT URL`` ``GET`` ``/api/oauth/authorize``

**Query Parameters**

.. list-table:: 
   :widths: 25 25 50
   :header-rows: 1

   * - **Name**
     - **Type**
     - **Description**
   * - ``client_id``
     - ``string``
     - **Required.** The client ID you received from Citesphere for your App.
   * - ``scope``
     - ``string``
     - **Required.** A space-delimited list of scopes. 
     
       Example: ``read``
   * - ``response_type``
     - ``string``
     - **Required.** Tells the authorization server which grant to execute.
       
       Example: ``code`` (In our case)
   * - ``state``
     - ``string``
     - An unguessable random string. It is used to protect against cross-site request forgery attacks.

       You will need to use the same ``state`` for the **Get Access Token** flow if you use the ``code`` returned by this request as it’s query parameter.

This request will take you to Citesphere, where the user will enter the credentials and 
``APPROVE`` or  ``DENY`` your application.

If the user ``APPROVES`` your application, Citesphere will redirect you back to your application’s ``redirect_url`` with the following **query parameters.**

.. list-table:: 
   :widths: 25 25 50
   :header-rows: 1

   * - **Name**
     - **Type**
     - **Description**
   * - ``code``
     - ``string``
     - A unique string you should use to get the ``access_token`` in the next step
   * - ``state``
     - ``string``
     - The same ``string`` you provided in the previous request.
   
**Example:** ``https://<your_app_redirect_url>?code=xyz123&state=mystate``

    **Note** Note that this step happens in the browser, initiated by your application’s user. That means ``code`` and ``state`` are visible in the address bar. 

    Your application should have a controller in the backend to retrieve ``code`` and ``state`` from ``<your_app_redirect_url endpoint>``. 
    That way, you can use ``code`` and ``state`` to get the ``access_token`` from your backend.

2. **Get Access Token**

    This step should **NOT** be done in the browser. Why? You have to pass your ``client_secret`` for getting the ``access_token``. 

    At any cost, you should **NOT** expose your ``client_secret`` to your user.

``ENDPOINT URL`` ``POST`` ``/api/oauth/token``

**Query Parameters**

.. list-table:: 
   :widths: 25 25 50
   :header-rows: 1

   * - **Name**
     - **Type**
     - **Description**
   * - ``client_id``
     - ``string``
     - **Required.** The client ID you received from Citesphere for your App.
   * - ``client_secret``
     - ``string``
     - **Required.** The client secret you received from Citesphere for your App.
   * - ``code``
     - ``string``
     - **Required.** The code you received as a response in the previous step
   * - ``redirect_uri``
     - ``string``
     - The URL of the application you configured in Citesphere
   * - ``state``
     - ``string``
     - An unguessable random string. It is used to protect against cross-site request forgery attacks.
   * - ``grant_type``
     - ``string``
     - **Required.** Use ``authorization_code`` for retrieving an ``access_token``.
     
       For a list of values, check ``https://auth0.com/docs/applications/application-grant-types``

**Response**

|    ``{``
|       ``"access_token": "2c7c0f10-adf5-ed55-a931-caeea29464ee",``
|       ``"token_type": "bearer",``
|       ``"refresh_token": "0d06219a-1b49-7895-9220-ef3b9810f09d",``
|       ``"expires_in": 406,``
|       ``"scope": "read"``
|    ``}``
    
* ``expires_in`` specifies the number of seconds remaining for the ``access_token`` to expire.

* You should use the ``access_token`` as the **Bearer token** header for accessing any resource. 
    * Header Name - ``Authorization``
    * Header Value -  ``Bearer 2c7c0f10-adf5-ed55-a931-caeea29464ee``

* You should use the ``refresh_token`` in order to get a new ``access_token`` once it is expired

3. **Refresh Token**

You would need to call this API for getting a new ``access_token`` if it expired. Your application should 
ideally store the refresh_token generated previously (ex. database). Use the ``refresh_token`` to retrieve a new ``access_token``. 

Of course, you should pass ``client_id`` and ``client_secret`` similar to the previous request.

``ENDPOINT URL`` ``POST`` ``/api/oauth/token``

.. list-table:: 
   :widths: 25 25 50
   :header-rows: 1

   * - **Name**
     - **Type**
     - **Description**
   * - ``client_id``
     - ``string``
     - **Required.** The client ID you received from Citesphere for your App.
   * - ``client_secret``
     - ``string``
     - **Required.** The client secret you received from Citesphere for your App.
   * - ``refresh_token``
     - ``string``
     - **Required.** Use the ``refresh_token`` you got from the previous step.

       Example: ``0d06219a-1b49-7895-9220-ef3b9810f09d``
   * - ``grant_type``
     - ``string``
     - **Required.** Use ``refresh_token`` in this scenario

Example: ``POST /api/oauth/token?client_id=OAUTHCLIENT007&client_secret=xyz&refresh_token=
0d06219a-1b49-7895-9220-ef3b9810f09d&grant_type=refresh_token``

**Response**
 
| ``{``
|    ``"access_token": "c322172e-16ac-8952-95e7-19639745bbaf",``
|    ``"token_type": "bearer",``
|    ``"refresh_token": "0d06219a-1b49-7895-9220-ef3b9810f09d",``
|    ``"expires_in": 3600,``
|    ``"scope": "read"``
| ``}``
You can use the newly generated ``access_token`` for accessing resources.

Using Citesphere from a Resource Provider
-----------------------------------------

If you do not need users to directly logging into your application but you do want to authenticate (and potentially authorize) users using Citesphere token, you should follow below steps.

Also refer to the corresponding RFC 7662: ``https://tools.ietf.org/html/rfc7662``

**Set up your Application with Citesphere**
First you will need to register your application as described in **OAuth2 Documentation.**

Make sure to select “Client Credentials” as application type and note down client id and client secret.

**Checking a Token**

Once you receive a token from an application to access your service, you can let Citesphere check that token. The endpoint for token introspection is as follows.

``ENDPOINT`` ``POST`` ``/api/oauth/check_token``

**PARAMETERS**
    ``token`` The token to check.

You will need to provide Authorization client id and secret of your app as Basic Auth (Authorization header) to access this endpoint.

Citesphere will respond with some metadata (as JSON) about the passed token. In case of a valid token, the response will look something like this:

   |  ``{``
   |    ``"active": true,``
   |    ``"exp": 1603293618,``
   |    ``"user_name": "myuser",``
   |    ``"authorities": [``
   |        ``"ROLE_USER"``
   |    ``],``
   |    ``"client_id": "OAUTHCLIENT007",``
   |    ``"scope": [``
   |        ``"read"``
   |    ``]``
   |  ``}``


where 

``active`` if the token is currently active

``exp`` time when token will expire (according to RFC 7662 “measured in the number of seconds since January 1 1970 UTC”).

``user_name`` username of the user authenticated with the passed token. 

``authorities`` list of all roles the user has.

``client_id`` the id of the client that authorized the user.

``scope`` list of scopes that token has been authorized for.

If a token has expired, you will get a response like this:

|    ``{``
|       ``"error": "invalid_token",``
|       ``"error_description": "Token has expired"``
|    ``}``