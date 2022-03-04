package ca.team5032.frc

import ca.team5032.frc.climb.Climb
import ca.team5032.frc.climb.ClimbDownCommand
import ca.team5032.frc.climb.ClimbUpCommand
import ca.team5032.frc.drive.DriveTrain
import ca.team5032.frc.intake.EjectCommand
import ca.team5032.frc.intake.Intake
import ca.team5032.frc.intake.IntakeCommand
import ca.team5032.frc.led.LEDSystem
import ca.team5032.frc.shooter.Shooter
import ca.team5032.frc.shooter.ShooterCommand
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.button.JoystickButton

object Perseverance : TimedRobot() {

    val driveController = XboxController(0)
    val peripheralController = XboxController(1)

    val drive = DriveTrain()
    val intake = Intake()
    val climb = Climb()
    val shooter = Shooter()

    val led = LEDSystem()

    const val debugMode = true

    override fun robotInit() {
        // Register intake commands.
        JoystickButton(peripheralController, XboxController.Button.kX.value).whenHeld(IntakeCommand())
        JoystickButton(peripheralController, XboxController.Button.kB.value).whenHeld(EjectCommand())

        // Register climb commands.
        JoystickButton(peripheralController,XboxController.Button.kY.value).whenHeld(ClimbUpCommand())
        JoystickButton(peripheralController,XboxController.Button.kA.value).whenHeld(ClimbDownCommand())

        JoystickButton(peripheralController, XboxController.Button.kRightBumper.value).whenHeld(ShooterCommand())
    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

}