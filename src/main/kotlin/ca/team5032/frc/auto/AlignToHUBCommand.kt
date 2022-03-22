package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import edu.wpi.first.wpilibj2.command.CommandBase

class AlignToTargetCommand(private val targetPipeline: Limelight.Pipeline) : CommandBase() {

    private val limelight = Perseverance.limelight

    override fun initialize() {
        limelight.pipeline = targetPipeline
    }

    override fun execute() {
        if (limelight.hasTarget()) {
            // Turn towards target.
        } else {
            // Turn CW until hasTarget.
        }
    }

}