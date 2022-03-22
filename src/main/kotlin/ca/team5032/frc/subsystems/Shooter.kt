package ca.team5032.frc.subsystems

import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.controller.BangBangController
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import kotlin.math.abs

class Shooter : Subsystem<Shooter.State>("Shooter", State.Idle), Tabbed {

    companion object {
        val RPM_THRESHOLD = DoubleProperty("RPM Threshold", 100.0)
        val TARGET_RPM = DoubleProperty("Target RPM", 300.0)
        val POWER = DoubleProperty("Target Speed", 0.35)

        // TODO: Figure out why these aren't accurate? and/or FF doesn't work how I thought.
        const val kS: Double = 0.70719
        const val kV: Double = 0.00031502
        const val kA: Double = 0.0
    }

    sealed class State {
        data class AtSpeed(val speed: Double): State()
        data class RampingUp(val targetSpeed: Double) : State()
        object Idle : State()
    }

    private val shooterFalcon = WPI_TalonFX(SHOOTER_ID)

    // TODO: PID instead of bang bang. Start using PID way more.
    private val bangBangController = BangBangController()
    private val feedforward = SimpleMotorFeedforward(kS, kV)

    init {
        // Set the shooter falcon to coast to prevent the brake from fighting against BangBang.
        shooterFalcon.setNeutralMode(NeutralMode.Coast)
        shooterFalcon.inverted = true
        tab.addString("shooter state") { state.javaClass.simpleName }

        tab.addNumber("Current RPM", ::getRPM)
        buildConfig(RPM_THRESHOLD, TARGET_RPM, POWER)
    }

    override fun periodic() {
        // Acts as synchronized or mutex
        state.let {
            // Simple state machine.
            if (it is State.RampingUp && withinThreshold(getRPM(), it.targetSpeed, RPM_THRESHOLD.value)) {
                state(State.AtSpeed(it.targetSpeed))
            } else if (it is State.AtSpeed && !withinThreshold(getRPM(), it.speed, RPM_THRESHOLD.value)) {
                state(State.RampingUp(it.speed))
            }

            when (it) {
                is State.AtSpeed ->
                    // Assumes feedforward gives passive voltage to maintain speed?
                    //shooterFalcon.setVoltage(feedforward.calculate(it.speed))
                    shooterFalcon.setVoltage(
                        3.7 * bangBangController.calculate(getRPM(), it.speed)
                                + 1 * feedforward.calculate(it.speed * 360 / 60)
                    )
                is State.RampingUp ->
                    // In Principle:
                    //shooterFalcon.set(-0.45)
                    shooterFalcon.setVoltage(
                        3.7 * bangBangController.calculate(getRPM(), it.targetSpeed)
                                + 1 * feedforward.calculate(it.targetSpeed * 360 / 60)
                    )
                    //shooterFalcon.set(TalonFXControlMode.MotionMagic)
                is State.Idle ->
                    shooterFalcon.set(0.0)
            }
        }
    }

    // TODO: Look into making this entire project multithreaded, would help with auto as well.
    fun shoot(targetRPM: Double) = state(State.RampingUp(targetRPM))
    fun stop() = state(State.Idle)

    // units per 100 millisecond -> rotations per minute.
    // 2048 units = 1 rotation.
    private fun getRPM() = -shooterFalcon.selectedSensorVelocity apply (TalonTicks / (100 * Millis) to Rotations / Minutes)

    private fun withinThreshold(a: Double, b: Double, threshold: Double): Boolean {
        return abs(a - b) < threshold
    }

}