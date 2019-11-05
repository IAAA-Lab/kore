-- TODO 1..N must be views registered as attributes over the base data table
-- TODO N..M must be an attribute table

.echo off

attach database 'AdministrativeUnitsSrc.gpkg' as src;

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/codelist/CountryCode'
where constraint_name = 'base2_countrycode';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/codelist/AdministrativeHierarchyLevel'
where constraint_name = 'au_administrativehierarchylevel';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/codelist/AdministrativeHierarchyLevel'
where constraint_name = 'au_administrativehierarchylevel';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/enumeration/LegalStatusValue/' || value
where constraint_name = 'au_legalstatusvalue';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/enumeration/TechnicalStatusValue/' || value
where constraint_name = 'au_technicalstatusvalue';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/codelist/GrammaticalGenderValue'
where constraint_name = 'gn_grammaticalgendervalue';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/codelist/GrammaticalNumberValue'
where constraint_name = 'gn_grammaticalnumbervalue';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.europa.eu/codelist/NativenessValue'
where constraint_name = 'gn_nativenessvalue';

update gpkg_data_column_constraints
set description = 'http://inspire.ec.eu ropa.eu/codelist/NameStatusValue'
where constraint_name = 'gn_namestatusvalue';

update gpkg_data_column_constraints
set description = 'http://www.loc.gov/standards/iso639-2/'
where constraint_name = 'gmd_languagecode';

update gpkg_data_column_constraints
set description = 'https://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#MD_CharacterSetCode_' || value
where constraint_name = 'md_charactersetcode'
  and value not like '(%';

update gpkg_data_column_constraints
set description = 'https://www.isotc211.org/2005/resources/Codelist/gmxCodelists.xml#MD_CharacterSetCode'
where constraint_name = 'md_charactersetcode'
  and value like '(%';

--
-- Feature "AU_AdministrativeUnit" {
--     tableName = "AU_AdministrativeUnit"
--     id INTEGER NOT NULL PRIMARY KEY
--     country TEXT NOT NULL CHECK(BASE2_CountryCode)
--     geometry MULTISURFACE NOT NULL
--     beginLifespanVersion DATETIME
--     endLifespanVersion DATETIME
--     nationalCode TEXT NOT NULL
--     nationalLevel TEXT NOT NULL CHECK(AU_AdministrativeHierarchyLevel)
-- }
--

update gpkg_geometry_columns
set (geometry_type_name, srs_id, z, m) = (SELECT geometry_type_name, 0, z, m
                                          FROM src.gpkg_geometry_columns
                                          WHERE table_name = 'adminunit')
where table_name = 'AU_AdministrativeUnit';

update gpkg_contents
set (min_x, min_y, max_x, max_y, srs_id) = (SELECT min_x, min_y, max_x, max_y, 0
                                            FROM src.gpkg_contents
                                            WHERE table_name = 'adminunit')
where table_name = 'AU_AdministrativeUnit';

insert into AU_AdministrativeUnit(id, country, geometry, nationalCode, nationalLevel, inspireId, beginLifespanVersion,
                                  endLifespanVersion)
select fid,
       country,
       the_geom,
       nationalcode,
       nationallevel,
       null,
       null,
       null
from src.adminunit;

update AU_AdministrativeUnit
set nationalLevel = '1stOrder'
where nationalLevel = 'http://inspire.ec.europa.eu/codelist/AdministrativeHierarchyLevel/1stOrder';

update AU_AdministrativeUnit
set nationalLevel = '2ndOrder'
where nationalLevel = 'http://inspire.ec.europa.eu/codelist/AdministrativeHierarchyLevel/2ndOrder';

update AU_AdministrativeUnit
set nationalLevel = '3rdOrder'
where nationalLevel = 'http://inspire.ec.europa.eu/codelist/AdministrativeHierarchyLevel/3rdOrder';

update AU_AdministrativeUnit
set nationalLevel = '4thOrder'
where nationalLevel = 'http://inspire.ec.europa.eu/codelist/AdministrativeHierarchyLevel/4thOrder';

