package ca.team5032.frc

import ca.team5032.frc.auto.*
import ca.team5032.frc.led.LEDSystem
import ca.team5032.frc.subsystems.*
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj2.command.*
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

    private val autonomousRoutine = SequentialCommandGroup(
        InstantCommand({ intake.deployIntake() }),
        WaitCommand(0.3),
        InstantCommand({ intake.intake() }),
        DriveForwardCommand(.70),
        WaitUntilCommand { intake.hasBall() },
        InstantCommand({ intake.stop() }),
        RotateToAngleCommand(180.0),
        InstantCommand({ println("Done rotating!") }),
        ShootAmountCommand(2),
        RotateToAngleCommand(-77.0),
        InstantCommand({
            intake.intake()
            transfer.up()
        }),
        DriveForwardCommand(2.8),
        WaitUntilCommand { transfer.hasBall() },
        InstantCommand({
            intake.stop()
            transfer.stop()
        }),
        RotateToAngleCommand(105.0),
        ShootAmountCommand(1),
        RotateToAngleCommand(180.0)
//        RotateToAngleCommand(360 - 125.00),
//        InstantCommand({
//            intake.intake()
//            transfer.up()
//        }),
//        DriveForwardCommand(2.2),
//        WaitUntilCommand { transfer.hasBall() },
//        InstantCommand({
//            intake.stop()
//            transfer.stop()
//        }),
//        RotateToAngleCommand(165.00),
//        DriveForwardCommand(2.00),
//        ShootAmountCommand(1)

    )

    override fun robotInit() {
        LiveWindow.disableAllTelemetry()
        //DriverStation.silenceJoystickConnectionWarning(true)
        registerCommands()

//        val camera = CameraServer.startAutomaticCapture("Driver cam", 0);
//
//         // Default configuration for the camera. 60fps 320p keepOpen.
//         camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen)
//         camera.setFPS(60)
//         camera.setResolution(320, 240)
    }

    override fun teleopInit() {
        drive.gyro.yaw = 0.0
    }

    override fun robotPeriodic() {
        CommandScheduler.getInstance().run()
    }

    private fun registerCommands() {
        // Register intake commands.
        JoystickButton(peripheralController, XboxController.Button.kX.value)
            .whenPressed(intake::intake, intake).whenReleased(intake::stop, intake)
        JoystickButton(peripheralController, XboxController.Button.kB.value)
            .whenPressed(intake::eject, intake).whenReleased(intake::stop, intake)

        // Register climb commands.
        JoystickButton(peripheralController, XboxController.Button.kY.value)
            .whenPressed(climb::up, climb).whenReleased(climb::stop, climb)
        JoystickButton(peripheralController, XboxController.Button.kA.value)
            .whenPressed(climb::down, climb).whenReleased(climb::stop, climb)

        // Register shooter commands.
        JoystickButton(peripheralController, XboxController.Button.kRightBumper.value)
            .whenPressed({ shooter.shoot() }, shooter).whenReleased(shooter::stop, shooter)

        // Register transfer commands.
        POVButton(peripheralController, 270)
            .whenPressed(transfer::up, transfer).whenReleased(transfer::stop, transfer)
        POVButton(peripheralController, 90)
            .whenPressed(transfer::down, transfer).whenReleased(transfer::stop, transfer)

        POVButton(peripheralController, 0)
            .whenPressed(intake::raiseIntake, intake)
        POVButton(peripheralController, 180)
            .whenPressed(intake::deployIntake, intake)

        val command = AlignToTargetCommand(Limelight.Pipeline.ReflectiveTape, -1)
        JoystickButton(driveController, XboxController.Button.kLeftBumper.value)
            .whenPressed({ command.schedule() }, limelight)
            .whenReleased({ command.cancel() }, limelight)

        val command2 = AlignToTargetCommand(Limelight.Pipeline.ReflectiveTape, 1)
        JoystickButton(driveController, XboxController.Button.kRightBumper.value)
            .whenPressed({ command2.schedule() }, limelight)
            .whenReleased({ command2.cancel() }, limelight)
    }

    override fun autonomousInit() {
        autonomousRoutine.schedule()
    }

    override fun autonomousExit() {
        autonomousRoutine.cancel()
    }

}