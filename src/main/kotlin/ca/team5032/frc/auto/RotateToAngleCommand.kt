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

        targetAngle = (Perseverance.drive.getHeading() + desiredAngle) % 360
        if (targetAngle < 0) {
            targetAngle += 360
        }

        println("Target $targetAngle")
        println("Curr Heading ${Perseverance.drive.getHeading()}")
        println("Diff Angle ${getDiffAngle()}")
    }

    override fun execute() {
        val output = Perseverance.drive.rotationController.calculate(getDiffAngle(), 0.0)
        val absolute = abs(output)
        val sign = sign(output)

        val remapped = absolute * (MAXIMUM_ROTATION_SPEED - MINIMUM_ROTATION_SPEED) + MINIMUM_ROTATION_SPEED

        Perseverance.drive.autonomousInput.zRotation = sign * remapped
    }

    override fun isFinished(): Boolean {
        println("Diff ${getDiffAngle()}, Phi ${Perseverance.drive.getHeading()}")

        return abs(getDiffAngle()) < 2.5
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.autonomousInput.zRotation = 0.0
        Perseverance.drive.changeState(DriveTrain.State.Idle)
    }

    private fun getDiffAngle(): Double {
        val phi = abs(Perseverance.drive.getHeading() - targetAngle) % 360
        val direction = sign(Perseverance.drive.getHeading() - targetAngle)

        return direction * (if (phi > 180) 360 - phi else phi)
    }

}