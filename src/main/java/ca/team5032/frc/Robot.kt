package ca.team5032.frc

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.wpilibj.ADIS16448_IMU
import edu.wpi.first.wpilibj.SPI
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.drive.MecanumDrive
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard

object Robot : TimedRobot() {

    // TODO: Switch to navX-MXP.
    private lateinit var imu: ADIS16448_IMU;

    private lateinit var joystick: XboxController
    private lateinit var mecanumDrive: MecanumDrive

    // Rate limiters to limit jerkiness of the robot
    private lateinit var xFilter: SlewRateLimiter
    private lateinit var yFilter: SlewRateLimiter
    private lateinit var zFilter: SlewRateLimiter

    private lateinit var falcons: List<WPI_TalonFX>

    override fun robotInit() {
        imu = ADIS16448_IMU(ADIS16448_IMU.IMUAxis.kZ, SPI.Port.kMXP, ADIS16448_IMU.CalibrationTime._1s)
        joystick = XboxController(0)

        falcons = listOf(
            WPI_TalonFX(0),
            WPI_TalonFX(1),
            WPI_TalonFX(2),
            WPI_TalonFX(3),
        )

        val nameMapping = listOf(
            "Front Left",
            "Rear Left",
            "Front Right",
            "Rear Right"
        )

        val driveDebug = Shuffleboard.getTab("Drive Debug")

        for (falcon in falcons) {
            driveDebug.add("Falcon ${falcon.deviceID} (${nameMapping[falcon.deviceID]})") {
                it.addDoubleProperty("Firmware Version", {
                    falcon.firmwareVersion.toDouble()
                }, {})
                it.addStringProperty("Control Mode", { falcon.controlMode.name }, {})
                it.addDoubleProperty("Speed", { falcon.get() }, {})
                it.addDoubleProperty("Temperature", { falcon.temperature }, {})
            }
        }

        falcons.forEach { it.setNeutralMode(NeutralMode.Brake) }
        falcons[2].inverted = true
        falcons[3].inverted = true

        mecanumDrive = MecanumDrive(
            /* frontLeftMotor  */ falcons[0],
            /* rearLeftMotor   */ falcons[1],
            /* frontRightMotor */ falcons[2],
            /* rearRightMotor  */ falcons[3]
        )

        driveDebug.add("Mecanum Drive", mecanumDrive)

        // Enable safety on the mecanum drive.
        mecanumDrive.isSafetyEnabled = false

        // Initialize the rate limiters, which limit the rate change of movement values.
        val rateLimit = 0.5
        xFilter = SlewRateLimiter(rateLimit)
        yFilter = SlewRateLimiter(rateLimit)
        zFilter = SlewRateLimiter(rateLimit)
    }

    override fun teleopPeriodic() {
        if (joystick.leftY > 0.1 || joystick.leftX > 0.1 || joystick.pov != -1) {
            falcons.forEach { it.setNeutralMode(NeutralMode.Coast) }
        } else {
            falcons.forEach { it.setNeutralMode(NeutralMode.Brake) }
        }

        if (joystick.pov != -1) {
            mecanumDrive.drivePolar(
                0.4,
                joystick.pov.toDouble(),
                0.0
            )
            return
        }

        var rotation = 0.0
        if (joystick.leftBumper) rotation -= 0.45
        if (joystick.rightBumper) rotation += 0.45

        mecanumDrive.driveCartesian(
            -joystick.leftY * 0.4,
            joystick.leftX * 0.5,
            rotation
        )
    }

}