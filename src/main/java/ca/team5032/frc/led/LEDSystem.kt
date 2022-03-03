package ca.team5032.frc.led

import ca.team5032.frc.Perseverance
import ca.team5032.frc.drive.DriveTrain
import edu.wpi.first.wpilibj.motorcontrol.Spark
import edu.wpi.first.wpilibj2.command.SubsystemBase

class LEDSystem : SubsystemBase() {

    companion object {

        const val BLINKIN_ID = 9

    }

    val blinkin: Spark = Spark(9)

    override fun periodic() {
        if (Perseverance.drive.state == DriveTrain.State.DRIVING) {
            blinkin.set(.91)
        } else {
            blinkin.set(.69)
        }
    }

}