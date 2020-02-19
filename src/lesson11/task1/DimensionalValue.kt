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

    companion object {
        private fun fetchPrefix(k: String): String {
            var s = k
            var res = ""
            while (s !in dimensions) {
                res = s.first().toString()
                s = s.toMutableList().drop(1).joinToString(separator = "")
            }
            return if (res !in prefixes) ""
            else res
        }

        private val dimensions = getDimensions().joinToString(separator = "")
        private val prefixes = getPrefixes().joinToString(separator = "")

        private fun getDimensions(): MutableSet<String> {
            val dim = mutableSetOf<String>()
            for (dimension in Dimension.values()) {
                dim.add(dimension.abbreviation)
            }
            return dim
        }

        private fun getPrefixes(): MutableSet<String> {
            val dim = mutableSetOf<String>()
            for (dimension in DimensionPrefix.values()) {
                dim.add(dimension.abbreviation)
            }
            return dim
        }

        private fun fetchDimension(k: String): String {
            return if (fetchPrefix(k) == "") k.split(" ").last() else
                k.split(" ").last().toMutableList().drop(1).joinToString(separator = "")
        }

        private fun fetchDimensionName(k: String): Dimension {
            val a = fetchDimension(k)
            require(a in dimensions)
            for (dimension in Dimension.values()) {
                if (a == dimension.abbreviation)
                    return dimension
            }
            throw IllegalArgumentException()
        }

        private fun valueDeterminant(s: String): Double {
            val value = s.split(" ").first()
            require(value.matches(Regex("""-?\d+(\.\d+)?""")))
            return value.toDouble()
        }

        private fun dimensionDeterminant(s: String): String {
            val dimension = s.split(" ").last()
            require(fetchDimension(dimension) in dimensions)
            require(fetchPrefix(dimension) in prefixes)
            return dimension
        }

        private fun fetchMultiplier(k: String): Double {
            for (dimension in DimensionPrefix.values()) {
                if (k == dimension.abbreviation)
                    return dimension.multiplier
            }
            return 1.0
        }
    }


    /**
     * Величина с БАЗОВОЙ размерностью (например для 1.0Kg следует вернуть результат в граммах -- 1000.0)
     */
    val value: Double
        get() {
            return if (actualDimension in dimensions) actualValue
            else actualValue * fetchMultiplier(fetchPrefix(actualDimension))
        }

    /**
     * БАЗОВАЯ размерность (опять-таки для 1.0Kg следует вернуть GRAM)
     */
    val dimension: Dimension
        get() = fetchDimensionName(actualDimension)

    /**
     * Конструктор из строки. Формат строки: значение пробел размерность (1 Kg, 3 mm, 100 g и так далее).
     */
    constructor(s: String) : this(valueDeterminant(s), dimensionDeterminant(s))


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
    operator fun minus(other: DimensionalValue): DimensionalValue {
        require(dimension == other.dimension)
        return this + other.unaryMinus()
    }

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
    NANO("n", 0.000000001),
    MEGA("M", 1000000.0);
}