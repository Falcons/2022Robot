package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.auto.Limelight
import ca.team5032.frc.utils.*
import ca.team5032.frc.utils.motor.Falcon500
import com.ctre.phoenix.motorcontrol.NeutralMode
import edu.wpi.first.math.controller.BangBangController
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.wpilibj.GenericHID
import kotlin.math.abs
import kotlin.math.pow

class Shooter : Subsystem<Shooter.State>("Shooter", State.Idle), Tabbed {

    companion object {
        val RPM_THRESHOLD = DoubleProperty("RPM Threshold", 100.0)
        val TARGET_RPM = DoubleProperty("Target RPM", 3000.0)
        val POWER = DoubleProperty("Target Speed", 0.35)

        // TODO: Figure out why these aren't accurate? and/or FF doesn't work how I thought.
        const val kS: Double = 0.069646
        const val kV: Double = 0.11522
        const val kA: Double = 0.0
    }

    sealed class State {
        data class AtSpeed(val speed: () -> Double): State()
        data class RampingUp(val targetSpeed: () -> Double) : State()
        object Idle : State()
    }

    private val shooterFalcon = Falcon500(SHOOTER_ID)

    // TODO: PID instead of bang bang. Start using PID way more.
    private val controller = PIDController(0.0, 0.0, 0.0)
    private val bangBangController = BangBangController()
    private val feedforward = SimpleMotorFeedforward(kS, kV)

    init {
        // Set the shooter falcon to coast to prevent the brake from fighting against BangBang.
        shooterFalcon.setNeutralMode(NeutralMode.Coast)
        shooterFalcon.inverted = true

        tab.addNumber("Encoder Value") { shooterFalcon.velocity(TalonTicks, Rotations / Minutes) }
        tab.addNumber("Current RPM", ::getRPM)
        tab.addNumber("Target RPM") {
            state.let {
                return@addNumber if (it is State.AtSpeed) {
                    it.speed()
                } else if (it is State.RampingUp) {
                    it.targetSpeed()
                } else {
                    0.0
                }
            }
        }
        tab.add(controller)

        buildConfig(RPM_THRESHOLD, TARGET_RPM, POWER)
    }

    override fun periodic() {
        state.let {
            // Simple state machine.
            if (it is State.RampingUp && withinThreshold(getRPM(), it.targetSpeed(), RPM_THRESHOLD.value) && it.targetSpeed() != 2000.0) {
                changeState(State.AtSpeed(it.targetSpeed))
            } else if (it is State.AtSpeed && !withinThreshold(getRPM(), it.speed(), RPM_THRESHOLD.value)) {
                changeState(State.RampingUp(it.speed))
            }

            when (it) {
                is State.AtSpeed -> {
                    shooterFalcon.setVoltage(
                        + 1.05 * feedforward.calculate(it.speed() apply (Rotations / Minutes to Rotations / Seconds))
                    )

                    Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))

                    if (it.speed() != 2000.0) {
                        Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kRightRumble, 1.0)
                        Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0)
                    }
                }
                is State.RampingUp -> {
                    // In Principle:
                    Perseverance.limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))
                    shooterFalcon.setVoltage(
                        + 1.05 * feedforward.calculate(it.targetSpeed() apply (Rotations / Minutes to Rotations / Seconds))
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
        changeState(State.Idle)
    }

    private fun getTargetRPM(): Double {
        if (Perseverance.limelight.hasTarget()) {
            val ty = Perseverance.limelight.target.offset.y

            return 3747.22 - 566.703 * ty.pow(0.342127)
        }

        return 2000.0
    }

    private fun getRPM() = shooterFalcon.velocity(TalonTicks, Rotations / Minutes)

    private fun withinThreshold(a: Double, b: Double, threshold: Double): Boolean {
        return abs(a - b) < threshold
    }

}