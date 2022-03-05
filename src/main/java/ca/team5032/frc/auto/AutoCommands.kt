package ca.team5032.frc.auto

import ca.team5032.frc.Perseverance
import ca.team5032.frc.drive.DriveTrain
import edu.wpi.first.wpilibj2.command.CommandBase
import java.util.*

class DriveBackwardsCommand : CommandBase() {

    private val MAX_TIME = 1 * 1000

    private var startingTime: Long = 0

    override fun initialize() {
        startingTime = Date().time
    }

    override fun execute() {
        if (Date().time - startingTime > MAX_TIME) {
            this.cancel()
            return
        }
        Perseverance.drive.auto(DriveTrain.AutoSpeeds(-0.4, 0.0, 0.0))
    }

}