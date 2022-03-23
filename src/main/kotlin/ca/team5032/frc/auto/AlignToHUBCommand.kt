package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import edu.wpi.first.wpilibj.GenericHID
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.sign

class AlignToTargetCommand(private val targetPipeline: Limelight.Pipeline) : CommandBase() {

    private val limelight = Perseverance.limelight

    override fun initialize() {
        limelight.pipeline = targetPipeline
        Perseverance.drive.state = DriveTrain.State.Autonomous
        limelight.state = Limelight.State.Targeting
    }

    override fun execute() {
        if (limelight.hasTarget()) {
            // Turn towards target.
            val distance = limelight.target.offset.x
            //val direction = sign(limelight.target.offset.x)

            val output = limelight.controller.calculate(distance, 0.0)
            val absolute = abs(output)
            val sign = sign(output)
            val remapped = absolute * (1 - 0.26) + 0.26

            Perseverance.drive.autonomousInput = DriveTrain.DriveInput(
                0.0,
                0.0,
                -(sign * remapped)
            )

            if (limelight.controller.atSetpoint()) {
                Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kRightRumble, 1.0)
                Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kLeftRumble, 1.0)
            } else {
                Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kRightRumble, 0.0)
                Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0)
            }
            //if (limelight.controller.atSetpoint()) this.cancel()
        } else {
            // Turn CW until hasTarget.
            Perseverance.drive.autonomousInput = DriveTrain.DriveInput(0.0, 0.0, 0.5)
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.state = DriveTrain.State.Idle
        limelight.state = Limelight.State.Idle

        limelight.pipeline = Limelight.Pipeline.BlueBall

        Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kRightRumble, 0.0)
        Perseverance.peripheralController.setRumble(GenericHID.RumbleType.kLeftRumble, 0.0)
    }

}