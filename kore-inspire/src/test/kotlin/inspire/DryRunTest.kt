/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package inspire

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import java.io.File

class DryRunTest {

    @Test
    fun `Annex I Addresses - addresses - draft`() {
        checkSchema("Addresses", "annex/i/ad/ad")
    }

    @Test
    fun `Annex I Administrative Units - administrative units`() {
        checkSchema("AdministrativeUnits", "annex/i/au/au")
    }

    @Test
    fun `Annex I Administrative Units - maritime units - draft`() {
        checkSchema("MaritimeUnits", "annex/i/au/mu")
    }

    @Test
    fun `Annex I Cadastral parcels - cadastral parcels - draft`() {
        checkSchema("CadastralParcels", "annex/i/cp/cp")
    }

    @Disabled("Without application schemas")
    @Test
    fun `Annex I Coordinate reference systems`() {
    }

    @Disabled("Without application schemas")
    @Test
    fun `Annex I Geographical grid systems`() {
    }

    @Test
    fun `Annex I Geographical Names - geographical names - draft`() {
        checkSchema("Geographical Names", "annex/i/gn/gn")
    }

    @Test
    fun `Annex I Hydrography - hydro base - draft`() {
        checkSchema("Hydro - base", "annex/i/hy/hy")
    }

    @Test
    fun `Annex I Hydrography - hydro network - draft`() {
        checkSchema("Hydro - Network", "annex/i/hy/hy-n")
    }

    @Test
    fun `Annex I Hydrography - hydro physical waters - draft`() {
        checkSchema("Hydro - Physical Waters", "annex/i/hy/hy-p")
    }

    @Disabled("No XSD schema")
    @Test
    fun `Annex I Protected Sites - protected sites full - draft`() {
        checkSchema("Protected Sites Full", "annex/i/ps/ps-f")
    }

    @Disabled("No XSD schema")
    @Test
    fun `Annex I Protected Sites - protected sites natura 2000 - draft`() {
        checkSchema("Protected Sites Natura2000", "annex/i/ps/ps-n2000")
    }

    @Test
    fun `Annex I Transport networks - air transport network - draft`() {
        checkSchema("Air Transport Network", "annex/i/tn/tn-a")
    }

    @Test
    fun `Annex I Transport networks - cable transport network - draft`() {
        checkSchema("Cable Transport Network", "annex/i/tn/tn-c")
    }

    @Test
    fun `Annex I Transport networks - common transport elements - draft`() {
        checkSchema("Common Transport Elements", "annex/i/tn/tn")
    }

    @Test
    fun `Annex I Transport networks - railway transport network - draft`() {
        checkSchema("Railway Transport Network", "annex/i/tn/tn-ra")
    }

    @Test
    fun `Annex I Transport networks - road transport network - draft`() {
        checkSchema("Road Transport Network", "annex/i/tn/tn-ro")
    }

    @Test
    fun `Annex I Transport networks - water transport network - draft`() {
        checkSchema("Water Transport Network", "annex/i/tn/tn-w")
    }

    @Test
    fun `Annex II Elevation - base types - draft`() {
        checkSchema("ElevationBaseTypes", "annex/ii/el/el-bas")
    }

    @Test
    fun `Annex II Elevation - grid coverage - draft`() {
        checkSchema("ElevationGridCoverage", "annex/ii/el/el-cov")
    }

    @Test
    fun `Annex II Elevation - tin - draft`() {
        checkSchema("ElevationTIN", "annex/ii/el/el-tin")
    }

    @Test
    fun `Annex II Elevation - vector elements - draft`() {
        checkSchema("ElevationVectorElements", "annex/ii/el/el-vec")
    }

    @Test
    fun `Annex II Geology - geology - draft`() {
        checkSchema("Geology", "annex/ii/ge/ge-core")
    }

    @Test
    fun `Annex II Geology - hydrogeology - draft`() {
        checkSchema("Hydrogeology", "annex/ii/ge/ge-hg")
    }

    @Test
    fun `Annex II Geology - geophysics - draft`() {
        checkSchema("Geophysics", "annex/ii/ge/ge-gp")
    }

    @Test
    fun `Annex II Land Cover - land cover nomenclature - draft`() {
        checkSchema("LandCoverNomenclature", "annex/ii/lc/lcn")
    }

    @Test
    fun `Annex II Land Cover - land cover raster - draft`() {
        checkSchema("LandCoverRaster", "annex/ii/lc/lcr")
    }

    @Test
    fun `Annex II Land Cover - land cover vector - draft`() {
        checkSchema("LandCoverVector", "annex/ii/lc/lcv")
    }

    @Test
    fun `Annex II Orthoimagery - orthoimagery - draft`() {
        checkSchema("Orthoimagery", "annex/ii/oi/oi")
    }

