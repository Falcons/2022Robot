package ca.team5032.frc.utils

import kotlin.math.abs

fun driveCartesianIK(ySpeed: Double, xSpeed: Double, zRotation: Double): List<Double> {
    val driveValues = listOf(
        ySpeed + xSpeed + zRotation,
        ySpeed - xSpeed - zRotation,
        ySpeed - xSpeed + zRotation,
        ySpeed + xSpeed - zRotation
    )

    val maxMagnitude = abs(driveValues.maxOrNull()!!)

    return if (maxMagnitude > 1.0) driveValues.map { it / maxMagnitude } else driveValues
}