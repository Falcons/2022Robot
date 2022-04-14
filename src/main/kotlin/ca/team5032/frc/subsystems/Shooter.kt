package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.auto.Limelight
import ca.team5032.frc.utils.*
import ca.team5032.frc.utils.motor.Falcon500
import ca.team5032.frc.utils.motor.MotorProfile
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.GenericHID
import kotlin.math.abs
import kotlin.math.pow

class Shooter : Subsystem<Shooter.State>("Shooter", State.Idle()), Tabbed {

    companion object {
        val RPM_THRESHOLD = DoubleProperty("RPM Threshold", 100.0)

        const val kS: Double = 0.20985
        const val kV: Double = 0.11193
    }

    sealed class State {
        data class AtSpeed(val speed: () -> Double): State()
        data class RampingUp(val targetSpeed: () -> Double) : State()
        data class Idle(val passiveRpm: Double = 0.0) : State()
    }

    private val shooterFalcon = Falcon500(SHOOTER_ID, MotorProfile.ShooterConfig)
    private val feedforward = SimpleMotorFeedforward(kS, kV)

    init {
        tab.addNumber("Encoder Value") { shooterFalcon.velocity(Rotations / Minutes) }
        tab.addNumber("Current RPM", ::getRPM)
        tab.addNumber("Target RPM") {
            state.let {
                return@addNumber when (it) {
                    is State.AtSpeed -> it.speed()
                    is State.RampingUp -> it.targetSpeed()
                    else -> 0.0
                }
            }
        }

        buildConfig(RPM_THRESHOLD)
    }

    override fun periodic() {
        state.let {
            if (it is State.RampingUp && withinThreshold(getRPM(), it.targetSpeed(), RPM_THRESHOLD.value) && it.targetSpeed() != 2300.0) {
                changeState(State.AtSpeed(it.targetSpeed))
            } else if (it is State.AtSpeed && !withinThreshold(getRPM(), it.speed(), RPM_THRESHOLD.value)) {
                changeState(State.RampingUp(it.speed))
            }

            when (it) {
                is State.AtSpeed -> {
                    shooterFalcon.setVoltage(
                        (it.speed() - getRPM()) / 500 +
                        + 1.07 * feedforward.calculate(it.speed() apply (Rotations / Minutes to Rotations / Seconds))
                    )

                    Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))

                    if (it.speed() != 2300.0) {
                        Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kRightRumble, 1.0)
                        Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0)
                    }
                }
                is State.RampingUp -> {
                    // In Principle:
                    Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))
                    shooterFalcon.setVoltage(
                        (it.targetSpeed() - getRPM()) / 500 +
                        + 1.07 * feedforward.calculate(it.targetSpeed() apply (Rotations / Minutes to Rotations / Seconds))
                    )
                }
                is State.Idle -> {
                    shooterFalcon.set(0.0)

                    Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kRightRumble, 0.0)
                    Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0)
                }
            }
        }
    }

    fun shoot() {
        Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))
        changeState(State.RampingUp(::getTargetRPM))
    }

    fun stop() {
        Perseverance.limelight.changeState(Limelight.State.Idle)
        changeState(State.Idle())
    }

    private fun getTargetRPM(): Double {
        if (Perseverance.limelight.hasTarget()) {
            val ty = Perseverance.limelight.target.offset.y

            return 3747.22 - 566.703 * ty.pow(0.342127)
        }

        return 2300.0
    }

    private fun getRPM() = shooterFalcon.velocity( Rotations / Minutes)

    private fun withinThreshold(a: Double, b: Double, threshold: Double): Boolean {
        return abs(a - b) < threshold
    }

}