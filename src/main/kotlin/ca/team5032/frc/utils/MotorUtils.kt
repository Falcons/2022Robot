package ca.team5032.frc.utils

import ca.team5032.frc.subsystems.DriveTrain
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX

// TODO: actually use this?
class Falcon500(motorId: Int) : WPI_TalonFX(motorId) {

    init {
        configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor)
    }

    // Default -> Metres
    fun position() = position(Metres)
    fun <T : Unit> position(desired: T) =
        if (desired is AngularDistance) {
            selectedSensorPosition apply (TalonTicks to desired)
        } else {
            selectedSensorPosition
                .apply(TalonTicks to Rotations)
                .apply(DriveTrain.ANGULAR_CONVERSION)
                .apply(Metres to desired)
        }

    // Encoder Ticks / Hundred Millis -> Metres / Second
    fun velocity() = velocity(Metres / Seconds)
    fun <T : Unit, K : Time> velocity(desired: Derivative<T, K>) =
        if (desired.a is AngularDistance) {
            selectedSensorVelocity apply (TalonTicks / (100 * Millis) to desired)
        } else {
            selectedSensorVelocity
                .apply(TalonTicks / (100 * Millis) to Rotations / Seconds)
                .apply(DriveTrain.ANGULAR_CONVERSION)
                .apply(Metres / Seconds to desired)
        }

}

/**
 * Until [edu.wpi.first.math.kinematics.MecanumDriveOdometry] is proven reliable, this simple class can be used
 * for Y axis odometry
 */
class MecanumLinearOdometry(vararg falcons: WPI_TalonFX, private val unit: Distance) {

    private val falcons = listOf(*falcons)
    private var startingPosition = 0.0

    fun zero() {
        // Look into this: - for now, seems more reliable to just get a starting position.
        // falcons.forEach { it.sensorCollection.setIntegratedSensorPosition(0.0) }
        startingPosition = getCurrentAbsolutePosition()
    }

    fun getElapsedDistance(): Double {
        return getCurrentAbsolutePosition() - startingPosition
    }

    private fun getCurrentAbsolutePosition(): Double {
        return (falcons.sumOf { it.sensorCollection.integratedSensorPosition } / falcons.size)
            .apply(TalonTicks / (100 * Millis) to Rotations / Seconds)
            .apply(DriveTrain.ANGULAR_CONVERSION)
            .apply(Metres / Seconds to unit / Seconds)
    }

}