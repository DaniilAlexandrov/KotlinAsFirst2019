@file:Suppress("UNUSED_PARAMETER")

package lesson8.task2

import lesson8.task3.Graph
import kotlin.math.abs
import kotlin.math.max

/**
 * Клетка шахматной доски. Шахматная доска квадратная и имеет 8 х 8 клеток.
 * Поэтому, обе координаты клетки (горизонталь row, вертикаль column) могут находиться в пределах от 1 до 8.
 * Горизонтали нумеруются снизу вверх, вертикали слева направо.
 */
data class Square(val column: Int, val row: Int) {
    /**
     * Пример
     *
     * Возвращает true, если клетка находится в пределах доски
     */
    fun inside(): Boolean = column in 1..8 && row in 1..8

    /**
     * Простая
     *
     * Возвращает строковую нотацию для клетки.
     * В нотации, колонки обозначаются латинскими буквами от a до h, а ряды -- цифрами от 1 до 8.
     * Для клетки не в пределах доски вернуть пустую строку
     */
    fun notation(): String = if (inside()) {
        ('a' + column - 1).toString() + row
    } else ""

}

/**
 * Простая
 *
 * Создаёт клетку по строковой нотации.
 * В нотации, колонки обозначаются латинскими буквами от a до h, а ряды -- цифрами от 1 до 8.
 * Если нотация некорректна, бросить IllegalArgumentException
 */
fun square(notation: String): Square {
    require(Regex("""[a-h][1-8]""").matches(notation))
    return Square(notation[0] - 'a' + 1, notation[1].toString().toInt())
}

/**
 * Простая
 *
 * Определить число ходов, за которое шахматная ладья пройдёт из клетки start в клетку end.
 * Шахматная ладья может за один ход переместиться на любую другую клетку
 * по вертикали или горизонтали.
 * Ниже точками выделены возможные ходы ладьи, а крестиками -- невозможные:
 *
 * xx.xxххх
 * xх.хxххх
 * ..Л.....
 * xх.хxххх
 * xx.xxххх
 * xx.xxххх
 * xx.xxххх
 * xx.xxххх
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: rookMoveNumber(Square(3, 1), Square(6, 3)) = 2
 * Ладья может пройти через клетку (3, 3) или через клетку (6, 1) к клетке (6, 3).
 */
fun rookMoveNumber(start: Square, end: Square): Int = rookTrajectory(start, end).size - 1

/**
 * Средняя
 *
 * Вернуть список из клеток, по которым шахматная ладья может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов ладьи см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры: rookTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          (здесь возможен ещё один вариант)
 *          rookTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(3, 3), Square(6, 3))
 *          (здесь возможен единственный вариант)
 *          rookTrajectory(Square(3, 5), Square(8, 5)) = listOf(Square(3, 5), Square(8, 5))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun rookTrajectory(start: Square, end: Square): List<Square> {
    require((start.inside()) && (end.inside()))
    val route = mutableListOf(start)
    if (end == start) return route
    if ((start.row != end.row) && (start.column != end.column))
        route.add(Square(end.column, start.row))
    route.add(end)
    return route
}

/**
 * Простая
 *
 * Определить число ходов, за которое шахматный слон пройдёт из клетки start в клетку end.
 * Шахматный слон может за один ход переместиться на любую другую клетку по диагонали.
 * Ниже точками выделены возможные ходы слона, а крестиками -- невозможные:
 *
 * .xxx.ххх
 * x.x.xххх
 * xxСxxxxx
 * x.x.xххх
 * .xxx.ххх
 * xxxxx.хх
 * xxxxxх.х
 * xxxxxхх.
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если клетка end недостижима для слона, вернуть -1.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Примеры: bishopMoveNumber(Square(3, 1), Square(6, 3)) = -1; bishopMoveNumber(Square(3, 1), Square(3, 7)) = 2.
 * Слон может пройти через клетку (6, 4) к клетке (3, 7).
 */
fun bishopMoveNumber(start: Square, end: Square): Int {
    require((start.inside()) && (end.inside()))
    return when {
        start == end -> 0
        ((end.row - end.column) == (start.row - start.column)) ||
                ((end.row + end.column) == (start.row + start.column)) -> 1
        (end.row + end.column) % 2 != (start.row + start.column) % 2 -> -1
        else -> 2
    }
}

