package ca.team5032.frc.led

import ca.team5032.frc.utils.BLINKIN_ID
import ca.team5032.frc.utils.Tabbed
import edu.wpi.first.wpilibj.motorcontrol.Spark
import edu.wpi.first.wpilibj2.command.SubsystemBase

class LEDSystem : SubsystemBase(), Tabbed {

    private val ledController = Spark(BLINKIN_ID)

    private val queue = mutableListOf<Pair<Color, Double>>()
    private var elapsedInstructionTime = 0.0

    override fun periodic() {
        if (queue.isEmpty()) {
            // Default behaviour
            ledController.set(0.0)

            return
        }

        ledController.set(queue.first().first.value)
        elapsedInstructionTime ++

        if (isInstructionDone()) {
            queue.removeFirst()
            elapsedInstructionTime = 0.0
        }
    }

    fun play(sequence: ColorSequence) {
        queue.addAll(sequence)
    }

    private fun isInstructionDone(): Boolean {
        return elapsedInstructionTime > (queue.first().second * 50)
    }

}