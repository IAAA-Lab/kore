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
package es.iaaa.kore

/**
 * A typed element.
 */
interface KoreTypedElement : KoreNamedElement {

    /**
     * The type.
     */
    var type: KoreClassifier?

    /**
     * The lower bound.
     */
    var lowerBound: Int

    /**
     * The upper bound.
     */
    var upperBound: Int

    /**
     * Is many?
     */
    val isMany: Boolean get() = upperBound > 1 || unbounded

    /**
     * Is ordered?
     */
    var ordered: Boolean

    /**
     * Is required?
     */
    val required: Boolean get() = lowerBound >= 1

    /**
     * Is unbounded?
     */
    val unbounded: Boolean get() = upperBound == UNBOUNDED_MULTIPLICITY

    companion object {

        /**
         * A value indicating that there is no upper bound.
         */
        const val UNBOUNDED_MULTIPLICITY = -1

        /**
         * A value indicating that there is an unspecified upper bound.
         */
        const val UNSPECIFIED_MULTIPLICITY = -2
    }
}