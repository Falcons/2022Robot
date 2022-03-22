package ca.team5032.frc.utils

// TODO: If I get bored, rewrite this to be even more generic, but more typesafe, supporting infinite derivations of
//  units, all SI units and more flexibility.

abstract class Unit(val conversionFactor: Double)

sealed class Distance(conversionFactor: Double) : Unit(conversionFactor)
object Inches : Distance(0.0254)
object Feet : Distance(0.3048)
object Metres : Distance(1.00)
object Kilometres : Distance(1000.0)

sealed class AngularDistance(conversionFactor: Double) : Unit(conversionFactor)
object Rotations : AngularDistance(1.00)
object Radians : AngularDistance(2 * Math.PI)
object Degrees : AngularDistance(360.00)
object TalonTicks : AngularDistance(2048.00)

sealed class Time(conversionFactor: Double) : Unit(conversionFactor)
object Millis : Time(0.001)
object Seconds : Time(1.00)
object Minutes : Time(60.00)
object Hours : Time(3600.00)

data class Derivative<T : Unit, K : Unit>(val a: T, val b: K)
data class Ratio<T : Unit, K : Unit>(val n: Double, val derivative: Derivative<T, K>)

// Base units
fun <T : Unit> convert(value: Double, from: T, to: T): Double {
    return value / from.conversionFactor * to.conversionFactor
}

infix fun <T : Unit> Number.apply(units: Pair<T, T>): Double {
    return convert(this.toDouble(), units.first, units.second)
}

fun <T : Unit> apply(value: Double, from: T, ratio: Ratio<*, T>): Double {
    return (value apply (from to ratio.derivative.b)) * ratio.n // expandability, converts units.
}

infix fun <T : Unit> Number.apply(ratio: Ratio<*, T>): Double {
    return apply(this.toDouble(), ratio.derivative.b, ratio)
}

operator fun <T : Unit, K : Unit> Number.times(derivative: Derivative<T, K>): Ratio<T, K> {
    return Ratio(this.toDouble(), derivative)
}

// Derives custom units.
operator fun <T : Unit> Number.times(unit: T): Unit {
    return object : Unit(unit.conversionFactor * this.toDouble()) {}
}

// 1st derivatives
operator fun <T : Unit, K : Unit> T.div(other: K): Derivative<T, K> {
    return Derivative(this, other)
}

fun <T : Unit, K : Unit> convert2(value: Double, from: Derivative<out T, out K>, to: Derivative<out T, out K>): Double {
    return value apply (from.a to to.a) apply (from.b to to.b)
}

@JvmName("apply2")
infix fun <T : Unit, K : Unit> Number.apply(units: Pair<Derivative<out T, out K>, Derivative<out T, out K>>): Double {
    return convert2(this.toDouble(), units.first, units.second)
}