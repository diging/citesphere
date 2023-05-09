API
===

.. autosummary::
   :toctree: generated

   lumache

This documentations documents version 1 of the Citesphere API. All endpoints are prefixed with ``/api/v1``


Authentication
--------------

Citesphere requires OAuth2 token for accessing the endpoints. Checkout ``OAuth2 Documentation`` to know more about retrieving the ``access_token`` 
(a.k.a Bearer token). You should add the below header for accessing any endpoint.

   Header Name: ``Authorization``
   
   Header Value: ``Bearer <access_token>,`` example: ``Bearer 05fa6b64-a5ee-4cce-78bb-cc492e1d73a7``


Test Endpoint
-------------

This endpoint returns all the available REST endpoints.

Example response:

.. code-block:: console

   {
    {
        "user": "myusername"
    },
    {
        "info": "{ /api/v1/test}"
    },
    {
        "info": "{ /api/v1/upload}"
    },
    ...
   }

Groups Endpoint
---------------

The groups endpoint returns all the groups the authenticating user has access to. 

Example response:

.. code-block:: console

   [
    {
        "id": 1234,
        "name": "dataset",
        "version": 2,
        "created": "2020-06-02T18:40:57Z",
        "lastModified": "2020-06-10T18:30:24Z",
        "numItems": 345,
        "owner": 111,
        "type": "Private",
        "description": "",
        "url": "",
        "syncInfo": null
    },
    {
        "id": 56789,
        "name": "Shared Group",
        "version": 3,
        "created": "2019-08-15T18:02:36Z",
        "lastModified": "2019-08-15T18:12:40Z",
        "numItems": 5555,
        "owner": 1123,
        "type": "Private",
        "description": "",
        "url": "",
        "syncInfo": null
    }
   ]

Groups Endpoint
---------------

This endpoint returns information about a specific group. The returned information will also include sync information for a group.

Example response:

.. code-block:: console

   {
    "id": 1234,
    "name": "dataset",
    "version": 18,
    "created": "2018-11-19T21:34:21Z",
    "lastModified": "2020-09-22T21:11:07Z",
    "numItems": 37,
    "owner": 1111,
    "type": "Private",
    "description": "<p>This is for testing Citesphere.</p>",
    "url": "",
    "syncInfo": {
        "createdOn": "2020-09-30T13:17:26-04:00",
        "status": "DONE"
    }
   }

Collections Endpoint
--------------------

The collections endpoint returns all collections in a group or in a collection.

Endpoint URL get ``/groups/{groupId}/collections, /groups/{groupId}/collections/{collectionId}/collections``

   ``parameters``

      ``maxCollectionNumber`` The returned number of collections can be limited using this parameter.

Example Response:

.. code-block:: console

   {
    "group": {
        "id": 1234,
        "name": "test",
        "version": 18,
        "created": "2018-11-19T21:34:21Z",
        "lastModified": "2020-09-22T21:11:07Z",
        "numItems": 37,
        "owner": 1111,
        "type": "Private",
        "description": "<p>This is for testing citesphere.</p>",
        "url": "",
        "syncInfo": {
            "createdOn": "2020-09-30T13:17:26-04:00",
            "status": "DONE"
        }
    },
    "collections": [
        {
            "id": {
                "timestamp": 1601486250,
                "date": 1601486250000
            },
            "key": "FG78888",
            "version": 291,
            "contentVersion": 0,
            "numberOfCollections": 0,
            "numberOfItems": 0,
            "name": "hello",
            "parentCollectionKey": "AAA4444",
            "lastModified": null,
            "groupId": "1234"
        }
    ]
   }

Items Endpoint
--------------

The items endpoint returns the items in a group or collection.

Endpoint URL get ``/groups/{groupId}/items, /groups/{groupId}/collections/{collectionId}/items``

``parameters``

      ``page`` The page to be returned. A page has at most 50 items.

      ``sort`` ``EXPERIMENTAL`` How the items should be sorted. Default is ``title``. This feature is experimental.

Example Response:

.. code-block:: console

   {
    "group": {
        "id": 1123,
        "name": "My Group",
        "version": 4,
        "created": "2018-09-10T20:04:46Z",
        "lastModified": "2019-06-20T12:01:29Z",
        "numItems": 2172,
        "owner": 1111,
        "type": "Private",
        "description": "",
        "url": "",
        "syncInfo": {
            "createdOn": "2020-09-30T15:55:18-04:00",
            "status": "DONE"
        }
    },
    "items": [
        {
            "key": "CXCXX",
            "group": "1123",
            "version": 4873,
            "title": "THE EFFECTS OF A DIET OF POLISHED AND OF UNPOLISHED RICE UPON THE METABOLIC ACTIVITY OF PARAMECIUM",
            "authors": [
                {
                    "@type": "Person",
                    "name": "Mary Drusilla Flather",
                    "uri": null,
                    "localAuthorityId": null,
                    "firstName": "Mary Drusilla",
                    "lastName": "Flather",
                    "positionInList": 0,
                    "affiliations": null
                }
            ],
            "editors": [],
            "otherCreators": [],
            "itemType": "JOURNAL_ARTICLE",
            "publicationTitle": "",
            "volume": "36",
            "issue": "",
            "pages": "",
            "date": "1919-01-01T00:00Z",
            "dateFreetext": "1919",
            "series": "",
            "seriesTitle": "",
            "url": "",
            "abstractNote": "",
            "accessDate": "",
            "seriesText": "",
            "journalAbbreviation": "",
            "language": "",
            "doi": null,
            "issn": null,
            "shortTitle": "",
            "archive": "",
            "archiveLocation": "",
            "libraryCatalog": "",
            "callNumber": "",
            "rights": "",
            "collections": [
                "V5B7E56T"
            ],
            "dateAdded": "2020-07-09T21:20:03Z",
            "dateModified": null,
            "conceptTagIds": null,
            "conceptTags": null,
            "references": null,
            "extra": "Bryn Mawr",
            "otherCreatorRoles": []
        },
        ...
      }
   ]
   }

