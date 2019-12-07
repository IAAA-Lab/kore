/**
 * Copyright 2019 Francisco J. Lopez Pellicer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
@file:Suppress("ObjectPropertyName")

package inspire

val au = {
    val au = base()
    au.input.selector.set(schemaName("AdministrativeUnits"))
    au.input.file.set("src/main/resources/INSPIRE Consolidated UML Model ANNEX I II III complete r4618.xml")
    au
}

fun main() {
    val au = au()
    au.convert(dryRun = false)
    println(au.lastDryRunOutput)
}

