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

class DriveTrain : SubsystemBase(), Tabbed {

    companion object {
        // IDs for the 4 drive Falcon500s.
        const val FRONT_LEFT_ID = 0
        const val REAR_LEFT_ID = 1
        const val FRONT_RIGHT_ID = 2
        const val REAR_RIGHT_ID = 3

        // TODO: Look into a generic 'Property' class to represent modifiable properties for subsystems, automatically
        // TODO: putting them into the Config tab in Shuffleboard.
        // Threshold to consider the robot as moving  (receiving joystick input)
        var DEADBAND_THRESHOLD = DoubleProperty("Deadband Threshold", 0.1)
        // Sensitivity for ySpeed cartesian movement. (north-south)
        var Y_SENSITIVITY = DoubleProperty("Y Sensitivity", 0.4)
        // Sensitivity for xSpeed cartesian movement. (south-west)
        var X_SENSITIVITY = DoubleProperty("X Senstivity", 0.5)

        // Constant rotation speed for the robot.
        var ROTATION_SPEED = DoubleProperty("Rotation Speed", 0.45)
        // Magnitude of micro movements done by the dpad.
        var MICRO_SPEED = DoubleProperty("Micro Speed", 0.4)
    }

    enum class State {
        AUTONOMOUS,
        DRIVING,
        STATIONARY
    }

    var state: State = State.STATIONARY

    private val controller: XboxController = Perseverance.driveController

    private val drive: MecanumDrive
    private val falcons: List<WPI_TalonFX> = listOf(
        WPI_TalonFX(FRONT_LEFT_ID),
        WPI_TalonFX(REAR_LEFT_ID),
        WPI_TalonFX(FRONT_RIGHT_ID),
        WPI_TalonFX(REAR_RIGHT_ID)
    )

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

    override fun periodic() {
        if (Perseverance.isDisabled || state == State.AUTONOMOUS) return

        if (abs(controller.leftY) > DEADBAND_THRESHOLD()
            || abs(controller.leftX) > DEADBAND_THRESHOLD()
            || controller.pov != -1) {
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

        drive.driveCartesian(
            -controller.leftY * Y_SENSITIVITY(),
            controller.leftX * X_SENSITIVITY(),
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