    @Test
    fun `Annex III Agricultural and aquaculture facilities - agricultural and aquaculture facilities model - draft`() {
        checkSchema("Agricultural and Aquaculture Facilities Model", "annex/iii/af/af")
    }

    @Test
    fun `Annex III Area Management restriction regulation zones and reporting units - area management restriction and regulation zones - draft`() {
        checkSchema("Area Management Restriction and Regulation Zones", "annex/iii/am/am")
    }

    @Test
    fun `Annex III Atmospheric conditions - atmospheric conditions - draft`() {
        checkSchema("Atmospheric Conditions and Meteorological Geographical Features", "annex/iii/ac/ac-mf")
    }

    @Test
    fun `Annex III Bio-geographical regions - bio-geographical regions - draft`() {
        checkSchema("Bio-geographicalRegions", "annex/iii/br/br")
    }

    @Test
    fun `Annex III Buildings - building base - draft`() {
        checkSchema("BuildingsBase", "annex/iii/bu/bu-base")
    }

    @Test
    fun `Annex III Buildings - building 2D - draft`() {
        checkSchema("Buildings2D", "annex/iii/bu/bu-core2D")
    }

    @Test
    fun `Annex III Buildings - building 3D - draft`() {
        checkSchema("Buildings3D", "annex/iii/bu/bu-core3D")
    }

    @Test
    fun `Annex III Energy resources - energy resources base - draft`() {
        checkSchema("Energy Resources Base", "annex/iii/er/er-b")
    }

    @Test
    fun `Annex III Energy resources - energy resources coverage - draft`() {
        checkSchema("Energy Resources Coverage", "annex/iii/er/er-c")
    }

    @Test
    fun `Annex III Energy resources - energy resources vector - draft`() {
        checkSchema("Energy Resources Vector", "annex/iii/er/er-v")
    }

    @Test
    fun `Annex III Environmental monitoring facilities - environmental monitoring facilities - draft`() {
        checkSchema("EnvironmentalMonitoringFacilities", "annex/iii/ef/ef")
    }

    @Test
    fun `Annex III Habitats and biotopes - habitats and biotopes - draft`() {
        checkSchema("HabitatsAndBiotopes", "annex/iii/hb/hb")
    }

    @Test
    fun `Annex III Human health and safety - human health - draft`() {
        checkSchema("HumanHealth", "annex/iii/hh/hh")
    }

    @Test
    fun `Annex III Human health and safety - safety - draft`() {
        checkSchema("Safety", "annex/iii/hh/hh-sa")
    }

    @Test
    fun `Annex III Land use - existing land use - draft`() {
        checkSchema("Existing Land Use", "annex/iii/lu/elu")
    }

    @Test
    fun `Annex III Land use - gridded land use - draft`() {
        checkSchema("Gridded Land Use", "annex/iii/lu/gelu")
    }

    @Test
    fun `Annex III Land use - land use nomenclature - draft`() {
        checkSchema("Land Use Nomenclature", "annex/iii/lu/lunom")
    }

    @Test
    fun `Annex III Land use - planned land use - draft`() {
        checkSchema("Planned Land Use", "annex/iii/lu/plu")
    }

    @Test
    fun `Annex III Land use - sampled land use - draft`() {
        checkSchema("Sampled Land Use", "annex/iii/lu/selu")
    }

    @Test
    fun `Annex III Meteorological geographical features - meteorological geographical features - draft`() {
        `Annex III Atmospheric conditions - atmospheric conditions - draft`()
    }

    @Test
    fun `Annex III Mineral resources - mineral resources - draft`() {
        checkSchema("MineralResources", "annex/iii/mr/mr-core")
    }

    @Test
    fun `Annex III Natural risk zones - natural risk zones - draft`() {
        checkSchema("NaturalRiskZones", "annex/iii/nz/nz-core")
    }

    @Test
    fun `Annex III Oceanographic geographical features - oceanographic geographical features - draft`() {
        checkSchema("Oceanographic Geographical Features", "annex/iii/of/of")
    }

    @Test
    fun `Annex III Population distribution and demography - population distribution - draft`() {
        checkSchema("Population distribution - demography", "annex/iii/pd/pd")
    }

    @Test
    fun `Annex III Production and industrial facilities - production and industrial facilities - draft`() {
        checkSchema("ProductionAndIndustrialFacilities", "annex/iii/pf/pf")
    }

    @Test
    fun `Annex III Sea regions - sea regions - draft`() {
        checkSchema("Sea Regions", "annex/iii/sr/sr")
    }

    @Test
    fun `Annex III Soil - soil - draft`() {
        checkSchema("Soil", "annex/iii/so/so")
    }

