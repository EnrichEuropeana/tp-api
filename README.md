# tp-api
Api for the [Transcribathon tool](https://europeana.transcribathon.eu) 

## License

Licensed under the EUPL V.1.2

For full details, see [License.md](License.md)

## Set up

Create a config.properties file in the resource package containing following properties:

 - PASS=[your database password]
 - DB_URL=[your jdbc url in format: *jdbc:driver://host:port/databaseName;URLAttributes*]
 - USER=[your database user]
 - SECRET_KEY=[needs to be requested, it's needed for the DEI-API]
 - DRIVER=[your jdbc driver e.g. *com.mysql.jdbc.Driver*]

The Transcribathon Project is using a MySQL 5.7 database instance. 


