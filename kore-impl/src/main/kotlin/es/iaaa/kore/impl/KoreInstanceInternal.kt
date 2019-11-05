package es.iaaa.kore.impl

import es.iaaa.kore.KoreAnnotation
import es.iaaa.kore.KoreAttribute
import es.iaaa.kore.KoreClass
import es.iaaa.kore.KoreClassifier
import es.iaaa.kore.KoreDataType
import es.iaaa.kore.KoreEnum
import es.iaaa.kore.KoreEnumLiteral
import es.iaaa.kore.KoreFactory
import es.iaaa.kore.KoreModelFactory
import es.iaaa.kore.KoreOperation
import es.iaaa.kore.KorePackage
import es.iaaa.kore.KoreParameter
import es.iaaa.kore.KoreReference

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