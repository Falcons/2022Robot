package ca.team5032.frc.utils

import edu.wpi.first.wpilibj2.command.SubsystemBase
import java.util.logging.Logger

enum class ControlState {
    MANUAL,
    AUTONOMOUS,
    IDLE
}

abstract class Subsystem<T : Any>(private val subsystemName: String, defaultState: T) : SubsystemBase() {

    private val logger: Logger = Logger.getLogger("Subsystem.${subsystemName}")

    /**
     * Represents the current state of the subsystem, will be overridden during autonomous.
     * Commands should call a proxy function to modify this state, while periodic should control the component
     * based on this state, regardless of the control mode.
     *
     * State is applied to control the subsystem and does not necessarily represent the true state of the component.
     */
    var state: T = defaultState

    /**
     * The control state represents how the subsystem is being controlled, either manually, automatically, or
     * the component is sitting idle.
     */
    // TODO: Use this, reformat subsystems to define inputs, consider embracing linear systems ideology a little?
    private var controlState: ControlState = ControlState.IDLE

    open fun onStateChange(oldState: T, newState: T) {}

    fun changeState(newState: T) {
        onStateChange(state, newState)

        // logger.info("[${subsystemName}] Changing state, from: ${state.javaClass.simpleName} to: ${newState.javaClass.simpleName}\n")
        state = newState
    }

}