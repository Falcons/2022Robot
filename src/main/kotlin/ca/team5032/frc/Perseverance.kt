package ca.team5032.frc

import ca.team5032.frc.auto.Limelight
import ca.team5032.frc.commands.*
import ca.team5032.frc.led.LEDSystem
import ca.team5032.frc.subsystems.*
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.button.JoystickButton

object Perseverance : TimedRobot(0.02) {

    val driveController = XboxController(0)
    val peripheralController = XboxController(1)

    val drive = DriveTrain()
    val intake = Intake()
    val climb = Climb()
    val transfer = Transfer()
    val shooter = Shooter()

    val limelight = Limelight()
    val led = LEDSystem()

    const val debugMode = true

    override fun robotInit() {
        //LiveWindow.disableAllTelemetry()
        //DriverStation.silenceJoystickConnectionWarning(true)
        registerCommands()
    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

    private fun registerCommands() {
        // Register intake commands.
        JoystickButton(peripheralController, XboxController.Button.kX.value)
            .whenHeld(IntakeInCommand())
        JoystickButton(peripheralController, XboxController.Button.kB.value)
            .whenHeld(IntakeOutCommand())

        // Register climb commands.
        JoystickButton(peripheralController, XboxController.Button.kY.value)
            .whenHeld(ClimbUpCommand())
        JoystickButton(peripheralController, XboxController.Button.kA.value)
            .whenHeld(ClimbDownCommand())

        // Register transfer commands.
        JoystickButton(peripheralController, XboxController.Button.kRightBumper.value)
            .whenHeld(TransferUpCommand())
        JoystickButton(peripheralController, XboxController.Button.kLeftBumper.value)
            .whenHeld(TransferDownCommand())

        // Register shooter commands.
        JoystickButton(peripheralController, XboxController.Button.kBack.value)
            .whenHeld(ShootAtRPMCommand())
    }

}