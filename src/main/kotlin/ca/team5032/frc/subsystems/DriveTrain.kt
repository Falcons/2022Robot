package ca.team5032.frc.subsystems

import ca.team5032.frc.Perseverance
import ca.team5032.frc.utils.*
import ca.team5032.frc.utils.motor.Falcon500
import ca.team5032.frc.utils.motor.MotorProfile
import edu.wpi.first.math.controller.PIDController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.kinematics.MecanumDriveKinematics
import edu.wpi.first.math.kinematics.MecanumDriveOdometry
import edu.wpi.first.math.kinematics.MecanumDriveWheelSpeeds
import edu.wpi.first.wpilibj.ADIS16448_IMU
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

// https://github.com/wpilibsuite/allwpilib/blob/main/wpilibjExamples/src/main/java/edu/wpi/first/wpilibj/examples/mecanumbot/Drivetrain.java
// https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/mecanum-drive-odometry.html
// https://docs.wpilib.org/en/stable/docs/software/kinematics-and-odometry/mecanum-drive-kinematics.html
class DriveTrain : Subsystem<DriveTrain.State>("Drive", State.Idle), Tabbed {

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
    }

    data class DriveInput(var ySpeed: Double, var xSpeed: Double, var zRotation: Double)

    sealed class State {
        object Autonomous : State()
        object Driving : State()
        object Idle : State()
    }

    val frontLeft = Falcon500(FRONT_LEFT_ID)
    val rearLeft = Falcon500(REAR_LEFT_ID)
    val frontRight = Falcon500(FRONT_RIGHT_ID)
    val rearRight = Falcon500(REAR_RIGHT_ID)

    private val field = Field2d()

    val gyro = ADIS16448_IMU()
    private val controller: XboxController = Perseverance.driveController

    private val odometry: MecanumDriveOdometry
    var pose = Pose2d(Translation2d(0.0, 0.0), Rotation2d(0.0)) // TODO: Determine starting pose?

    val autonomousInput = DriveInput(0.0, 0.0, 0.0)
    val rotationController = PIDController(0.01, 0.0, 0.0)

    private val hasInput: Boolean
        get() = abs(controller.leftY) > DEADBAND_THRESHOLD.value
                || abs(controller.leftX) > DEADBAND_THRESHOLD.value
                || abs(controller.rightX) > DEADBAND_THRESHOLD.value
                || controller.pov != -1

    init {
        frontRight.inverted = true
        rearRight.inverted = true

        MotorProfile.DriveConfig.apply(frontLeft, rearLeft, frontRight, rearRight)

        tab.add("ADIS Gyro", gyro)
        tab.add("Field2d", field)
        //tab.add(rotationController)

        gyro.calibrate()

        val kinematics = MecanumDriveKinematics(
            Translation2d(0.31, 0.28),
            Translation2d(0.31, -0.28),
            Translation2d(-0.31, 0.28),
            Translation2d(-0.31, -0.28)
        )
        // TODO: Is it possible to construct starting pose from distance to HUB and balls? might not be worth the calculation
        // for auto tho.
        odometry = MecanumDriveOdometry(kinematics, Rotation2d(0.0))

        tab.add("encoder vals") {
            it.addDoubleProperty("Left Front", { frontLeft.sensorCollection.integratedSensorPosition }, {})
            it.addDoubleProperty("Left Rear", { rearLeft.sensorCollection.integratedSensorPosition }, {})
            it.addDoubleProperty("Right Front", { frontRight.sensorCollection.integratedSensorPosition }, {})
            it.addDoubleProperty("Right Rear", { rearRight.sensorCollection.integratedSensorPosition }, {})
        }

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
                changeState(State.Driving)
            } else if (state !is State.Idle) {
                changeState(State.Idle)
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

        val wheelSpeeds = MecanumDriveWheelSpeeds(
            frontLeft.velocity(),
            rearLeft.velocity(),
            frontRight.velocity(),
            rearRight.velocity()
        )
        val gyroRadians = Rotation2d.fromDegrees(-gyro.angle)

        pose = odometry.update(gyroRadians, wheelSpeeds)

        Perseverance.limelight.getPoseOrNull()?.let {
            odometry.resetPosition(it, Rotation2d(0.0))
            pose = it
        }

        field.robotPose = odometry.poseMeters
    }

}