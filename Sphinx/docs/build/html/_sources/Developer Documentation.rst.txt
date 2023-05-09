Developer Documentation
=======================

.. autosummary::
   :toctree: generated


Tech Stack
----------
* MySQL
* MongoDB
* Apache Zookeeper
* Apache Kafka
* Eclipse IDE for Enterprise Java Developers
* MongoDB Compass
* Apache Tomcat 8.5.x

Project Local Setup
-------------------

   * Create Zotero account

      * After creating an account go to this link and create an application.  ``https://www.zotero.org/oauth/apps/new``

      * Give Application name: Citesphere dev, select Browser as Application type. Enter callback URL as ``http://localhost:8080/citesphere``. Click register application.
      
      * Keep client key and client secret ready.

   * Creating "settings.xml" file.

      * Create a file named "settings.xml" inside ``.m2`` folder. It is a hidden folder. To view it go to your home directory press ``command + shift +`` . All hidden files should show up.

      * Now open .m2 folder and create a file ``"settings.xml"``

      * create two folders uploadFiles and keep note of its path.

      * copy below code into it.

         ``<settings>
         <profiles>
         <profile>
         <id>mysql_username</id>
         <properties>
         <zotero.client.key>your_zotero_key</zotero.client.key>
         <zotero.client.secret>your_zotero_secret</zotero.client.secret>
         <upload.folder.path>/Users/your_path/uploadFiles</upload.folder.path>
         <token.signing.key>EsAxEfq/AGPGfPEJ/xEVSjVAU7H8GThexbfNnsrmaRYVREoxAJJEXDPKDO0GE87vhL1Z3OJz88CACqC4lKJ5TA==</token.signing.key>
         <db.database.url>jdbc:mysql://localhost:3306/citesphere</db.database.url>
         <db.user>mysql_db_username</db.user>
         <db.password>mysql_password</db.password>
         <app.url>http://localhost:8080/citesphere</app.url>
         <kafka.hosts>localhost:9092</kafka.hosts>
         <db.hibernate.dialect>org.hibernate.dialect.MySQL5InnoDBDialect</db.hibernate.dialect>
         </properties>
         </profile>
         </profiles>
         </settings>``

   * Start MySQL

      * Open terminal enter this command ``/usr/local/mysql/bin/mysql -u your_mysql_db_username -p``. 
        If you don't have any additional user other than root just give root as username. Press enter. Give your MySQL password.
      
      * create another user. Use this command. ``CREATE USER 'user_name'@'localhost' IDENTIFIED BY 'password';``
      
      * Grant all privileges.  ``GRANT ALL PRIVILEGES ON * . * TO 'user_name'@'localhost';``.
      
      * update new username and password in ``"settings.xml"`` file which is inside .m2 folder.
      
      * Type ``exit`` you will come out of mysql terminal.
      
      * Now start MySQL with new username and password you just created ``/usr/local/mysql/bin/mysql -u your_mysql_db_username -p``.
      
      * Create a database citesphere using this command. ``create database citesphere;`` press enter.
      
      * Use newly created database. ``use citesphere;`` press enter. You will get a message saying ``Database changed``.


   * Start Apache Kafka and Zookeeper.
      * To start Zookeeper open terminal and use this command. ``sudo /Users/your_path/zookeeper-3.3.6/bin/zkServer.sh start``. 
      * To start Kafka use this ``sudo /Users/your_path/kafka_2.11-1.1.1/bin/kafka-server-start.sh /Users/your_path/kafka_2.11-1.1.1/config/server.properties``  (there is space in between start.sh and /Users)


   1. Clone the citesphere repository in local from ``https://github.com/diging/citesphere``
   
   2. Import the project in eclipse → While importing make sure the import is done from the directory - ``{directory where the project is cloned}/citesphere``
   
   3. Open the ``Servers view`` (Window → Show View → Servers, or maybe ``Window → Show View → Other`` ...  and select ``Server``, then ``Open``) and click ``New →  Server``. 
   
   4. Now select ``Tomcat v8.5 Server`` and click ``Next``. Select the Tomcat installation directory and click ``Finish``.
   
   5. In the ``Servers`` view, it can be noted that the ``Tomcat v8.5 Server at localhost`` will be present. Double click on this. Click on the Timeouts section. Specify the start time limit as 500s and stop time limit as 15s and press ``Command + S/Ctrl + S`` to save.
   
   6. Right click on the citesphere project ``Maven → Select Maven Profiles``...Select the ``profile id`` and ensure that the id is same as the id specified in the ``settings.xml`` file in ./m2 folder. Click ``Ok``.
   
   7. In the servers view, right click on the ``Tomcat v8.5 Server at localhost`` and select ``Add and Remove``...If citesphere is not added, add the same and click Finish.
   
   8. Right click on ``Tomcat v8.5 Server at localhost`` and click ``Clean``....
   
   9. Right click on ``Tomcat v8.5 Server at localhost`` and click ``Start``....
   
   10. Once the server is started up and running, go to ``http://localhost:8080/citesphere`` and verify if the citesphere page is loaded.
   
   11. Now, click on ``Sign Up`` at the bottom and create a new user with your credentials.
   
   12. Then logout and login with the credentials username: admin and password: admin.
   
   13. Click on ``Users`` tab. The user that was created should appear here. Add the user and click on ``Make Admin``. Now, you can logout and login back with your own credentials.
   
   14. Click on ``Connect Zotero``. It will redirect the page to the Zotero application. Click on ``Accept defaults``.
   
   15. Now in the MySQL shell, you can find all the citesphere tables by using this command ``show tables;``
   
   16. Similarly in MongoDB Compass, click on connect (need not specify any credentials). The list of collections in citesphere can be seen.

Converting MySQL to UTF8mb4 encoding to handle special characters (Example: Č, ā)
---------------------------------------------------------------------------------

Change the character set of all the text/varchar columns to utf8mb4. Recommended collate: utf8mb4_unicode_ci

Note: utf8mb4 consumes 4 bytes as opposed to 3 bytes in UTF8. The varchar limit now reduces to 190 instead of 255 limit.

*Connection String example:* ``jdbc:mysql://localhost:3306/citespheredev?useUnicode=true&amp;characterEncoding=UTF-8``

Create database with:

CREATE DATABASE citesphere CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

``List of Tables and fields to be updated for Citesphere:``