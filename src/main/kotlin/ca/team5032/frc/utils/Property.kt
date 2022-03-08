package ca.team5032.frc.utils

import edu.wpi.first.util.sendable.SendableBuilder

sealed class Property<T>(val name: String, var value: T) {

    abstract fun add(builder: SendableBuilder)

}

class DoubleProperty(name: String, defaultValue: Double) : Property<Double>(name, defaultValue) {

    override fun add(builder: SendableBuilder) {
        builder.addDoubleProperty(name, { value }) { v -> value = v }
    }

}

class BooleanProperty(name: String, defaultValue: Boolean) : Property<Boolean>(name, defaultValue) {

    override fun add(builder: SendableBuilder) {
        builder.addBooleanProperty(name, { value }) { v -> value = v}
    }

}