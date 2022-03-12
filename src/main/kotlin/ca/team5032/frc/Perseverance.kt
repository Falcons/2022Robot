package ca.team5032.frc

import ca.team5032.frc.led.LEDSystem
import ca.team5032.frc.subsystems.*
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.XboxController
import edu.wpi.first.wpilibj2.command.CommandBase
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
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(intake)
                }

                override fun initialize() {
                    intake.intake()
                }

                override fun cancel() {
                    intake.stop()
                }
            })
        JoystickButton(peripheralController, XboxController.Button.kB.value)
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(intake)
                }

                override fun initialize() {
                    intake.eject()
                }

                override fun cancel() {
                    intake.stop()
                }
            })

        // Register climb commands.
        JoystickButton(peripheralController, XboxController.Button.kY.value)
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(climb)
                }

                override fun initialize() {
                    climb.up()
                }

                override fun cancel() {
                    climb.stop()
                }
            })
        JoystickButton(peripheralController, XboxController.Button.kA.value)
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(climb)
                }

                override fun initialize() {
                    climb.down()
                }

                override fun cancel() {
                    climb.stop()
                }
            })

        // Register transfer commands.
        JoystickButton(peripheralController, XboxController.Button.kRightBumper.value)
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(transfer)
                }

                override fun initialize() {
                    transfer.up()
                }

                override fun cancel() {
                    transfer.stop()
                }
            })
        JoystickButton(peripheralController, XboxController.Button.kLeftBumper.value)
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(transfer)
                }

                override fun initialize() {
                    transfer.up()
                }

                override fun cancel() {
                    transfer.stop()
                }
            })

        JoystickButton(peripheralController, XboxController.Button.kBack.value)
            .whenHeld(object : CommandBase() {
                init {
                    addRequirements(shooter)
                }

                override fun initialize() {
                    shooter.shoot(Shooter.TARGET_RPM.value)
                }

                override fun cancel() {
                    shooter.stop()
                }
            })
    }

}