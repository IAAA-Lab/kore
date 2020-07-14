/**
 * SPDX-License-Identifier: EUPL-1.2
 * Copyright 2019-2020 Francisco J Lopez-Pellicer
 *
 * Licensed under the  EUPL-1.2-or-later
 */
package es.iaaa.kore.impl

import es.iaaa.kore.*

class KoreInstanceInternal : KoreModelFactory, KoreFactory by KoreFactoryImpl() {
    override fun createAnnotation(): KoreAnnotation = KoreAnnotationImpl()
    override fun createClassifier(): KoreClassifier = KoreClassifierImpl()
    override fun createPackage(): KorePackage = KorePackageImpl()
    override fun createClass(): KoreClass = KoreClassImpl()
    override fun createDataType(): KoreDataType = KoreDataTypeImpl()
    override fun createEnum(): KoreEnum = KoreEnumImpl()
    override fun createEnumLiteral(): KoreEnumLiteral = KoreEnumLiteralImpl()
    override fun createAttribute(): KoreAttribute = KoreAttributeImpl()
    override fun createReference(): KoreReference = KoreReferenceImpl()
    override fun createOperation(): KoreOperation = KoreOperationImpl()
    override fun createParameter(): KoreParameter = KoreParameterImpl()
}