package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.MecanumDrive
import kotlin.math.abs

// TODO: Add kinematics and odometry for pose estimation during autonomous.
// https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/mecanumbot/Drivetrain.java
// https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/mecanum-drive-odometry.html
// https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/mecanum-drive-kinematics.html
class DriveTrain : Subsystem<DriveTrain.State>(State.Idle), Tabbed {

    companion object {
        // Threshold to consider the robot as moving  (receiving joystick input)
        val DEADBAND_THRESHOLD = DoubleProperty("Deadband Threshold", 0.1)
        // Sensitivity for ySpeed cartesian movement. (north-south)
        val Y_SENSITIVITY = DoubleProperty("Y Sensitivity", 0.4)
        // Sensitivity for xSpeed cartesian movement. (south-west)
        val X_SENSITIVITY = DoubleProperty("X Sensitivity", 0.5)

        // Constant rotation speed for the robot.
        val ROTATION_SPEED = DoubleProperty("Rotation Speed", 0.45)
        val FAST_ROTATION = DoubleProperty("Rotation Speed", 0.65)
        // Magnitude of micro movements done by the dpad.
        val MICRO_SPEED = DoubleProperty("Micro Speed", 0.5)
    }

    sealed class State {
        object Autonomous : State() // TODO: Proper autonomous mode for all subsystems, locks normal input and only controllable through auto.
        object Driving : State()
        object Idle : State()
    }

    private val controller: XboxController = Perseverance.driveController

    private val drive: MecanumDrive
    private val falcons: List<WPI_TalonFX> = listOf(
        WPI_TalonFX(FRONT_LEFT_ID),
        WPI_TalonFX(REAR_LEFT_ID),
        WPI_TalonFX(FRONT_RIGHT_ID),
        WPI_TalonFX(REAR_RIGHT_ID)
    )

    // TODO: Caused weird delay errors? investigate.
    private val ySpeedRateLimiter = SlewRateLimiter(0.5)
    private val xSpeedRateLimiter = SlewRateLimiter(0.5)
    private val zRotationRateLimiter = SlewRateLimiter(0.5)

    private val isInput: Boolean
        get() = abs(controller.leftY) > DEADBAND_THRESHOLD.value
                || abs(controller.leftX) > DEADBAND_THRESHOLD.value
                || controller.pov != -1

    init {
        falcons[2].inverted = true
        falcons[3].inverted = true

        falcons.forEach { it.setNeutralMode(NeutralMode.Brake) }

        drive = MecanumDrive(
            falcons[0], falcons[1], falcons[2], falcons[3]
        )

        buildConfig(DEADBAND_THRESHOLD, Y_SENSITIVITY, X_SENSITIVITY, ROTATION_SPEED, MICRO_SPEED)
    }

    override fun periodic() {
        if (isInput) {
            setState(State.Driving)
        } else {
            setState(State.Idle)
        }

        if (Perseverance.isDisabled) return

        if (controller.pov != -1) {
            drive.drivePolar(
                MICRO_SPEED.value,
                controller.pov.toDouble(),
                0.0
            )
            return
        }

        val rotation = controller.rightX * ROTATION_SPEED.value
//        if (controller.leftBumper) rotation -= ROTATION_SPEED.value
//        if (controller.rightBumper) rotation += ROTATION_SPEED.value
//        if (controller.leftTriggerAxis > 0.05) rotation -= FAST_ROTATION.value
//        if (controller.rightTriggerAxis > 0.05) rotation += FAST_ROTATION.value

        val additionalMult = if (controller.xButton) 1.65 else 1.0

        drive.driveCartesian(
            -controller.leftY * Y_SENSITIVITY.value * additionalMult,
            controller.leftX * X_SENSITIVITY.value * additionalMult,
            rotation
        )
    }

}