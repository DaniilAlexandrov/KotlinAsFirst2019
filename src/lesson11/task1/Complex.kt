@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

import kotlin.math.pow

/**
 * Класс "комплексое число".
 *
 * Общая сложность задания -- лёгкая.
 * Объект класса -- комплексное число вида x+yi.
 * Про принципы работы с комплексными числами см. статью Википедии "Комплексное число".
 *
 * Аргументы конструктора -- вещественная и мнимая часть числа.
 */
class Complex(val re: Double, val im: Double) {

    /**
     * Конструктор из вещественного числа
     */
    constructor(x: Double) : this(x, 0.0)

    /**
     * Конструктор из строки вида x+yi
     */
    constructor(s: String) : this(
        Regex("""-?\d+(\.\d+)*""").findAll(s).elementAt(0).value.toDouble(),
        Regex("""-?\d+(\.\d+)*""").findAll(s).elementAt(1).value.toDouble()
    )

    /**
     * Сложение.
     */
    operator fun plus(other: Complex): Complex = Complex(re + other.re, im + other.im)

    /**
     * Смена знака (у обеих частей числа)
     */
    operator fun unaryMinus(): Complex = Complex(-re, -im)

    /**
     * Вычитание
     */
    operator fun minus(other: Complex): Complex = this + other.unaryMinus()

    /**
     * Умножение
     */
    operator fun times(other: Complex): Complex = Complex(
        re * other.re - im * other.im,
        re * other.im + im * other.re
    )

    /**
     * Деление
     */
    operator fun div(other: Complex): Complex = Complex(
        (re * other.re + im * other.im) / (other.re.pow(2) + other.im.pow(2)),
        (im * other.re - re * other.im) / (other.re.pow(2) + other.im.pow(2))
    )

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean = other is Complex && re == other.re && im == other.im

    /**
     * Преобразование в строку
     */
    override fun toString(): String {
        var sign = "+"
        if (im == 0.0) return "$re" else
            if (im < 0) sign = ""
        return "$re$sign${im}i"
    }

    override fun hashCode(): Int {
        var result = re.hashCode()
        result = 31 * result + im.hashCode()
        return result
    }
}