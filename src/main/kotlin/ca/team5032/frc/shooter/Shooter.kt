package ca.team5032.frc.shooter

import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.SHOOTER_ID
import ca.team5032.frc.utils.Subsystem
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.controller.BangBangController
import edu.wpi.first.math.controller.SimpleMotorFeedforward

class Shooter : Subsystem<Shooter.State>(State(0.0)), Tabbed {

    companion object {
        // Just for testing, RPM should be dynamically calculated based on distance to hub.
        val TARGET_RPM = DoubleProperty("Target RPM", 1000.0)

        // TODO: Determine via SysId
        const val kS: Double = 0.0
        const val kA: Double = 0.0
        const val kV: Double = 0.0
    }

    data class State(val targetShooterRPM: Double)

    private val shooterFalcon = WPI_TalonFX(SHOOTER_ID)

    private val bangBangController = BangBangController()
    private val feedforward = SimpleMotorFeedforward(kS, kV, kA)

    init {
        // Set the shooter falcon to coast to prevent the brake from fighting against BangBang.
        shooterFalcon.setNeutralMode(NeutralMode.Coast)

        tab.addNumber("Actual RPM", ::getRPM)
        buildConfig(TARGET_RPM)
    }

    override fun periodic() {
        // TODO: BangBang + FF
        // https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/bang-bang.html
        // https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/feedforward.html#feedforward-control-in-wpilib

        // In Principle:
        shooterFalcon.setVoltage(bangBangController.calculate(getRPM(), TARGET_RPM.value) + 0.9 * feedforward.calculate(TARGET_RPM.value))
    }

    // units per second -> rotations per minute.
    // 2048 units = 1 rotation.
    private fun getRPM() = shooterFalcon.sensorCollection.integratedSensorPosition / 2048 * 60

}