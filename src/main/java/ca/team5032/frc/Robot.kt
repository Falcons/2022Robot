package ca.team5032.frc

import com.ctre.phoenix.motorcontrol.can.WPI_TalonFX
import edu.wpi.first.math.filter.SlewRateLimiter
import edu.wpi.first.wpilibj.ADIS16448_IMU
import edu.wpi.first.wpilibj.Joystick
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.drive.MecanumDrive

class Robot : TimedRobot() {

    // TODO: Switch to navX-MXP.
    private lateinit var imu: ADIS16448_IMU;

    private lateinit var joystick: Joystick
    private lateinit var mecanumDrive: MecanumDrive

    // Rate limiters to limit jerkiness of the robot
    private lateinit var xFilter: SlewRateLimiter
    private lateinit var yFilter: SlewRateLimiter
    private lateinit var zFilter: SlewRateLimiter

    private lateinit var falcons: List<WPI_TalonFX>

    override fun robotInit() {
        imu = ADIS16448_IMU()
        joystick = Joystick(0)

        falcons = listOf(
            WPI_TalonFX(0),
            WPI_TalonFX(1),
            WPI_TalonFX(2),
            WPI_TalonFX(3),
        )

        mecanumDrive = MecanumDrive(
            /* frontLeftMotor  */ falcons[0],
            /* rearLeftMotor   */ falcons[1],
            /* frontRightMotor */ falcons[2],
            /* rearRightMotor  */ falcons[3]
        )

        // Enable safety on the mecanum drive.
        mecanumDrive.isSafetyEnabled = true

        // Initialize the rate limiters, which limit the rate change of movement values.
        val rateLimit = 0.5
        xFilter = SlewRateLimiter(rateLimit)
        yFilter = SlewRateLimiter(rateLimit)
        zFilter = SlewRateLimiter(rateLimit)
    }

    override fun teleopPeriodic() {
        var rotation = 0.0
        // 8 ccw, 9 cw
        if (joystick.getRawButton(8)) rotation -= 0.2
        if (joystick.getRawButton(9)) rotation += 0.2

        mecanumDrive.driveCartesian(
            -joystick.y.apply(yFilter::calculate),
            joystick.x.apply(xFilter::calculate),
            rotation,
            imu.angle
        )
    }

}