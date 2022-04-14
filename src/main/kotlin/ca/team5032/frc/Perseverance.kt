package ca.team5032.frc

import ca.team5032.frc.auto.*
import ca.team5032.frc.led.LEDSystem
import ca.team5032.frc.subsystems.*
import edu.wpi.first.cameraserver.CameraServer
import edu.wpi.first.cscore.VideoSource
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
        DriveForwardCommand(.65),
        WaitUntilCommand { intake.hasBall() },
        InstantCommand({ intake.stop() }),
        RotateToAngleCommand(180.0),
        ShootAmountCommand(2),
        InstantCommand({
            limelight.changeState(Limelight.State.Targeting(
                Limelight.Pipeline.getBall()
            ))
        }),
        RotateToAngleCommand(-77.0),
        AlignToTargetCommand(
            Limelight.Pipeline.getBall(),
            1,
            false
        ),
        InstantCommand({
            limelight.changeState(Limelight.State.Targeting(
                Limelight.Pipeline.ReflectiveTape
            ))
        }),
        InstantCommand({
            intake.intake()
            transfer.up()
        }),
        DriveForwardCommand(3.0),
        WaitUntilCommand { transfer.hasBall() },
        InstantCommand({
            intake.stop()
            transfer.stop()
        }),
        RotateToAngleCommand(120.0),
        ShootAmountCommand(1),
        InstantCommand({
            limelight.changeState(Limelight.State.Targeting(
                Limelight.Pipeline.getBall()
            ))
        }),
        RotateToAngleCommand(180.0),
        AlignToTargetCommand(
            Limelight.Pipeline.getBall(),
            1,
            false
        ),
        InstantCommand({
            limelight.changeState(Limelight.State.Targeting(Limelight.Pipeline.ReflectiveTape))
        }),
        InstantCommand({
            intake.intake()
            transfer.up()
        }),
        DriveForwardCommand(2.07),
        WaitUntilCommand {
            transfer.hasBall() && intake.hasBall()
        },
        InstantCommand({
            intake.stop()
            transfer.stop()
        }),
        RotateToAngleCommand(180.0),
        DriveForwardCommand(1.70, 1.0),
        AlignToTargetCommand(Limelight.Pipeline.ReflectiveTape, -1),
        ShootCommand(2)
    )

    override fun robotInit() {
        LiveWindow.disableAllTelemetry()
        //DriverStation.silenceJoystickConnectionWarning(true)
        registerCommands()

        val camera = CameraServer.startAutomaticCapture("Driver cam", 0);

         // Default configuration for the camera. 60fps 320p keepOpen.
         camera.setConnectionStrategy(VideoSource.ConnectionStrategy.kKeepOpen)
         camera.setFPS(60)
         camera.setResolution(320, 240)
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
            .whenActive(climb::up, climb)
            .whenInactive(climb::stop, climb)

        JoystickButton(peripheralController, XboxController.Button.kA.value)
            .whenActive(climb::down, climb)
            .whenInactive(climb::stop, climb)

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
        drive.gyro.yaw = 0.0
    }

    override fun autonomousExit() {
        autonomousRoutine.cancel()
    }

}