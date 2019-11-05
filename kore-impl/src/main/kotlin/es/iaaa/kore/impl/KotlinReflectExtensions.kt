package es.iaaa.kore.impl

import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSubtypeOf

/**
 * A [KProperty1] is changeable when it is mutable. A read-write property is changeable.
 */
val <T, R> KProperty1<T, R>.isChangeable: Boolean get() = this is KMutableProperty1<T, R>

/**
 * A [KProperty1] is unsettable when it has a notion of a no-value state. A propert that
 * that allows nullable references is unsettables.
 */
val <T, R> KProperty1<T, R>.isUnsettable: Boolean get() = returnType.isMarkedNullable

/**
 * A [KProperty1] is many when it may contain multiple values. A property whose type is
 * a [Collection] is many.
 */
val <T, R> KProperty1<T, R>.isMany: Boolean get() = returnType.isSubtypeOf(Collection::class.createType())
