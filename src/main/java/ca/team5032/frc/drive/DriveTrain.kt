package ca.team5032.frc.drive

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.DoubleProperty
import ca.team5032.frc.utils.Tabbed
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.MecanumDrive
import edu.wpi.first.wpilibj2.command.SubsystemBase
import kotlin.math.abs

enum class DirectionX(val multiplier: Int) {
    FORWARD(1),
    BACKWARD(-1)
}

enum class DirectionYZ(val multiplier: Int) {
    LEFT(-1),
    RIGHT(1)
}

class DriveTrain : SubsystemBase(), Tabbed {

    companion object {
        // IDs for the 4 drive Falcon500s.
        const val FRONT_LEFT_ID = 0
        const val REAR_LEFT_ID = 1
        const val FRONT_RIGHT_ID = 2
        const val REAR_RIGHT_ID = 3

        // Threshold to consider the robot as moving  (receiving joystick input)
        val DEADBAND_THRESHOLD = DoubleProperty("Deadband Threshold", 0.1)
        // Sensitivity for ySpeed cartesian movement. (north-south)
        val Y_SENSITIVITY = DoubleProperty("Y Sensitivity", 0.4)
        // Sensitivity for xSpeed cartesian movement. (south-west)
        val X_SENSITIVITY = DoubleProperty("X Sensitivity", 0.5)

        // Constant rotation speed for the robot.
        val ROTATION_SPEED = DoubleProperty("Rotation Speed", 0.45)
        // Magnitude of micro movements done by the dpad.
        val MICRO_SPEED = DoubleProperty("Micro Speed", 0.5)
    }

    enum class State {
        AUTONOMOUS, // TODO: Proper autonomous mode for all subsystems, locks normal input and only controllable through auto.
        DRIVING,
        STATIONARY
    }

    data class AutoSpeeds(val ySpeed: Double, val xSpeed: Double, val zRotation: Double)

    var state: State = State.STATIONARY
    var autoSpeeds = AutoSpeeds(0.0, 0.0, 0.0)

    private val controller: XboxController = Perseverance.driveController

    private val drive: MecanumDrive
    private val falcons: List<WPI_TalonFX> = listOf(
        WPI_TalonFX(FRONT_LEFT_ID),
        WPI_TalonFX(REAR_LEFT_ID),
        WPI_TalonFX(FRONT_RIGHT_ID),
        WPI_TalonFX(REAR_RIGHT_ID)
    )


    private val isInput: Boolean
        get() = abs(controller.leftY) > DEADBAND_THRESHOLD()
                || abs(controller.leftX) > DEADBAND_THRESHOLD()
                || controller.pov != -1

    init {
        falcons[2].inverted = true
        falcons[3].inverted = true

        drive = MecanumDrive(
            falcons[0], falcons[1], falcons[2], falcons[3]
        )

        if (Perseverance.debugMode) {
            tab.addString("State") { state.name }
            tab.add("Mecanum Visualizer") { drive }
        }

        buildConfig(DEADBAND_THRESHOLD, Y_SENSITIVITY, X_SENSITIVITY, ROTATION_SPEED, MICRO_SPEED)
    }

    fun auto(speeds: AutoSpeeds) {
        if (state != State.AUTONOMOUS) return

        autoSpeeds = speeds
    }

    override fun periodic() {
        if (state == State.AUTONOMOUS) {
            drive.driveCartesian(autoSpeeds.ySpeed, autoSpeeds.xSpeed, autoSpeeds.zRotation)

            autoSpeeds = AutoSpeeds(0.0, 0.0, 0.0)
            return
        }

        if (Perseverance.isDisabled || state == State.AUTONOMOUS) return

        if (isInput) {
            if (state == State.STATIONARY) unlock()
        } else {
            if (state == State.DRIVING) lock()
        }

        if (controller.pov != -1) {
            drive.drivePolar(
                MICRO_SPEED(),
                controller.pov.toDouble(),
                0.0
            )
            return
        }

        var rotation = 0.0
        if (controller.leftBumper) rotation -= ROTATION_SPEED()
        if (controller.rightBumper) rotation += ROTATION_SPEED()

        val additionalMult = if (controller.xButton) 1.5 else 1.0

        drive.driveCartesian(
            -controller.leftY * Y_SENSITIVITY() * additionalMult,
            controller.leftX * X_SENSITIVITY() * additionalMult,
            rotation
        )
    }

    /**
     * Unlocks the drivetrain, allowing the robot to move around freely.
     */
    private fun unlock() {
        falcons.forEach { it.setNeutralMode(NeutralMode.Coast) }
        state = State.DRIVING
    }

    /**
     * Locks the drivetrain, preventing the robot from moving and making any impact less extreme.
     */
    private fun lock() {
        falcons.forEach { it.setNeutralMode(NeutralMode.Brake) }
        state = State.STATIONARY
    }

}