update AU_AdministrativeUnit
set upperLevelUnit = (select upper.id
                      from src.upperlevelunit,
                           AU_AdministrativeUnit upper
                      where AU_AdministrativeUnit.nationalCode = upperlevelunit.nationalcode
                        and upper.nationalCode = upperlevelunit.upperlevelunit)
where exists(select upper.id
             from src.upperlevelunit,
                  AU_AdministrativeUnit upper
             where AU_AdministrativeUnit.nationalCode = upperlevelunit.nationalcode
               and upper.nationalCode = upperlevelunit.upperlevelunit);

-- unpopulated columns of AU_AdministrativeUnit
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('column', 'AU_AdministrativeUnit', 'beginLifespanVersion', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('column', 'AU_AdministrativeUnit', 'endLifespanVersion', 2);

--
-- Feature "AU_AdministrativeBoundary" {
--     tableName = "AU_AdministrativeBoundary"
--     id INTEGER NOT NULL PRIMARY KEY
--     country TEXT NOT NULL CHECK(BASE2_CountryCode)
--     geometry CURVE NOT NULL
--     beginLifespanVersion DATETIME
--     endLifespanVersion DATETIME
--     legalStatus TEXT CHECK(AU_LegalStatusValue) DEFAULT 'agreed'
--     technicalStatus TEXT CHECK(AU_TechnicalStatusValue) DEFAULT 'edgeMatched'
--  }
--

update gpkg_geometry_columns
set (geometry_type_name, srs_id, z, m) = (SELECT geometry_type_name, 0, z, m
                                          FROM src.gpkg_geometry_columns
                                          WHERE table_name = 'adminboundary')
where table_name = 'AU_AdministrativeBoundary';

update gpkg_contents
set (min_x, min_y, max_x, max_y, srs_id) = (SELECT min_x, min_y, max_x, max_y, 0
                                            FROM src.gpkg_contents
                                            WHERE table_name = 'adminboundary')
where table_name = 'AU_AdministrativeBoundary';

insert into AU_AdministrativeBoundary(id, country, geometry, inspireId, beginLifespanVersion, endLifespanVersion,
                                      legalStatus, technicalStatus)
select fid,
       country,
       the_geom,
       null,
       date_boundary,
       null,
       legalstatus,
       null
from src.adminboundary;

-- unpopulated columns of AU_AdministrativeUnit
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('column', 'AU_AdministrativeBoundary', 'endLifespanVersion', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('column', 'AU_AdministrativeBoundary', 'technicalStatus', 2);

-- unpopulated row/columns of AU_AdministrativeUnit
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, row_id_value, md_file_id)
select 'row/col', 'AU_AdministrativeBoundary', 'legalStatus', id, 2
from AU_AdministrativeBoundary
where legalStatus is null;

-- Feature "AU_Condominium" {
--     tableName = "AU_Condominium"
--     id INTEGER NOT NULL PRIMARY KEY
--     geometry MULTISURFACE NOT NULL
--     beginLifespanVersion DATETIME
--     endLifespanVersion DATETIME
--   }

update gpkg_geometry_columns
set (srs_id, z, m) = (0, 0, 0)
where table_name = 'AU_Condominium';

update gpkg_contents
set srs_id = 0
where table_name = 'AU_Condominium';

-- unpopulated table of AU_Condominium
insert into main.gpkg_metadata_reference(reference_scope, table_name, md_file_id)
values ('table', 'AU_Condominium', 2);

--   Feature "AU_ResidenceOfAuthority" {
--     tableName = "AU_ResidenceOfAuthority"
--     id INTEGER NOT NULL PRIMARY KEY
--     geometry POINT
--   }

update gpkg_geometry_columns
set (srs_id, z, m) = (0, 0, 0)
where table_name = 'AU_ResidenceOfAuthority';

update gpkg_contents
set srs_id = 0
where table_name = 'AU_ResidenceOfAuthority';

-- unpopulated table of AU_ResidenceOfAuthority
insert into main.gpkg_metadata_reference(reference_scope, table_name, md_file_id)
values ('table', 'AU_ResidenceOfAuthority', 2);

-- Attributes "AU_AdministrativeHierarchyLevel" {
--    description = ""
--    tableName = "AU_AdministrativeHierarchyLevel"
--    id INTEGER NOT NULL PRIMARY KEY
--    value TEXT NOT NULL CHECK(AU_AdministrativeHierarchyLevel)
-- }

insert into AU_AdministrativeHierarchyLevel(value)
select value
from gpkg_data_column_constraints
where constraint_name = 'au_administrativehierarchylevel';

-- Relation "AU_admUnit_boundary" {
--     profile = "features"
--     tableName = "AU_admUnit_boundary"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeBoundary(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES AU_AdministrativeUnit(id)
-- }

insert into AU_admUnit_boundary(base_id, related_id)
select distinct adminunit.fid, adminboundary.fid
from src.adminunit,
     src.adminboundary,
     src.boundary_admunit
where adminunit.nationalCode = boundary_admunit.admunit
  and adminboundary.nationalcode = boundary_admunit.boundary;

--   Attributes "BASE_Identifier" {
--     tableName = "BASE_Identifier"
--     id INTEGER NOT NULL PRIMARY KEY
--     namespace TEXT NOT NULL
--     localId TEXT NOT NULL
--     versionId TEXT
--   }

insert into BASE_Identifier(namespace, localId, versionId)
select 'ES.IGN.BDLJE.', nationalCode, '2014'
from src.adminunit;

insert into BASE_Identifier(namespace, localId, versionId)
select 'ES.IGN.BDLJE.', nationalCode, '2014'
from src.adminboundary;

create index idx_localId on BASE_Identifier (localId);
update AU_AdministrativeBoundary
set inspireId = (select id
                 from BASE_Identifier,
                      src.adminboundary
                 where localId = src.adminboundary.nationalCode
                   and src.adminboundary.fid = AU_AdministrativeBoundary.id)
where exists(select id
             from BASE_Identifier,
                  src.adminboundary
             where localId = src.adminboundary.nationalCode
               and src.adminboundary.fid = AU_AdministrativeBoundary.id);
drop index idx_localId;

update AU_AdministrativeUnit
set inspireId = (select id from BASE_Identifier where localId = AU_AdministrativeUnit.nationalCode)
where exists(select id from BASE_Identifier where localId = AU_AdministrativeUnit.nationalCode);

--   Relation "AU_AdministrativeBoundary_nationalLevel" {
--     profile = "attributes"
--     tableName = "AU_AdministrativeBoundary_nationalLevel"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeBoundary(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES AU_AdministrativeHierarchyLevel(id)
--   }

insert into AU_AdministrativeBoundary_nationalLevel(base_id, related_id)
select adminboundary.fid, AU_AdministrativeHierarchyLevel.id
from src.adminboundary,
     AU_AdministrativeHierarchyLevel
where substr(adminboundary.nationallevel, 67) = AU_AdministrativeHierarchyLevel.value;

--   Relation "AU_administeredBy_coAdminister" {
--     profile = "features"
--     tableName = "AU_administeredBy_coAdminister"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeUnit(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES AU_AdministrativeUnit(id)
--   }
--
--  TODO Document as voidable other:unpopulated

--   Relation "AU_condominium_admUnit" {
--     profile = "features"
--     tableName = "AU_condominium_admUnit"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeUnit(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES AU_Condominium(id)
--   }
--
--  TODO Document as voidable other:unpopulated

--   Attributes "GMD_LocalisedCharacterString" {
--     tableName = "GMD_LocalisedCharacterString"
--     id INTEGER NOT NULL PRIMARY KEY
--     value TEXT NOT NULL
--   }

insert into GMD_LocalisedCharacterString(value)
select distinct nationallevelname
from src.adminunit;

-- unpopulated columns of GMD_LocalisedCharacterString
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GMD_LocalisedCharacterString', 'locale', 2);

--   Relation "AU_AdministrativeUnit_nationalLevelName" {
--     profile = "attributes"
--     tableName = "AU_AdministrativeUnit_nationalLevelName"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeUnit(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES GMD_LocalisedCharacterString(id)
--   }

insert into AU_AdministrativeUnit_nationalLevelName(base_id, related_id)
select adminunit.fid, GMD_LocalisedCharacterString.id
from src.adminunit,
     GMD_LocalisedCharacterString
where adminunit.nationallevelname = GMD_LocalisedCharacterString.value;

--   Relation "AU_AdministrativeUnit_residenceOfAuthority" {
--     profile = "attributes"
--     tableName = "AU_AdministrativeUnit_residenceOfAuthority"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeUnit(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES AU_ResidenceOfAuthority(id)
--   }
--  TODO Document as voidable other:unpopulated

--   Relation "AU_Condominium_name" {
--     profile = "attributes"
--     tableName = "AU_Condominium_name"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_Condominium(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES GN_GeographicalName(id)
--   }
--  TODO Document as voidable other:unpopulated

--   Relation "GN_GeographicalName_spelling" {
--     profile = "attributes"
--     tableName = "GN_GeographicalName_spelling"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES GN_GeographicalName(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES GN_SpellingOfName(id)
--   }

insert into GN_GeographicalName_spelling(base_id, related_id)
select fid, fid
from src.adminunit;

--   Attributes "GN_GeographicalName" {
--     tableName = "GN_GeographicalName"
--     id INTEGER NOT NULL PRIMARY KEY
--     language TEXT -> NULL
--     nameStatus TEXT CHECK(GN_NameStatusValue) -> endony
--     nativeness TEXT CHECK(GN_NativenessValue) -> official
--     sourceOfName TEXT -> NULL
--     pronunciation_pronunciationIPA TEXT -> NULL
--     pronunciation_pronunciationSoundLink TEXT -> NULL
--     grammaticalGender TEXT CHECK(GN_GrammaticalGenderValue)
--     grammaticalNumber TEXT CHECK(GN_GrammaticalNumberValue)
--   }

insert into GN_GeographicalName(id, language, nameStatus, nativeness, sourceOfName,
                                pronunciation_pronunciationIPA, pronunciation_pronunciationSoundLink,
                                grammaticalGender, grammaticalNumber)
select fid,
       null,
       'endonym',
       'official',
       null,
       null,
       null,
       null,
       null
from src.adminunit;

-- unpopulated columns of AU_ResidenceOfAuthority
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_GeographicalName', 'language', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_GeographicalName', 'sourceOfName', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_GeographicalName', 'pronunciation_pronunciationIPA', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_GeographicalName', 'pronunciation_pronunciationSoundLink', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_GeographicalName', 'grammaticalGender', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_GeographicalName', 'grammaticalNumber', 2);

--   Relation "AU_AdministrativeUnit_name" {
--     profile = "attributes"
--     tableName = "AU_AdministrativeUnit_name"
--     base_id INTEGER NOT NULL FOREIGN KEY (base_id) REFERENCES AU_AdministrativeUnit(id)
--     related_id INTEGER NOT NULL FOREIGN KEY (related_id) REFERENCES GN_GeographicalName(id)
--   }

insert into AU_AdministrativeUnit_name(base_id, related_id)
select fid, fid
from src.adminunit;

--   Attributes "GN_SpellingOfName" {
--     tableName = "GN_SpellingOfName"
--     id INTEGER NOT NULL PRIMARY KEY
--     text TEXT NOT NULL
--     script TEXT
--     transliterationScheme TEXT
--   }

insert into GN_SpellingOfName(id, text, script, transliterationScheme)
select fid, nameunit, null, null
from src.adminunit;

-- unpopulated columns of GN_SpellingOfName
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_SpellingOfName', 'script', 2);
insert into main.gpkg_metadata_reference(reference_scope, table_name, column_name, md_file_id)
values ('columns', 'GN_SpellingOfName', 'transliterationScheme', 2);

--   Attributes "GMD_PT_Locale" {
--     tableName = "GMD_PT_Locale"
--     id INTEGER NOT NULL PRIMARY KEY
--     characterSetCode TEXT CHECK(MD_CharacterSetCode)
--     country TEXT CHECK(GMD_CountryCode)
--     languageCode TEXT NOT NULL CHECK(GMD_LanguageCode)
--   }

-- unpopulated table of GMD_PT_Locale
insert into main.gpkg_metadata_reference(reference_scope, table_name, md_file_id)
values ('table', 'GMD_PT_Locale', 2);

detach database src;