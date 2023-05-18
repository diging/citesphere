Database setup
==============

.. autosummary::
   :toctree: generated

Create the database with the write character set.

    ``CREATE DATABASE citesphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;``

After tables have been created (app has started up), the following needs to be changed:

    ``alter table jv_snapshot modify state longtext;``