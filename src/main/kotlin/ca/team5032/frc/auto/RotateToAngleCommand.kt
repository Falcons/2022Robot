package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.subsystems.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase
import kotlin.math.abs
import kotlin.math.sign

class RotateToAngleCommand(private val angle: Double) : CommandBase() {

    private val minimumRotationSpeed = 0.26

    override fun initialize() {
        Perseverance.drive.changeState(DriveTrain.State.Autonomous)
        Perseverance.drive.gyro.reset()
    }

    override fun execute() {
        val currentAngle = Perseverance.drive.gyro.angle

        val desiredAngle = angle

        val output = Perseverance.drive.rotationController.calculate(currentAngle, desiredAngle)
        val absolute = abs(output)
        val sign = -sign(output)

        val remapped = absolute * (0.7 - minimumRotationSpeed) + minimumRotationSpeed

        Perseverance.drive.autonomousInput.zRotation = sign * remapped

        Perseverance.drive.autonomousInput
            .zRotation = remapped

        if (Perseverance.drive.rotationController.atSetpoint()) this.cancel()
    }

    override fun isFinished(): Boolean {
        return abs(Perseverance.drive.gyro.angle - angle) < 10
    }

    override fun end(interrupted: Boolean) {
        Perseverance.drive.changeState(DriveTrain.State.Idle)
    }

}