/**
 * Сложная
 *
 * Вернуть список из клеток, по которым шахматный слон может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов слона см. предыдущую задачу.
 *
 * Если клетка end недостижима для слона, вернуть пустой список.
 *
 * Если клетка достижима:
 * - список всегда включает в себя клетку start
 * - клетка end включается, если она не совпадает со start.
 * - между ними должны находиться промежуточные клетки, по порядку от start до end.
 *
 * Примеры: bishopTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          bishopTrajectory(Square(3, 1), Square(3, 7)) = listOf(Square(3, 1), Square(6, 4), Square(3, 7))
 *          bishopTrajectory(Square(1, 3), Square(6, 8)) = listOf(Square(1, 3), Square(6, 8))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun proxyFinder(start: Square, end: Square): Square {
    val rowDiff = end.row - start.row
    val colDiff = end.column - start.column
    val proxyDistance = (rowDiff - colDiff) / 2
    val downwards = (Square(start.column - proxyDistance, start.row + proxyDistance))
    val upwards = (Square(end.column + proxyDistance, end.row - proxyDistance))
    /*val oddnessDeterminant = (start.column + start.row) % 2
    var res = Square(0, 0)
    for (proxyCol in 1..8)
        for (proxyRow in 1..8) {
            if ((Square(proxyCol, proxyRow) == start) || Square(proxyCol, proxyRow) == end) continue
            else if ((proxyCol + proxyRow) % 2 == oddnessDeterminant)
                if (sqr(start.column - proxyCol) + sqr(end.column - proxyCol) +
                    sqr(start.row - proxyRow) + sqr(end.row - proxyRow) == sqr(colDiff) + sqr(rowDiff)
                )
                    res = Square(proxyCol, proxyRow)
        }
    return res */

    return if (start.column - proxyDistance !in 1..8 || start.row + proxyDistance !in 1..8) upwards
    else downwards
}

fun bishopTrajectory(start: Square, end: Square): List<Square> {
    val route = mutableListOf(start)
    return when (bishopMoveNumber(start, end)) {
        -1 -> emptyList()
        0 -> route
        1 -> {
            route.add(end)
            route
        }
        else -> {
            route.add(proxyFinder(start, end))
            route.add(end)
            route
        }
    }
}

/**
 * Средняя
 *
 * Определить число ходов, за которое шахматный король пройдёт из клетки start в клетку end.
 * Шахматный король одним ходом может переместиться из клетки, в которой стоит,
 * на любую соседнюю по вертикали, горизонтали или диагонали.
 * Ниже точками выделены возможные ходы короля, а крестиками -- невозможные:
 *
 * xxxxx
 * x...x
 * x.K.x
 * x...x
 * xxxxx
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: kingMoveNumber(Square(3, 1), Square(6, 3)) = 3.
 * Король может последовательно пройти через клетки (4, 2) и (5, 2) к клетке (6, 3).
 */
fun kingMoveNumber(start: Square, end: Square): Int {
    require(start.inside() && end.inside())
    // Растояние Чебышёва.
    return max(abs(end.column - start.column), abs(end.row - start.row))
}

