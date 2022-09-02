package ca.team5032.frc

import ca.team5032.frc.auto.AlignToTargetCommand
import ca.team5032.frc.auto.DriveDistanceCommand
import ca.team5032.frc.auto.Limelight
import ca.team5032.frc.auto.routines.*
import ca.team5032.frc.led.LEDSystem
import ca.team5032.frc.subsystems.*
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.button.JoystickButton
import edu.wpi.first.wpilibj2.command.button.POVButton

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

    private lateinit var autoCommand: Command
    private val autoChooser = SendableChooser<Command>()

    override fun robotInit() {
        LiveWindow.disableAllTelemetry()
        DriverStation.silenceJoystickConnectionWarning(true)
        registerCommands()

        autoChooser.setDefaultOption("3 Ball + 2 Pickup (slow 5?)", FiveBallAutoCommand())
        autoChooser.addOption("3 Ball", ThreeBallAutoCommand())
        autoChooser.addOption("2 Ball (start of 5 ball path)", TwoBallAltAutoCommand())
        autoChooser.addOption("2 Ball (stray ball)", TwoBallAutoCommand())
        autoChooser.addOption("1 Ball", OneBallAutoCommand())
        autoChooser.addOption("Taxi", DriveDistanceCommand(1.00))
        autoChooser.addOption("Do Nothing", InstantCommand())

        SmartDashboard.putData(autoChooser)
        println("Alliance: ${DriverStation.getAlliance().name}")
    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

    private fun registerCommands() {
        // Register intake commands.
        JoystickButton(peripheralController, XboxController.Button.kX.value)
            .whenPressed(intake::cycle, intake).whenReleased(intake::stop, intake)
        JoystickButton(peripheralController, XboxController.Button.kB.value)
            .whenPressed(intake::eject, intake).whenReleased(intake::stop, intake)

        // Register climb commands.
        JoystickButton(peripheralController, XboxController.Button.kY.value)
            .whenActive(climb::up, climb)
            .whenInactive(climb::stop, climb)

        JoystickButton(peripheralController, XboxController.Button.kA.value)
            .whenActive(climb::down, climb)
            .whenInactive(climb::stop, climb)

        // Register shooter commands.
        JoystickButton(peripheralController, XboxController.Button.kRightBumper.value)
            .whenPressed({ shooter.shoot() }, shooter).whenReleased(shooter::stop, shooter)

        JoystickButton(peripheralController, XboxController.Button.kLeftBumper.value)
            .whenPressed({ shooter.shoot { 2400.0 } }, shooter).whenReleased(shooter::stop, shooter)

        // Register transfer commands.
        POVButton(peripheralController, 270)
            .whenPressed(transfer::up, transfer).whenReleased(transfer::stop, transfer)
        POVButton(peripheralController, 90)
            .whenPressed(transfer::down, transfer).whenReleased(transfer::stop, transfer)

        POVButton(peripheralController, 0)
            .whenPressed(intake::raise, intake)
        POVButton(peripheralController, 180)
            .whenPressed(intake::deploy, intake)

        val command = AlignToTargetCommand({ Limelight.Pipeline.ReflectiveTape }, -1)
        JoystickButton(driveController, XboxController.Button.kLeftBumper.value)
            .whenPressed({ command.schedule() }, limelight)
            .whenReleased({ command.cancel() }, limelight)

        val command2 = AlignToTargetCommand({ Limelight.Pipeline.ReflectiveTape }, 1)
        JoystickButton(driveController, XboxController.Button.kRightBumper.value)
            .whenPressed({ command2.schedule() }, limelight)
            .whenReleased({ command2.cancel() }, limelight)
    }

    override fun autonomousInit() {
        drive.gyro.yaw = 0.0

        autoCommand = autoChooser.selected
        autoCommand.schedule()
    }

    override fun autonomousExit() {
        autoCommand.cancel()
    }

}