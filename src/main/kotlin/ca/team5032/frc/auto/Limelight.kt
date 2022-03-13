package ca.team5032.frc.auto

import ca.team5032.frc.utils.Subsystem
import edu.wpi.first.networktables.NetworkTableInstance
import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.drive.Vector2d

class Limelight: Subsystem<Limelight.State>(State.Idle) {

    enum class CameraMode(val value: Int) {
        Processing(0),
        Drive(1);

        companion object {
            fun from(value: Int) = values().firstOrNull { it.value == value } ?: Drive
        }
    }

    enum class Pipeline(val value: Int) {
        ReflectiveTape(0),
        RedBall(1),
        BlueBall(2);

        companion object {
            fun from(value: Int) = values().firstOrNull { it.value == value } ?: ReflectiveTape
        }
    }

    enum class LEDMode(val value: Int) {
        Current(0),
        Off(1),
        Blink(2),
        On(3);

        companion object {
            fun from(value: Int) = values().firstOrNull { it.value == value } ?: Off
        }
    }

    sealed class State {
        object OnTarget : State()
        object Targetting : State()
        object Idle : State()
    }

    data class LimelightTarget(
        val position: Vector2d,
        val areaPercentage: Double,
        val skew: Double,
        val shortestSide: Double,
        val longestSide: Double,
        val horizontalSide: Double,
        val verticalSide: Double
        )

    private val networkTable = NetworkTableInstance.getDefault().getTable("limelight")
    private val solenoid = DoubleSolenoid(PneumaticsModuleType.CTREPCM, 0, 1)

    val target: LimelightTarget
        get() = LimelightTarget(
            Vector2d(
                networkTable.getEntry("tx").getDouble(0.0),
                networkTable.getEntry("ty").getDouble(0.0)
            ),
            networkTable.getEntry("ta").getDouble(0.0),
            networkTable.getEntry("ts").getDouble(0.0),
            networkTable.getEntry("tshort").getDouble(0.0),
            networkTable.getEntry("tlong").getDouble(0.0),
            networkTable.getEntry("thor").getDouble(0.0),
            networkTable.getEntry("tvert").getDouble(0.0)
        )

    var pipeline: Pipeline
        get() = Pipeline.from(networkTable.getEntry("getpipe").getNumber(0).toInt())
        set(v) {
            networkTable.getEntry("pipeline").setNumber(v.value)
            if (v == Pipeline.ReflectiveTape) solenoid.set(DoubleSolenoid.Value.kReverse)
            else solenoid.set(DoubleSolenoid.Value.kForward)
        }

    var cameraMode: CameraMode
        get() = CameraMode.from(networkTable.getEntry("camMode").getNumber(0).toInt())
        set(v) { networkTable.getEntry("camMode").setNumber(v.value) }

    var ledMode: LEDMode
        get() = LEDMode.from(networkTable.getEntry("ledMode").getNumber(0).toInt())
        set(v) { networkTable.getEntry("ledMode").setNumber(v.value) }

    override fun periodic() {
        state.let {
            when (it) {
                is State.Targetting -> {
                    cameraMode = CameraMode.Processing
                    ledMode = LEDMode.On
                }
                is State.OnTarget -> ledMode = LEDMode.On
                is State.Idle -> drive()
            }
        }
    }

    fun hasTarget(): Boolean = networkTable.getEntry("tv").getNumber(0) == 1.0

    private fun drive() {
        ledMode = LEDMode.Off
        cameraMode = CameraMode.Drive
    }

}