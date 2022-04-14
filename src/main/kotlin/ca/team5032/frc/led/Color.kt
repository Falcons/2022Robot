package ca.team5032.frc.led

sealed class Color(val value: Double) {

    object HotPink      : Color(0.57)
    object DarkRed      : Color(0.59)
    object Red          : Color(0.61)
    object RedOrange    : Color(0.63)
    object Orange       : Color(0.95)
    object Gold         : Color(0.67)
    object Yellow       : Color(0.69)
    object LawnGreen    : Color(0.71)
    object Lime         : Color(0.73)
    object DarkGreen    : Color(0.75)
    object Green        : Color(0.77)
    object BlueGreen    : Color(0.79)
    object Aqua         : Color(0.81)
    object SkyBlue      : Color(0.83)
    object DarkBlue     : Color(0.85)
    object Blue         : Color(0.87)
    object BlueViolet   : Color(0.89)
    object Violet       : Color(0.91)
    object White        : Color(0.93)
    object Gray         : Color(0.95)
    object DarkGray     : Color(0.97)
    object Black        : Color(0.99)

}

typealias ColorSequence = List<Pair<Color, Double>>