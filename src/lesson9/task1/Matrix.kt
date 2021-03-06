@file:Suppress("UNUSED_PARAMETER", "unused")

package lesson9.task1

import java.lang.IllegalArgumentException

/**
 * Ячейка матрицы: row = ряд, column = колонка
 */
data class Cell(val row: Int, val column: Int)

/**
 * Интерфейс, описывающий возможности матрицы. E = тип элемента матрицы
 */
interface Matrix<E> {
    /** Высота */
    val height: Int

    /** Ширина */
    val width: Int

    /**
     * Доступ к ячейке.
     * Методы могут бросить исключение, если ячейка не существует или пуста
     */
    operator fun get(row: Int, column: Int): E

    operator fun get(cell: Cell): E

    /**
     * Запись в ячейку.
     * Методы могут бросить исключение, если ячейка не существует
     */
    operator fun set(row: Int, column: Int, value: E)

    operator fun set(cell: Cell, value: E)
}

/**
 * Простая
 *
 * Метод для создания матрицы, должен вернуть РЕАЛИЗАЦИЮ Matrix<E>.
 * height = высота, width = ширина, e = чем заполнить элементы.
 * Бросить исключение IllegalArgumentException, если height или width <= 0.
 */
fun <E> createMatrix(height: Int, width: Int, e: E): Matrix<E> {
    require(height > 0 && width > 0)
    return MatrixImpl(height, width, e)
}

/**
 * Средняя сложность
 *
 * Реализация интерфейса "матрица"
 */
class MatrixImpl<E>(override val height: Int, override val width: Int, e: E) : Matrix<E> {

    private val matrixSkeleton = MutableList(height) { MutableList(width) { e } }

    private fun isEligible(row: Int, column: Int) = column in 0 until width && row in 0 until height

    override fun get(row: Int, column: Int): E = if (!isEligible(row, column))
        throw IllegalArgumentException()
    else matrixSkeleton[row][column]

    override fun get(cell: Cell): E = get(cell.row, cell.column)

    override fun set(row: Int, column: Int, value: E) {
        require(isEligible(row, column))
        matrixSkeleton[row][column] = value
    }

    override fun set(cell: Cell, value: E) {
        set(cell.row, cell.column, value)
    }

    override fun equals(other: Any?) =
        other is MatrixImpl<*> && height == other.height && width == other.width &&
                matrixSkeleton == other.matrixSkeleton


    override fun toString(): String {
        val builder = StringBuilder()
        builder.append("[")
        for (row in 0 until height) {
            builder.append("[")
            for (column in 0 until width) {
                builder.append(this[row, column])
                if (row != width && column != height) builder.append(", ")
            }
            builder.append("]")
        }
        builder.append("]")
        return "$builder"
    }

    override fun hashCode(): Int {
        var result = height
        result = 31 * result + width
        result = 31 * result + matrixSkeleton.hashCode()
        return result
    }
}

