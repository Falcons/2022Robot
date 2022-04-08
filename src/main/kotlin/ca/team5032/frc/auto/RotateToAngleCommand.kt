package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import ca.team5032.frc.utils.MAXIMUM_ROTATION_SPEED
import ca.team5032.frc.utils.MINIMUM_ROTATION_SPEED
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.sign

class RotateToAngleCommand(private val desiredAngle: Double) : CommandBase() {

    var targetAngle = 0.0

    override fun initialize() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)
        targetAngle = abs((Perseverance.drive.getHeading() + desiredAngle) % 360)
        if (targetAngle < 0) {
            targetAngle += 360
        }
    }

    override fun execute() {
        val currentAngle = getDiffAngle()

        val output = Perseverance.drive.rotationController.calculate(currentAngle, 0.0)
        val absolute = abs(output)
        val sign = sign(output)

        val remapped = absolute * (MAXIMUM_ROTATION_SPEED - MINIMUM_ROTATION_SPEED) + MINIMUM_ROTATION_SPEED

        Perseverance.drive.autonomousInput.zRotation = sign * remapped

        if (Perseverance.drive.rotationController.atSetpoint()) this.cancel()
    }

    override fun isFinished(): Boolean {
        return abs(getDiffAngle()) < 3
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.autonomousInput.zRotation = 0.0
        Perseverance.drive.changeState(DriveTrain.State.Idle)
    }

    private fun getDiffAngle(): Double {
        val phi = abs(Perseverance.drive.getHeading() - targetAngle) % 360
        val distance = if (phi > 180) 360 - phi else phi

        return distance
    }

}