/**
 * Сложная
 *
 * Вернуть список из клеток, по которым шахматный король может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов короля см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры: kingTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 *          (здесь возможны другие варианты)
 *          kingTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(4, 2), Square(5, 2), Square(6, 3))
 *          (здесь возможен единственный вариант)
 *          kingTrajectory(Square(3, 5), Square(6, 2)) = listOf(Square(3, 5), Square(4, 4), Square(5, 3), Square(6, 2))
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
fun kingTrajectory(start: Square, end: Square): List<Square> {
    require(start.inside() && end.inside())
    val route = mutableListOf(start)
    if (start == end) return route
    var currentSquare = route.last()
    while (currentSquare != end) {
        when {
            (currentSquare.column == end.column) -> if (currentSquare.column > end.column)
                route.add(Square(currentSquare.column, currentSquare.row - 1))
            else route.add(Square(currentSquare.column, currentSquare.row + 1))
            (currentSquare.row == end.row) -> if (currentSquare.row > end.row)
                route.add(Square(currentSquare.column - 1, currentSquare.row))
            else route.add(Square(currentSquare.column + 1, currentSquare.row))
            (currentSquare.row > end.row && currentSquare.column > end.column) ->
                route.add(Square(currentSquare.column - 1, currentSquare.row - 1))
            (currentSquare.row > end.row && currentSquare.column < end.column) ->
                route.add(Square(currentSquare.column + 1, currentSquare.row - 1))
            (currentSquare.row < end.row && currentSquare.column > end.column) ->
                route.add(Square(currentSquare.column - 1, currentSquare.row + 1))
            (currentSquare.row < end.row && currentSquare.column < end.column) ->
                route.add(Square(currentSquare.column + 1, currentSquare.row + 1))
        }
        currentSquare = route.last()
    }
    return route
}

/**
 * Сложная
 *
 * Определить число ходов, за которое шахматный конь пройдёт из клетки start в клетку end.
 * Шахматный конь одним ходом вначале передвигается ровно на 2 клетки по горизонтали или вертикали,
 * а затем ещё на 1 клетку под прямым углом, образуя букву "Г".
 * Ниже точками выделены возможные ходы коня, а крестиками -- невозможные:
 *
 * .xxx.xxx
 * xxKxxxxx
 * .xxx.xxx
 * x.x.xxxx
 * xxxxxxxx
 * xxxxxxxx
 * xxxxxxxx
 * xxxxxxxx
 *
 * Если клетки start и end совпадают, вернуть 0.
 * Если любая из клеток некорректна, бросить IllegalArgumentException().
 *
 * Пример: knightMoveNumber(Square(3, 1), Square(6, 3)) = 3.
 * Конь может последовательно пройти через клетки (5, 2) и (4, 4) к клетке (6, 3).
 */
fun knightMoveNumber(start: Square, end: Square): Int {
    require(start.inside() && end.inside())
    if (start == end) return 0
    return knightTrajectory(start, end).size - 1
}

/**
 * Очень сложная
 *
 * Вернуть список из клеток, по которым шахматный конь может быстрее всего попасть из клетки start в клетку end.
 * Описание ходов коня см. предыдущую задачу.
 * Список всегда включает в себя клетку start. Клетка end включается, если она не совпадает со start.
 * Между ними должны находиться промежуточные клетки, по порядку от start до end.
 * Примеры:
 *
 * knightTrajectory(Square(3, 3), Square(3, 3)) = listOf(Square(3, 3))
 * здесь возможны другие варианты)
 * knightTrajectory(Square(3, 1), Square(6, 3)) = listOf(Square(3, 1), Square(5, 2), Square(4, 4), Square(6, 3))
 * (здесь возможен единственный вариант)
 * knightTrajectory(Square(3, 5), Square(5, 6)) = listOf(Square(3, 5), Square(5, 6))
 * (здесь опять возможны другие варианты)
 * knightTrajectory(Square(7, 7), Square(8, 8)) =
 *     listOf(Square(7, 7), Square(5, 8), Square(4, 6), Square(6, 7), Square(8, 8))
 *
 * Если возможно несколько вариантов самой быстрой траектории, вернуть любой из них.
 */
// Looked too dumb.
// fun toSquare(a: List<String>): List<Square> = a.map { Square((it.first().toInt() - 96), (it.last().toInt() - 48)) }

fun connectionGeneration(): Graph {
    val board = Graph()
    for (col in 1..8)
        for (row in 1..8)
            board.addVertex(Square(col, row).notation())
    val adjacent =
        listOf(Pair(1, 2), Pair(-1, -2), Pair(2, 1), Pair(-2, -1), Pair(-1, 2), Pair(1, -2), Pair(2, -1), Pair(-2, 1))
    for (i in 1..8)
        for (j in 1..8)
            for ((dX, dY) in adjacent) {
                val nextHop = Square(i + dX, j + dY)
                if (nextHop.row in 1..8 && nextHop.column in 1..8) board.connect(
                    Square(i, j).notation(),
                    nextHop.notation()
                )
            }
    return board
}

fun knightTrajectory(start: Square, end: Square): List<Square> {
    require(start.inside() && end.inside())
    if (start == end) return listOf(start)
    val board = connectionGeneration()
    return board.path(start.notation(), end.notation())
}

//fun main() {
//    val list = listOf("a6", "b4")
//   println(list + "c9")
//}