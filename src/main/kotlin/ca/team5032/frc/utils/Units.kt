package ca.team5032.frc.utils

abstract class GenericUnit(val conversionFactor: Double)

sealed class Distance(conversionFactor: Double) : GenericUnit(conversionFactor)
object Inches : Distance(0.0254)
object Feet : Distance(0.3048)
object Metres : Distance(1.00)
object Kilometres : Distance(1000.0)

sealed class AngularDistance(conversionFactor: Double) : GenericUnit(conversionFactor)
object Rotations : AngularDistance(1.00)
object Radians : AngularDistance(1 / 2 * Math.PI)
object Degrees : AngularDistance(1 / 360.00)
object TalonTicks : AngularDistance(1 / 2048.00)
object DriveTicks : AngularDistance(1 / (2048.00 * 9))

sealed class Time(conversionFactor: Double) : GenericUnit(conversionFactor)
object Millis : Time(1 / 1000.00)
object Ticks : Time(0.02)
object Seconds : Time(1.00)
object Minutes : Time(60.00)
object Hours : Time(3600.00)

data class Derivative<T : GenericUnit, K : GenericUnit>(val a: T, val b: K)
data class Ratio<T : GenericUnit, K : GenericUnit>(val n: Double, val derivative: Derivative<T, K>)

// Base units
fun <T : GenericUnit> convert(value: Double, from: T, to: T): Double {
    return value * from.conversionFactor / to.conversionFactor
}

infix fun <T : GenericUnit> Number.apply(units: Pair<T, T>): Double {
    return convert(this.toDouble(), units.first, units.second)
}

fun <T : GenericUnit> apply(value: Double, from: T, ratio: Ratio<*, T>): Double {
    return (value apply (from to ratio.derivative.b)) * ratio.n // expandability, converts units.
}

infix fun <T : GenericUnit> Number.apply(ratio: Ratio<*, T>): Double {
    return apply(this.toDouble(), ratio.derivative.b, ratio)
}

operator fun <T : GenericUnit, K : GenericUnit> Number.times(derivative: Derivative<T, K>): Ratio<T, K> {
    return Ratio(this.toDouble(), derivative)
}

// Derives custom units.
operator fun <T : GenericUnit> Number.times(unit: T): GenericUnit {
    return object : GenericUnit(unit.conversionFactor * this.toDouble()) {}
}

// 1st derivatives
operator fun <T : GenericUnit, K : GenericUnit> T.div(other: K): Derivative<T, K> {
    return Derivative(this, other)
}

fun <T : GenericUnit, K : GenericUnit> convert2(value: Double, from: Derivative<out T, out K>, to: Derivative<out T, out K>): Double {
    return value apply (from.a to to.a) apply (to.b to from.b)
}

@JvmName("apply2")
infix fun <T : GenericUnit, K : GenericUnit> Number.apply(units: Pair<Derivative<out T, out K>, Derivative<out T, out K>>): Double {
    return convert2(this.toDouble(), units.first, units.second)
}

fun main() {
    print(12 apply (Feet / Seconds to Kilometres / Hours))
}