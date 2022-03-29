package ca.team5032.frc.utils

import ca.team5032.frc.subsystems.DriveTrain
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
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

/**
 * Until [edu.wpi.first.math.kinematics.MecanumDriveOdometry] is proven reliable, this simple class can be used
 * for Y axis odometry
 */
class MecanumLinearOdometry(vararg falcons: WPI_TalonFX, private val unit: Distance) {

    private val falcons = listOf(*falcons)
    private var startingPosition = 0.0
    private val tab = Shuffleboard.getTab("Linear Odometry")

    fun zero() {
        falcons.forEach { it.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor) }

        // Look into this: - for now, seems more reliable to just get a starting position.
        //falcons.forEach { it.sensorCollection.setIntegratedSensorPosition(0.0, 0) }
        startingPosition = getCurrentAbsolutePosition()
    }

    fun getElapsedDistance(): Double {
        return getCurrentAbsolutePosition() - startingPosition
    }

    private fun getCurrentAbsolutePosition(): Double {
        return (falcons.sumOf { it.selectedSensorPosition } / falcons.size)
            .apply(TalonTicks to Rotations)
            .apply(DriveTrain.ANGULAR_CONVERSION)
            .apply(Metres to unit)
    }

}