# tp-api
Api for the [Transcribathon tool](https://europeana.transcribathon.eu) 

## License

Licensed under the EUPL V.1.2

For full details, see [License.md](License.md)

## Technical background

The Transcribathon Project uses a MySQL 5.7 database instance. The API is connected to the *transcribathon* database. It runs with a Tomcat v9 server.

Java Version 8

## Set up

Create a `config.properties` file in the resource package containing following properties:

 - PASS=[your database password]
 - DB_URL=[your jdbc url in format: *jdbc:driver://host:port/databaseName;URLAttributes*]
 - USER=[your database user]
 - SECRET_KEY=[needs to be requested, it's needed for the DEI-API; if you don't use DEI you can leave it blank]
 - DRIVER=[your jdbc driver e.g. *com.mysql.jdbc.Driver*]
 - SOLR=[your solr server]
 
Make sure to configure your e.g. Apache webserver as a reverse proxy between for Tomcat server. Should look like this:
 
`ProxyPass         /tp-api  http://localhost:8080/tp-api`  
`ProxyPassReverse  /tp-api  http://localhost:8080/tp-api`

For deploying use either an IDE like Eclipse and export a war-file or use a build tool like *ant*. 

If you choose *ant* you can use the existing build.xml in the project. Run `ant -f build.xml`. The war-file is copied to dist-dir inside the project. 

Copy your war-file to e.g. Tomcat webapps-dir and restart the server.


