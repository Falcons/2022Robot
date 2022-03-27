package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.controller.ProfiledPIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.MecanumDriveKinematics
import edu.wpi.first.math.kinematics.MecanumDriveOdometry
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds
import edu.wpi.first.math.trajectory.TrapezoidProfile
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import edu.wpi.first.wpilibj.ADIS16448_IMU
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.MecanumDrive
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/mecanumbot/Drivetrain.java
// https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/mecanum-drive-odometry.html
// https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/mecanum-drive-kinematics.html
class DriveTrain : Subsystem<DriveTrain.State>("Drive", State.Idle), Tabbed, Sendable {

    companion object {
        // Threshold to consider the robot as moving  (receiving joystick input)
        val DEADBAND_THRESHOLD = DoubleProperty("Deadband Threshold", 0.05)
        // Sensitivity for ySpeed cartesian movement. (north-south)
        val Y_SENSITIVITY = DoubleProperty("Y Sensitivity", 1.0)
        // Sensitivity for xSpeed cartesian movement. (south-west)
        val X_SENSITIVITY = DoubleProperty("X Sensitivity", 1.0)

        // Constant rotation speed for the robot.
        val ROTATION_SPEED = DoubleProperty("Rotation Speed", 0.45)
        val FAST_ROTATION = DoubleProperty("Rotation Speed", 0.65)
        // Magnitude of micro movements done by the dpad.
        val MICRO_SPEED = DoubleProperty("Micro Speed", 0.5)

        val ANGULAR_CONVERSION = 0.47877872 * (Metres / Rotations)
    }

    data class DriveInput(var ySpeed: Double, var xSpeed: Double, var zRotation: Double)

    sealed class State {
        object Autonomous : State() // TODO: Proper autonomous mode for all subsystems, locks normal input and only controllable through auto.
        object Driving : State()
        object Idle : State()
    }

    private val field = Field2d()

    private val gyro = ADIS16448_IMU()
    private val controller: XboxController = Perseverance.driveController

    private val frontLeft = WPI_TalonFX(FRONT_LEFT_ID)
    private val rearLeft = WPI_TalonFX(REAR_LEFT_ID)
    private val frontRight = WPI_TalonFX(FRONT_RIGHT_ID)
    private val rearRight = WPI_TalonFX(REAR_RIGHT_ID)

    private val odometry: MecanumDriveOdometry
    private var pose = Pose2d(Translation2d(0.0, 0.0), Rotation2d(0.0)) // TODO: Determine starting pose?

    val autonomousInput = DriveInput(0.0, 0.0, 0.0)
    private val rotationController = ProfiledPIDController(0.0, 0.0, 0.0, TrapezoidProfile.Constraints(6.28, 3.14))

    private val hasInput: Boolean
        get() = abs(controller.leftY) > DEADBAND_THRESHOLD.value
                || abs(controller.leftX) > DEADBAND_THRESHOLD.value
                || abs(controller.rightX) > DEADBAND_THRESHOLD.value
                || controller.pov != -1

    init {
        frontRight.inverted = true
        rearRight.inverted = true

        // TODO: A motor configuration profile utility for configuring all motors as required (also impl current lims)
        // Would also wrap all the motors, allowing increased logging (temps).
        listOf(frontLeft, rearLeft, frontRight, rearRight)
            .forEach { it.setNeutralMode(NeutralMode.Brake) }

        tab.add("ADIS Gyro", gyro)
        tab.add("Field2d", field)

        gyro.calibrate()

        val kinematics = MecanumDriveKinematics(
            Translation2d(0.31, 0.28),
            Translation2d(0.31, -0.28),
            Translation2d(-0.31, 0.28),
            Translation2d(-0.31, -0.28)
        )
        // TODO: Is it possible to construct starting pose from distance to HUB and balls? might not be worth the calculation
        // for auto tho.
        odometry = MecanumDriveOdometry(kinematics, Rotation2d(gyro.gyroAngleZ))

        listOf(frontLeft, rearLeft, frontRight, rearRight)
            .forEach { it.configSelectedFeedbackSensor(FeedbackDevice.IntegratedSensor) }

        buildConfig(DEADBAND_THRESHOLD, Y_SENSITIVITY, X_SENSITIVITY, ROTATION_SPEED, MICRO_SPEED)
    }

    private fun getInput(): DriveInput {
        return when (state) {
            is State.Autonomous -> autonomousInput
            is State.Driving -> DriveInput(
                    -controller.leftY * Y_SENSITIVITY.value,
                    controller.leftX * X_SENSITIVITY.value,
                    controller.rightX * ROTATION_SPEED.value
                )
            else -> DriveInput(0.0, 0.0, 0.0)
        }
    }

    override fun periodic() {
        if (Perseverance.isDisabled) return

        if (state !is State.Autonomous) {
            if (hasInput) {
                state = State.Driving
            } else if (state !is State.Idle) {
                state = State.Idle
            }
        }

        var (ySpeed, xSpeed, zRotation) = getInput()

        if (state is State.Driving) {
            if (controller.pov != -1) {
                ySpeed = MICRO_SPEED.value * cos(controller.pov.toDouble() * (PI / 180.0))
                xSpeed = MICRO_SPEED.value * sin(controller.pov.toDouble() * (PI / 180.0))
            }
        }

        val motorOutputs = driveCartesianIK(ySpeed, xSpeed, zRotation)
        frontLeft.set(motorOutputs[0])
        frontRight.set(motorOutputs[1])
        rearLeft.set(motorOutputs[2])
        rearRight.set(motorOutputs[3])

        val encoder = TalonTicks / (100 * Millis)
        val rps = Rotations / Seconds

        val wheelSpeeds = MecanumDriveWheelSpeeds(
            // TODO: wrap falcon500s so encoders more accessible and in proper units, holy shit.
            frontLeft.selectedSensorVelocity apply (encoder to rps) apply ANGULAR_CONVERSION,
            rearLeft.selectedSensorVelocity apply (encoder to rps) apply ANGULAR_CONVERSION,
            frontRight.selectedSensorVelocity apply (encoder to rps) apply ANGULAR_CONVERSION,
            rearRight.selectedSensorVelocity apply (encoder to rps) apply ANGULAR_CONVERSION
        )
        val gyroRadians = Rotation2d.fromDegrees(-gyro.gyroAngleZ)

        pose = odometry.update(gyroRadians, wheelSpeeds)
        field.robotPose = odometry.poseMeters
    }

    override fun initSendable(builder: SendableBuilder) {
        builder.addDoubleProperty("Gyroscope Reading", { gyro.gyroAngleZ }) {}
    }

}