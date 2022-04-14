package ca.team5032.frc.utils.motor

import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.controller.PIDController
import kotlin.math.sign

class Falcon500(private val motorId: Int, config: MotorProfile<Falcon500>?) : WPI_TalonFX(motorId) {

    var angularConversion = DRIVE_ANGULAR_CONVERSION
    var ticks: AngularDistance = DriveTicks

    private val controller = PIDController(0.3, 0.1, 0.0)

    init {
        configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor)

        config?.apply(this)
    }

    fun <T : Distance, K : Time> setSpeed(speed: Double, unit: Derivative<T, K>) {
//        val velocityInTicks = speed
//            .apply (unit to Metres / Seconds)
//            .apply ((1 / angularConversion) * (Rotations / Metres))
//            .apply (Rotations / Seconds to ticks / Seconds)

        val output = controller.calculate(velocity(unit), speed)
        val remapped = output * (1 - 0.3) + (0.3 * sign(output))

        println("Motor Id $motorId: Output $output, Remapped $remapped")
        println("- Current vel: ${velocity(unit)}, Desired: $speed")

        set(remapped)
    }

    // Defaults to Metres and DriveTrain angular conversion
    fun position() = position(Metres)
    fun <T : GenericUnit> position(desired: T) =
        if (desired is AngularDistance) {
            selectedSensorPosition apply (ticks to desired)
        } else {
            selectedSensorPosition
                .apply(ticks to Rotations)
                .apply(angularConversion * (Metres / Rotations))
                .apply(Metres to desired)
        }

    // Defaults to Metres / Second and DriveTrain angular conversion
    fun velocity() = velocity( Metres / Seconds)
    fun <T : GenericUnit, K : Time> velocity(desired: Derivative<T, K>) =
        if (desired.a is AngularDistance) {
            selectedSensorVelocity apply (ticks / (100 * Millis) to desired)
        } else {
            selectedSensorVelocity
                .apply(ticks / (100 * Millis) to Rotations / Seconds)
                .apply(angularConversion * (Metres / Rotations))
                .apply(Metres / Seconds to desired)
        }

}