    @Test
    fun `Annex III Species distribution - species distribution - draft`() {
        checkSchema("SpeciesDistribution", "annex/iii/sd/sd")
    }

    @Test
    fun `Annex III Statistical units - statistical units base - draft`() {
        checkSchema("Statistical Units Base", "annex/iii/su/su-core")
    }

    @Test
    fun `Annex III Statistical units - statistical units grid - draft`() {
        checkSchema("Statistical Units Grid", "annex/iii/su/su-grid")
    }

    @Test
    fun `Annex III Statistical units - statistical units vector - draft`() {
        checkSchema("Statistical Units Vector", "annex/iii/su/su-vector")
    }

    @Test
    fun `Annex III Utility and governmental services - administrative and social governmental services - draft`() {
        checkSchema("AdministrativeAndSocialGovernmentalServices", "annex/iii/us/us-govserv")
    }

    @Test
    fun `Annex III Utility and governmental services - environmental management facilities - draft`() {
        checkSchema("Environmental Management Facilities", "annex/iii/us/us-emf")
    }

    @Test
    fun `Annex III Utility and governmental services - common utility network elements - draft`() {
        checkSchema("Common Utility Network Elements", "annex/iii/us/us-net-common")
    }

    @Test
    fun `Annex III Utility and governmental services - electricity network - draft`() {
        checkSchema("Electricity Network", "annex/iii/us/us-net-el")
    }

    @Test
    fun `Annex III Utility and governmental services - oil-gas-chemicals network - draft`() {
        checkSchema("Oil-Gas-Chemicals Network", "annex/iii/us/us-net-ogc")
    }

    @Test
    fun `Annex III Utility and governmental services - sewer network - draft`() {
        checkSchema("Sewer Network", "annex/iii/us/us-net-sw")
    }

    @Test
    fun `Annex III Utility and governmental services - telecommunications network - draft`() {
        checkSchema("Telecommunications Network", "annex/iii/us/us-net-tc")
    }

    @Test
    fun `Annex III Utility and governmental services - thermal network - draft`() {
        checkSchema("Thermal Network", "annex/iii/us/us-net-th")
    }

    @Test
    fun `Annex III Utility and governmental services - water network - draft`() {
        checkSchema("Water Network", "annex/iii/us/us-net-wa")
    }

    @Test
    fun `Base Types - base types - draft`() {
        checkSchema("Base Types", "base-types/base")
    }

    @Test
    fun `Base Types - base types 2 - draft`() {
        checkSchema("Base Types 2", "base-types/base2")
    }

    @Test
    fun `Base Models - coverages base - draft`() {
        checkSchema("Coverages (Base)", "base-models/cov/cvbase")
    }

    @Test
    fun `Base Models - coverages domain and range - draft`() {
        checkSchema("Coverages (Domain and Range)", "base-models/cov/gmlcov")
    }

    @Test
    fun `Base Models - activity complex - draft`() {
        checkSchema("Activity Complex", "base-models/act/act-core")
    }

    @Test
    fun `Base Models - network - draft`() {
        checkSchema("Network", "base-models/net/net")
    }

    @Test
    fun `Base Models - observations - observations references - draft`() {
        checkSchema("Observation References", "base-models/om/omor")
    }

    @Test
    fun `Base Models - observations - processes - draft`() {
        checkSchema("Processes", "base-models/om/ompr")
    }

    @Test
    fun `Base Models - observations - specialised observations - draft`() {
        checkSchema("Specialised Observations", "base-models/om/omso")
    }

    @Test
    fun `Base Models - observations - observable properties - draft`() {
        checkSchema("Observable Properties", "base-models/om/omop")
    }

    private fun checkSchema(schema: String, route: String) {
        val config = Configuration(
            file = "src/main/resources/$INSPIRE_CONSOLIDATED_UML_MODEL",
            description = false,
            sql = false,
            metadata = false
        )
        val run = configuration(schema, route, config)
        run.convert(true)
        val expected = File("src/test/resources/$route.txt").readText().split("\n")
        val dryRun = run.lastDryRunOutput.toString()
        val output = File("build/test/resources/$route.txt")
        output.parentFile.mkdirs()
        output.writeText(dryRun)
        val actual = dryRun.split("\n")
        expected.zip(actual).forEach {
            if (it.first.isBlank()) {
                assertTrue(it.second.isBlank())
            } else {
                assertEquals(it.first.trim(), it.second.trim())
            }
        }
        assertEquals(expected.size, actual.size)
    }

    companion object {
        private const val INSPIRE_CONSOLIDATED_UML_MODEL =
            "INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml"
    }
}

/**
 * See https://youtrack.jetbrains.com/issue/KT-33787
 */
fun workaround() {}