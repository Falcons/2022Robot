package ca.team5032.frc.shooter

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX
import edu.wpi.first.math.Nat
import edu.wpi.first.math.VecBuilder
import edu.wpi.first.math.controller.BangBangController
import edu.wpi.first.math.controller.LinearQuadraticRegulator
import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.estimator.KalmanFilter
import edu.wpi.first.math.numbers.N1
import edu.wpi.first.math.system.LinearSystemLoop
import edu.wpi.first.math.system.plant.LinearSystemId
import edu.wpi.first.wpilibj.XboxController

class Shooter : Subsystem<Shooter.State>(State(0.0, 0.0)), Tabbed {

    companion object {
        val TARGET_RPM = DoubleProperty("Target RPM", 1000.0)

        // TODO: Determine via SysId
        const val kS: Double = 0.0
        const val kA: Double = 0.0
        const val kV: Double = 0.0
    }

    data class State(val targetShooterRPM: Double, val transferSpeed: Double)

    private val controller: XboxController = Perseverance.peripheralController

    private val shooterFalcon = WPI_TalonFX(SHOOTER_ID)
    private val transferVictor = WPI_VictorSPX(TRANSFER_ID)

    private val bangBangController = BangBangController()
    private val feedForward = SimpleMotorFeedforward(kS, kV, kA)

    private val loop: LinearSystemLoop<N1, N1, N1>

    init {
        // Set the shooter falcon to coast to prevent the brake from fighting against BangBang.
        shooterFalcon.setNeutralMode(NeutralMode.Coast)

        tab.addNumber("Actual RPM", ::getRPM)
        buildConfig(TARGET_RPM)

        val linearSystem = LinearSystemId.identifyVelocitySystem(kV, kA)
        val observer = KalmanFilter(Nat.N1(), Nat.N1(), linearSystem, VecBuilder.fill(3.0), VecBuilder.fill(0.01), 0.02)
        val controller = LinearQuadraticRegulator(linearSystem, VecBuilder.fill(100.0), VecBuilder.fill(12.0), 0.02)
        loop = LinearSystemLoop(linearSystem, controller, observer, 12.0, 0.02)
    }

    override fun periodic() {
        // TODO: BangBang + FF
        // https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/bang-bang.html
        // https://docs.wpilib.org/en/stable/docs/software/advanced-controls/controllers/feedforward.html#feedforward-control-in-wpilib

        // In Principle:
        //shooterFalcon.setVoltage(bangBangController.calculate(getRPM(), TARGET_RPM()) + 0.9 * feedForward.calculate(TARGET_RPM()))

        loop.correct(VecBuilder.fill(getRPM()))
        loop.predict(0.02)

        shooterFalcon.setVoltage(loop.u[0, 0])
    }

    fun target(rpm: Double) {
        loop.nextR = VecBuilder.fill(rpm)
    }

    // units per second -> rotations per minute.
    // 2048 units = 1 rotation.
    private fun getRPM() = shooterFalcon.sensorCollection.integratedSensorPosition / 2048 * 60

}