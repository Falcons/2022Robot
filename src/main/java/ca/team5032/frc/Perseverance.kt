package ca.team5032.frc

import ca.team5032.frc.drive.DriveTrain
import ca.team5032.frc.intake.EjectCommand
import ca.team5032.frc.intake.Intake
import ca.team5032.frc.intake.IntakeCommand
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.button.JoystickButton

object Perseverance : TimedRobot() {

    val driveController = XboxController(0)
    val drive = DriveTrain()
    val intake = Intake()

    const val debugMode = true

    override fun robotInit() {
        // TODO: move, but keep this initialization
        // imu = ADIS16448_IMU(ADIS16448_IMU.IMUAxis.kZ, SPI.Port.kMXP, ADIS16448_IMU.CalibrationTime._1s)

        JoystickButton(driveController, XboxController.Button.kX.value).whenHeld(IntakeCommand())
        JoystickButton(driveController, XboxController.Button.kB.value).whenHeld(EjectCommand())
    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

}