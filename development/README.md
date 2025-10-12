# Development Database Setup

This guide explains how to set up and use the PostgreSQL database for development using Docker.

## Prerequisites

- Docker installed on your machine
- Docker Compose installed on your machine
- Basic understanding of Docker and PostgreSQL

## Quick Start

1. Start the database:

    ```bash
    docker-compose -f database-compose.yml up -d
    ```

2. Stop the database:

    ```bash
    docker-compose -f database-compose.yml down
    ```

## Database Configuration

Default configuration (can be overridden with environment variables):

- **Database Name**: psgp_db
- **Username**: postgres
- **Password**: postgres
- **Port**: 5432
- **Host**: localhost (when connecting from your machine)
- **Host**: postgres (when connecting from other Docker containers)

### Environment Variables

You can customize the database configuration by creating a `.env` file in the same directory as `database-compose.yml`:

```env
POSTGRES_DB=your_database_name
POSTGRES_USER=your_username
POSTGRES_PASSWORD=your_secure_password
```

## Data Persistence

The database data is stored in a Docker volume named `psgp_postgres_data`. This means:

- Data persists between container restarts
- Data remains even if you remove the container
- Data is only deleted if you explicitly remove the volume

### Managing Data

1. View the volume:

```bash
docker volume ls | grep psgp_postgres_data
```

2. Remove the volume (⚠️ This will delete all data):

```bash
docker-compose -f database-compose.yml down -v
```

## Connecting to the Database

### From Your Application

Use these connection details in your application's configuration:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/psgp_db
spring.datasource.username=postgres
spring.datasource.password=postgres
```

### Using psql CLI

Connect to the database using psql inside the container:

```bash
docker exec -it psgp_postgres psql -U postgres -d psgp_db
```

### Using Database Tools

You can connect using tools like DBeaver or pgAdmin with these details:

- Host: localhost
- Port: 5432
- Database: psgp_db
- Username: postgres
- Password: postgres

## Troubleshooting

### Common Issues

1. **Port Conflict**

   ```bash
   Error: Ports are not available: listen tcp 0.0.0.0:5432: bind: address already in use
   ```
   Solution: Stop any existing PostgreSQL instances or change the port in database-compose.yml

2. **Permission Issues**

   ```bash
   Error: permission denied to create database
   ```
   Solution: Check that the POSTGRES_USER has appropriate privileges

3. **Container Won't Start**

   ```bash
   Check the logs: docker-compose -f database-compose.yml logs
   ```

### Useful Commands

1. View container logs:

```bash
docker-compose -f database-compose.yml logs -f
```

2. Restart the container:

```bash
docker-compose -f database-compose.yml restart
```

3. Check container status:

```bash
docker-compose -f database-compose.yml ps
```

## Development Best Practices

1. **Always use environment variables** for sensitive information
2. **Backup your data** regularly during development
3. **Don't use the default credentials** in production
4. **Version control your database schema changes** using initialization scripts

## Additional Resources

- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [Docker PostgreSQL Image](https://hub.docker.com/_/postgres)
- [Docker Compose Documentation](https://docs.docker.com/compose/)

## Contributing

1. Keep initialization scripts idempotent (safe to run multiple times)
2. Document any schema changes
3. Update this README when adding new features or configurations

## Security Notice

⚠️ The default configuration is for development only. For production:

- Use strong passwords
- Restrict network access
- Enable SSL
- Follow security best practices
