package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase

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

            Perseverance.drive.autonomousInput = DriveTrain.DriveInput(
                0.0,
                0.0,
                -limelight.controller.calculate(distance, 0.0)
            )
        } else {
            // Turn CW until hasTarget.
            Perseverance.drive.autonomousInput = DriveTrain.DriveInput(0.0, 0.0, 0.5)
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.state = DriveTrain.State.Idle
    }

}