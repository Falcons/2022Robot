package ca.team5032.frc.utils

import edu.wpi.first.wpilibj2.command.SubsystemBase
import java.util.function.Consumer

enum class ControlState {
    MANUAL,
    AUTONOMOUS,
    IDLE
}

data class StateTransition<T : Any>(val from: T, val to: T, val subsystem: Subsystem<T>)

abstract class Subsystem<T : Any>(val subsystemName: String, defaultState: T) : SubsystemBase() {

    private val transitionTasks: MutableList<Consumer<StateTransition<T>>> = mutableListOf()

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
    private var controlState: ControlState = ControlState.IDLE

    protected fun setState(newState: T): Set<Subsystem<T>> {
        //if (newState == state) return setOf(this)

        //val transition = StateTransition(state, newState, this)
        //transitionTasks.forEach { it.accept(transition) }
        //transitionTasks.clear()

        //print("[${subsystemName}] Changing state from ${state.javaClass.simpleName} to ${newState.javaClass.simpleName}\n")
        state = newState

        // Returns the set for chaining setting states with commands.
        return setOf(this)
    }

}