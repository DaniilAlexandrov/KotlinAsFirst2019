@file:Suppress("UNUSED_PARAMETER")

package lesson11.task1

/**
 * Класс "Величина с размерностью".
 *
 * Предназначен для представления величин вроде "6 метров" или "3 килограмма"
 * Общая сложность задания - средняя.
 * Величины с размерностью можно складывать, вычитать, делить, менять им знак.
 * Их также можно умножать и делить на число.
 *
 * В конструктор передаётся вещественное значение и строковая размерность.
 * Строковая размерность может:
 * - либо строго соответствовать одной из abbreviation класса Dimension (m, g)
 * - либо соответствовать одной из приставок, к которой приписана сама размерность (Km, Kg, mm, mg)
 * - во всех остальных случаях следует бросить IllegalArgumentException
 */


class DimensionalValue(private val actualValue: Double, private val actualDimension: String) :
    Comparable<DimensionalValue> {
    private fun fetchPrefix(k: String): String {
        var s = k
        var res = 'a'
        while (s !in getDimensions()) {
            res = s.first()
            s = s.toMutableList().drop(1).joinToString(separator = "")
        }
        return res.toString()
    }

    private fun fetchDimension(k: String): String =
        k.toMutableList().minus(fetchPrefix(k).toMutableList()).joinToString(separator = "")

    companion object {
        private fun getPrefixes(): MutableSet<String> {
            val dim = mutableSetOf<String>()
            for (dimension in DimensionPrefix.values()) {
                dim.add(dimension.abbreviation)
            }
            return dim
        }

        private fun getDimensions(): MutableSet<String> {
            val dim = mutableSetOf<String>()
            for (dimension in Dimension.values()) {
                dim.add(dimension.abbreviation)
            }
            return dim
        }

        fun valueDeterminant(s: String) =
            Regex("""\d+(\.\d+)?""").find(s)?.value?.toDouble() ?: throw IllegalArgumentException()

        fun prefixDeterminant(s: String) =
            Regex(
                """[${getPrefixes().joinToString(separator = "")}}]?[${getDimensions().joinToString(separator = "")}]"""
            ).find(s)?.value ?: throw IllegalArgumentException()
    }


    private fun fetchMultiplier(k: String): Double {
        for (dimension in DimensionPrefix.values()) {
            if (k == dimension.abbreviation)
                return dimension.multiplier
        }
        return 0.0
    }

    /**
     * Величина с БАЗОВОЙ размерностью (например для 1.0Kg следует вернуть результат в граммах -- 1000.0)
     */
    val value: Double
        get() {
            return if (actualDimension in getDimensions()) actualValue
            else actualValue * fetchMultiplier(fetchPrefix(actualDimension))
        }

    /**
     * БАЗОВАЯ размерность (опять-таки для 1.0Kg следует вернуть GRAM)
     */
    val dimension: Dimension
        get() {
            return when (fetchDimension(actualDimension)) {
                "g" -> Dimension.GRAM
                "Hz" -> Dimension.HERTZ
                else -> Dimension.METER // Если есть какой-то способ делать это более автоматически, можно, пожалуйста, намек?
            }
        }

    /**
     * Конструктор из строки. Формат строки: значение пробел размерность (1 Kg, 3 mm, 100 g и так далее).
     */
    constructor(s: String) : this(valueDeterminant(s), prefixDeterminant(s))

    /**
     * Сложение с другой величиной. Если базовая размерность разная, бросить IllegalArgumentException
     * (нельзя складывать метры и килограммы)
     */
    operator fun plus(other: DimensionalValue): DimensionalValue {
        require(dimension == other.dimension)
        return DimensionalValue(value + other.value, dimension.abbreviation)
    }

    /**
     * Смена знака величины
     */
    operator fun unaryMinus(): DimensionalValue = DimensionalValue(-value, dimension.abbreviation)

    /**
     * Вычитание другой величины. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun minus(other: DimensionalValue): DimensionalValue = this + other.unaryMinus()

    /**
     * Умножение на число
     */
    operator fun times(other: Double): DimensionalValue = DimensionalValue(value * other, dimension.abbreviation)

    /**
     * Деление на число
     */
    operator fun div(other: Double): DimensionalValue {
        require(other != 0.0)
        return DimensionalValue(value / other, dimension.abbreviation)
    }

    /**
     * Деление на другую величину. Если базовая размерность разная, бросить IllegalArgumentException
     */
    operator fun div(other: DimensionalValue): Double {
        require(dimension == other.dimension)
        return value / other.value
    }

    /**
     * Сравнение на равенство
     */
    override fun equals(other: Any?): Boolean =
        other is DimensionalValue && dimension == other.dimension && value == other.value

    /**
     * Сравнение на больше/меньше. Если базовая размерность разная, бросить IllegalArgumentException
     */
    override fun compareTo(other: DimensionalValue): Int {
        require(dimension == other.dimension)
        return when {
            value > other.value -> 1
            value < other.value -> -1
            else -> 0
        }
    }

    override fun hashCode(): Int = javaClass.hashCode()
}

/**
 * Размерность. В этот класс можно добавлять новые варианты (секунды, амперы, прочие), но нельзя убирать
 */
enum class Dimension(val abbreviation: String) {
    METER("m"),
    GRAM("g"),
    HERTZ("Hz");
}

/**
 * Приставка размерности. Опять-таки можно добавить новые варианты (деци-, санти-, мега-, ...), но нельзя убирать
 */
enum class DimensionPrefix(val abbreviation: String, val multiplier: Double) {
    KILO("K", 1000.0),
    MILLI("m", 0.001),
    MEGA("M", 1000000.0);
}