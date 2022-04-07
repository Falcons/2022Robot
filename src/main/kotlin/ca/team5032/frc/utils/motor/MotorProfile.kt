package ca.team5032.frc.utils.motor

import com.ctre.phoenix.motorcontrol.NeutralMode
import com.ctre.phoenix.sensors.SensorInitializationStrategy
import edu.wpi.first.wpilibj.motorcontrol.MotorController

sealed class MotorProfile<T : MotorController> {

    abstract fun apply(motor: T)

    fun apply(vararg motor: T) {
        motor.iterator().forEachRemaining(::apply)
    }

    object DriveConfig : MotorProfile<Falcon500>() {
        override fun apply(motor: Falcon500) {
            motor.setNeutralMode(NeutralMode.Brake)
            motor.configIntegratedSensorInitializationStrategy(SensorInitializationStrategy.BootToZero)

            motor.sensorCollection.setIntegratedSensorPosition(0.0, 0)
        }
    }

}