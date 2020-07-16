# Evaluation of AU

## Setup the workspace

Let's create a directory on my computer to work on (e.g. `AU evaluation`).

## Setup the environment

First we try with this configuration (`stack.yml`).

```yml
version: '3.1'

services:

  db:
    image: crunchydata/crunchy-postgres-gis:centos7-11.4-2.4.0
    environment:
      PG_MODE: primary
      PG_PRIMARY_USER: primaryuser
      PG_PRIMARY_PASSWORD: password
      PG_DATABASE: userdb
      PG_USER: testuser
      PG_PASSWORD: password
      PG_ROOT_PASSWORD: password
      PG_PRIMARY_PORT: 5432
    volumes: 
      - ./pgvolume:/pgdata
      - .:/backups
    ports:
      - 5432:5432

  pga4:
    image: dpage/pgadmin4:4.1
    restart: on-failure
    environment:
      PGADMIN_SETUP_EMAIL: testuser@unizar.es
      PGADMIN_SETUP_PASSWORD: password
      PGADMIN_DEFAULT_EMAIL: testuser@unizar.es
      PGADMIN_DEFAULT_PASSWORD: password
      PGADMIN_LISTEN_PORT: 5050
    ports:
      - 5050:5050
    depends_on:
      - "db"
```

Run it with `docker-compose -f stack.yml up`. We can check the access to the instance of the database.

```bash
docker exec -ti auevaluation_db_1 sh
```

Test if it works:

```bash
psql -h localhost -U postgres
postgres=# GRANT USAGE ON SCHEMA geoschema TO testuser
postgres-# \q
```

## Data import

First replace `$$PATH$$` by `/backups`. Then execute:

```bash
psql -h localhost -U postgres userdb -f /backups/restore.sql
```

The structure is the expected:

- adminboundary
- adminunit
- admunit_boundary
- boundary_admunit
- lowlevelunit
- upperlevelunit

## PG to GPKG

```
ogr2ogr -f GPKG ori.gpkg PG:"dbname=userdb host=localhost user=postgres password=password" -dsco version=1.2 -dsco ADD_GPKG_OGR_CONTENTS=no -lco ASPATIAL_VARIANT=GPKG_ATTRIBUTES -lco SPATIAL_INDEX=no -oo schemas=geoschema -oo list_all_tables=yes -oo active_schema=geoschema
```