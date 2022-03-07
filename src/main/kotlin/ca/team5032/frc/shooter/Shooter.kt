package ca.team5032.frc.shooter

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.math.controller.BangBangController
import edu.wpi.first.wpilibj.XboxController

class Shooter : Subsystem<Shooter.State>(State(0.0, 0.0)), Tabbed {

    companion object {
        val TARGET_RPM = DoubleProperty("Target RPM", 1000.0)
    }

    data class State(val targetShooterRPM: Double, val transferSpeed: Double)

    private val controller: XboxController = Perseverance.peripheralController

    private val shooterFalcon = WPI_TalonFX(SHOOTER_ID)
    private val transferVictor = WPI_VictorSPX(TRANSFER_ID)

    private val bangBangController = BangBangController()

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

        // bangBangController.calculate(getRPM(), TARGET_RPM())
    }

    // units per second -> rotations per minute.
    // 2048 units = 1 rotation.
    private fun getRPM() = shooterFalcon.sensorCollection.integratedSensorPosition / 2048 * 60

}