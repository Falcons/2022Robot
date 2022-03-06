package ca.team5032.frc.utils

import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.shuffleboard.ShuffleboardTab

interface Tabbed {

    // The Shuffleboard tab for this tabbed item.
    val tab: ShuffleboardTab
        get() = Shuffleboard.getTab(this.javaClass.simpleName)

    /**
     * Creates the configuration widget from a set of properties.
     *
     * @param properties The list of properties to edit through the widget.
     */
    fun buildConfig(vararg properties: Property<*>) {
        tab.add("Config") { builder ->
            properties.asList().forEach { it.add(builder) }

            builder.setSmartDashboardType("RobotPreferences")
        }
    }

}