Converting MySQL to UTF8mb4 encoding to handle special characters (Example: Č, ā)
=================================================================================

.. autosummary::
   :toctree: generated

Change the character set of all the text/varchar columns to utf8mb4. Recommended collate: utf8mb4_unicode_ci

*Note:* utf8mb4 consumes 4 bytes as opposed to 3 bytes in UTF8. The varchar limit now reduces to 190 instead of 255 limit.

**Connection String example:** 

``jdbc:mysql://localhost:3306/citespheredev?useUnicode=true&amp;characterEncoding=UTF-8``

**Create database with:**

CREATE DATABASE citesphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

List of Tables and fields to be updated for Citesphere:
-------------------------------------------------------

1. **Citation**

.. code-block:: mysql

   alter table Citation modify column title longtext character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table jv_snapshot modify column state text character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify seriesText longtext, modify seriesTitle longtext character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify pages varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify issue varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify url varchar(255), modify language varchar(255), modify archive varchar(255), modify archiveLocation varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify url varchar(255), modify rights varchar(255), modify callNumber varchar(255), modify libraryCatalog varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify volume varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify journalAbbreviation varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify publicationTitle longtext character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation character set utf8mb4 collate utf8mb4_unicode_ci;
   
   alter table Citation modify column shortTitle varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify column extra longtext character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify column dateFreetext varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify series varchar(255) character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify extra longtext character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify column abstractNote longtext character set utf8mb4 collate utf8mb4_unicode_ci null;
   
   alter table Citation modify column language longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

2. **Person**

.. code-block:: mysql

   alter table Person modify column name longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table Person modify column lastName longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table Person modify column firstName longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

3. **Affiliation**

.. code-block:: mysql

   alter table Affiliation modify column name longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

4. **CitationConcept**

.. code-block:: mysql

   alter table CitationConcept modify uri varchar(191), modify description longtext, modify name longtext, modify owner_username varchar(191) character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table CitationConceptTag modify conceptUri varchar(191), modify conceptName longtext, modify typeName longtext, modify typeUri varchar(191) character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table ConceptType modify description longtext, modify name longtext, modify owner_username varchar(191), modify uri varchar(191) character set utf8mb4 collate utf8mb4_unicode_ci null;`

   alter table ConceptType modify name longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

5. **AuthorityEntry**

.. code-block:: mysql

   alter table AuthorityEntry modify name longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table AuthorityEntry modify description longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table AuthorityEntry modify username varchar(191) character set utf8mb4 collate utf8mb4_unicode_ci null;

6. **Reference**

.. code-block:: mysql

   alter table Reference modify authorString longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table Reference modify referenceString longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table Reference modify referenceStringRaw longtext character set utf8mb4 collate utf8mb4_unicode_ci null;

   alter table Reference modify title longtext character set utf8mb4 collate utf8mb4_unicode_ci null;