package ca.team5032.frc.utils

import edu.wpi.first.wpilibj2.command.SubsystemBase

enum class ControlState {
    MANUAL,
    AUTONOMOUS,
    IDLE
}

abstract class Subsystem<T>(defaultState: T) : SubsystemBase() {

    /**
     * Represents the current state of the subsystem, will be overridden during autonomous.
     * Commands should call a proxy function to modify this state, while periodic should control the component
     * based on this state, regardless of the control mode.
     *
     * State is applied to control the subsystem and does not necessarily represent the true state of the component.
     */
    private var state: T = defaultState

    /**
     * The control state represents how the subsystem is being controlled, either manually, automatically, or
     * the component is sitting idle.
     */
    private var controlState: ControlState = ControlState.IDLE

}