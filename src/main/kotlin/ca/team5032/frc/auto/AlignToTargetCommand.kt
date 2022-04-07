package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.Perseverance.limelight
import ca.team5032.frc.subsystems.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.sign

class AlignToTargetCommand(private val targetPipeline: Limelight.Pipeline, private val mult: Int) : CommandBase() {

    private var ticksAtSetpoint = 0

    // Must be at the setpoint for 0.1 seconds.
    private var tickThreshold = 0.1 / Perseverance.period

    private val minimumRotationSpeed = 0.26

    override fun initialize() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)

        limelight.changeState(Limelight.State.Targeting(targetPipeline))

        ticksAtSetpoint = 0
    }

    override fun execute() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)
        limelight.changeState(Limelight.State.Targeting(targetPipeline))

        if (limelight.hasTarget()) {
            // Turn towards target.
            val distance = limelight.target.offset.x

            val output = limelight.controller.calculate(distance, 0.0)
            val absolute = abs(output)
            val sign = -sign(output)

            val remapped = absolute * (1 - minimumRotationSpeed) + minimumRotationSpeed

            Perseverance.drive.autonomousInput.zRotation = sign * remapped

            // TODO: Remove, keep aligning when going to shoot?
            if (limelight.controller.atSetpoint()) {
                ticksAtSetpoint++
            } else {
                ticksAtSetpoint = 0
            }
        } else {
            // Turn until hasTarget.
            Perseverance.drive.autonomousInput.zRotation = 0.5 * mult

            ticksAtSetpoint = 0
        }
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.autonomousInput.zRotation = 0.0

        Perseverance.drive.changeState(DriveTrain.State.Idle)
        limelight.changeState(Limelight.State.Idle)
    }

    override fun isFinished(): Boolean {
        return ticksAtSetpoint >= tickThreshold
    }

}