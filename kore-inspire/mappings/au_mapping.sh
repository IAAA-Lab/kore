#! /usr/bin/env zsh

cp ../templates/AdministrativeUnits.gpkg AdministrativeUnitsTarget.gpkg

ogr2ogr -f GPKG AdministrativeUnitsSrc.gpkg \
  PG:"dbname=userdb host=localhost user=postgres password=password" \
  -dsco version=1.2 \
  -dsco ADD_GPKG_OGR_CONTENTS=no \
  -lco ASPATIAL_VARIANT=GPKG_ATTRIBUTES \
  -lco SPATIAL_INDEX=no \
  -oo schemas=geoschema \
  -oo list_all_tables=yes \
  -oo active_schema=geoschema

sqlite3 --init au_mapping.sql AdministrativeUnitsTarget.gpkg