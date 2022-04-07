package ca.team5032.frc.utils.motor

import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX

class Falcon500(motorId: Int) : WPI_TalonFX(motorId) {

    var angularConversion = ANGULAR_CONVERSION

    init {
        configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor)
    }

    // Defaults to Metres and DriveTrain angular conversion
    fun position() = position(Metres)
    fun <T : GenericUnit> position(desired: T) =
        selectedSensorPosition / 2048 / 9 * angularConversion.n
//        if (desired is AngularDistance) {
//            selectedSensorPosition apply (TalonTicks to desired)
//        } else {
//            selectedSensorPosition
//                .apply(TalonTicks to Rotations)
//                .apply(angularConversion)
//                .apply(Metres to desired)
//        }

    // Defaults to Metres / Second and DriveTrain angular conversion
    fun velocity() = velocity(DriveTicks, Metres / Seconds)
    fun <T : GenericUnit, K : Time, U : AngularDistance> velocity(ticks: U, desired: Derivative<T, K>) =
        if (desired.a is AngularDistance) {
            selectedSensorVelocity apply (ticks / (100 * Millis) to desired)
        } else {
            selectedSensorVelocity
                .apply(ticks / (100 * Millis) to Rotations / Seconds)
                .apply(angularConversion)
                .apply(Metres / Seconds to desired)
        }

}