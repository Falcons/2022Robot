package ca.team5032.frc.led

import ca.team5032.frc.utils.BLINKIN_ID
import ca.team5032.frc.utils.Tabbed
import edu.wpi.first.wpilibj.motorcontrol.Spark
import edu.wpi.first.wpilibj2.command.SubsystemBase

class LEDSystem : SubsystemBase(), Tabbed {

    private val ledController = Spark(BLINKIN_ID)

    private val queue = mutableListOf<Pair<Color, Double>>()
    private var elapsedInstructionTime = 0

    private var looping = false

    override fun periodic() {
        if (queue.isEmpty()) {
            // Default behaviour
            ledController.set(Color.Gold.value)

            return
        }

        ledController.set(queue.first().first.value)
        elapsedInstructionTime ++

        if (isInstructionDone()) {
            if (looping) {
                queue.add(queue.removeFirst())
            } else {
                queue.removeFirst()
            }

            elapsedInstructionTime = 0
        }
    }

    fun play(sequence: ColorSequence) {
        queue.addAll(sequence)
    }

    fun loop() {
        looping = !looping
    }

    fun stop() {
        queue.clear()
    }

    private fun isInstructionDone(): Boolean {
        return elapsedInstructionTime > (queue.first().second * 50)
    }

}