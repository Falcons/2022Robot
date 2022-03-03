package ca.team5032.frc

import ca.team5032.frc.climb.Climb
import ca.team5032.frc.climb.ClimbDownCommand
import ca.team5032.frc.climb.ClimbUpCommand
import ca.team5032.frc.drive.DriveTrain
import ca.team5032.frc.intake.EjectCommand
import ca.team5032.frc.intake.Intake
import ca.team5032.frc.intake.IntakeCommand
import ca.team5032.frc.led.LEDSystem
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.button.JoystickButton

object Perseverance : TimedRobot() {

    val driveController = XboxController(0)
    //val peripheralController = XboxController(1)

    val drive = DriveTrain()
    val intake = Intake()
    val climb = Climb()

    val led = LEDSystem()

    const val debugMode = true

    override fun robotInit() {
        // Register intake commands.
        JoystickButton(driveController, XboxController.Button.kX.value).whenHeld(IntakeCommand())
        JoystickButton(driveController, XboxController.Button.kB.value).whenHeld(EjectCommand())

        // Register climb commands.
        JoystickButton(driveController,XboxController.Button.kY.value).whenHeld(ClimbUpCommand())
        JoystickButton(driveController,XboxController.Button.kA.value).whenHeld(ClimbDownCommand())

    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

}