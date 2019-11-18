@file:Suppress("UNUSED_PARAMETER")

package lesson8.task1

import lesson1.task1.sqr
import kotlin.math.*

/**
 * Точка на плоскости
 */
data class Point(val x: Double, val y: Double) {
    /**
     * Пример
     *
     * Рассчитать (по известной формуле) расстояние между двумя точками
     */
    fun distance(other: Point): Double = sqrt(sqr(x - other.x) + sqr(y - other.y))
}

/**
 * Треугольник, заданный тремя точками (a, b, c, см. constructor ниже).
 * Эти три точки хранятся в множестве points, их порядок не имеет значения.
 */
@Suppress("MemberVisibilityCanBePrivate")
class Triangle private constructor(private val points: Set<Point>) {

    private val pointList = points.toList()

    val a: Point get() = pointList[0]

    val b: Point get() = pointList[1]

    val c: Point get() = pointList[2]

    constructor(a: Point, b: Point, c: Point) : this(linkedSetOf(a, b, c))

    /**
     * Пример: полупериметр
     */
    fun halfPerimeter() = (a.distance(b) + b.distance(c) + c.distance(a)) / 2.0

    /**
     * Пример: площадь
     */
    fun area(): Double {
        val p = halfPerimeter()
        return sqrt(p * (p - a.distance(b)) * (p - b.distance(c)) * (p - c.distance(a)))
    }

    /**
     * Пример: треугольник содержит точку
     */
    fun contains(p: Point): Boolean {
        val abp = Triangle(a, b, p)
        val bcp = Triangle(b, c, p)
        val cap = Triangle(c, a, p)
        return abp.area() + bcp.area() + cap.area() <= area()
    }

    override fun equals(other: Any?) = other is Triangle && points == other.points

    override fun hashCode() = points.hashCode()

    override fun toString() = "Triangle(a = $a, b = $b, c = $c)"
}

/**
 * Окружность с заданным центром и радиусом
 */
data class Circle(val center: Point, val radius: Double) {
    /**
     * Простая
     *
     * Рассчитать расстояние между двумя окружностями.
     * Расстояние между непересекающимися окружностями рассчитывается как
     * расстояние между их центрами минус сумма их радиусов.
     * Расстояние между пересекающимися окружностями считать равным 0.0.
     */
    fun distance(other: Circle): Double {
        val distance = center.distance(other.center) - (radius + other.radius)
        return if (distance > 0) distance else 0.0
    }

    /**
     * Тривиальная
     *
     * Вернуть true, если и только если окружность содержит данную точку НА себе или ВНУТРИ себя
     */
    fun contains(p: Point): Boolean = center.distance(p) <= radius
}

/**
 * Отрезок между двумя точками
 */
data class Segment(val begin: Point, val end: Point) {
    override fun equals(other: Any?) =
        other is Segment && (begin == other.begin && end == other.end || end == other.begin && begin == other.end)

    override fun hashCode() =
        begin.hashCode() + end.hashCode()
}

/**
 * Средняя
 *
 * Дано множество точек. Вернуть отрезок, соединяющий две наиболее удалённые из них.
 * Если в множестве менее двух точек, бросить IllegalArgumentException
 */
// Copy pasted with minor adjustments from nearestCirclePair
fun diameter(vararg points: Point): Segment {
    require(points.size > 1)
    var maxDistance = Double.NEGATIVE_INFINITY
    var minIndexPair = Pair(0, 0)
    for (i in 0 until points.size - 1)
        for (j in i + 1 until points.size) {
            val currentDistance = points[i].distance(points[j])
            if (currentDistance > maxDistance) {
                minIndexPair = Pair(i, j)
                maxDistance = currentDistance
            }
        }
    return Segment(points[minIndexPair.first], points[minIndexPair.second])
}

/**
 * Простая
 *
 * Построить окружность по её диаметру, заданному двумя точками
 * Центр её должен находиться посередине между точками, а радиус составлять половину расстояния между ними
 */
fun circleByDiameter(diameter: Segment): Circle =
    Circle(
        Point((diameter.begin.x + diameter.end.x) / 2, (diameter.begin.y + diameter.end.y) / 2),
        diameter.begin.distance(diameter.end) / 2
    )

/**
 * Прямая, заданная точкой point и углом наклона angle (в радианах) по отношению к оси X.
 * Уравнение прямой: (y - point.y) * cos(angle) = (x - point.x) * sin(angle)
 * или: y * cos(angle) = x * sin(angle) + b, где b = point.y * cos(angle) - point.x * sin(angle).
 * Угол наклона обязан находиться в диапазоне от 0 (включительно) до PI (исключительно).
 */
class Line private constructor(

    val b: Double,
    val angle: Double
) {
    init {
        require(angle >= 0 && angle < PI) { "Incorrect line angle: $angle" }
    }

    constructor(point: Point, angle: Double) : this(point.y * cos(angle) - point.x * sin(angle), angle)

    /**
     * Средняя
     *
     * Найти точку пересечения с другой линией.
     * Для этого необходимо составить и решить систему из двух уравнений (каждое для своей прямой)
     */
    fun crossPoint(other: Line): Point {
        val crossX = (other.b * cos(angle) - b * cos(other.angle)) / sin(angle - other.angle)
        val crossY = (crossX * sin((angle)) + b) / cos(angle)
        val perpendicularCaseY = (sin(other.angle) * crossX + other.b) / cos(other.angle)
        return if (angle == PI / 2) Point(crossX, perpendicularCaseY) else
            Point(crossX, crossY)
    }

    override fun equals(other: Any?) = other is Line && angle == other.angle && b == other.b

    override fun hashCode(): Int {
        var result = b.hashCode()
        result = 31 * result + angle.hashCode()
        return result
    }

    override fun toString() = "Line(${cos(angle)} * y = ${sin(angle)} * x + $b)"
}

/**
 * Средняя
 *
 * Построить прямую по отрезку
 */
fun lineBySegment(s: Segment): Line = lineByPoints(s.begin, s.end)

/**
 * Средняя
 *
 * Построить прямую по двум точкам
 */
fun lineByPoints(a: Point, b: Point): Line {
    val k = (b.y - a.y) / (b.x - a.x)
    return if (k < 0) Line(a, (2 * PI + atan(k)) % PI)
    else Line(a, atan(k) % PI)
}

/**
 * Сложная
 *
 * Построить серединный перпендикуляр по отрезку или по двум точкам
 */
fun bisectorByPoints(a: Point, b: Point): Line {
    val desirableAngle = atan((a.y - b.y) / (a.x - b.x)) + PI / 2
    val applicationPoint = Point((b.x + a.x) / 2, (b.y + a.y) / 2)
    return Line(applicationPoint, desirableAngle % PI)
}

/**
 * Средняя
 *
 * Задан список из n окружностей на плоскости. Найти пару наименее удалённых из них.
 * Если в списке менее двух окружностей, бросить IllegalArgumentException
 */
fun findNearestCirclePair(vararg circles: Circle): Pair<Circle, Circle> {
    require(circles.size > 1)
    var minDistance = Double.POSITIVE_INFINITY
    var minIndexPair = Pair(0, 0)
    for (i in 0 until circles.size - 1)
        for (j in i + 1 until circles.size) {
            val currentDistance = circles[i].distance(circles[j])
            if (currentDistance < minDistance) {
                minIndexPair = Pair(i, j)
                minDistance = currentDistance
            }
        }
    return Pair(circles[minIndexPair.first], circles[minIndexPair.second])
}

/**
 * Сложная
 *
 * Дано три различные точки. Построить окружность, проходящую через них
 * (все три точки должны лежать НА, а не ВНУТРИ, окружности).
 * Описание алгоритмов см. в Интернете
 * (построить окружность по трём точкам, или
 * построить окружность, описанную вокруг треугольника - эквивалентная задача).
 */
fun circleByThreePoints(a: Point, b: Point, c: Point): Circle {
    val circleCenter = bisectorByPoints(a, b).crossPoint(bisectorByPoints(b, c))
    val circleRadius = circleCenter.distance(a)
    return Circle(circleCenter, circleRadius)
}

/**
 * Очень сложная
 *
 * Дано множество точек на плоскости. Найти круг минимального радиуса,
 * содержащий все эти точки. Если множество пустое, бросить IllegalArgumentException.
 * Если множество содержит одну точку, вернуть круг нулевого радиуса с центром в данной точке.
 *
 * Примечание: в зависимости от ситуации, такая окружность может либо проходить через какие-либо
 * три точки данного множества, либо иметь своим диаметром отрезок,
 * соединяющий две самые удалённые точки в данном множестве.
 */
/* https://codeforces.com/blog/entry/3229
fun minContainingCircle(vararg points: Point): Circle {
    require(points.isNotEmpty())
    if (points.size == 1) return Circle(points[0], 0.0)
    val middle = Point((points[0].x + points[1].x) / 2, (points[0].y + points[1].y) / 2)
    var res = Circle(middle, middle.distance(points[1]))
    val pointsList = points.toList()//.shuffled()
    for (index in 2 until pointsList.size) {
        if (res.contains(pointsList[index])) continue
        else {
            val toBeChecked = points.toMutableList().subList(0, index).shuffled()
            res = circleByOnePoint(toBeChecked, pointsList[index])
        }
    }
    return res
}

fun circleByOnePoint(points: List<Point>, firstFixed: Point): Circle {
    val middle = Point((points[0].x + firstFixed.x) / 2, (points[0].y + firstFixed.y) / 2)
    var tempMinCircle = Circle(middle, middle.distance(firstFixed))
    for (index in 1 until points.size) {
        if (tempMinCircle.contains(points[index])) continue
        else {
            val toBeChecked = points.toMutableList().subList(0, index)//.shuffled()
            tempMinCircle = circleByTwoPoints(toBeChecked, firstFixed, points[index])
        }
    }
    return tempMinCircle
}

fun circleByTwoPoints(points: List<Point>, firstFixed: Point, secondFixed: Point): Circle {
    val middle = Point((secondFixed.x + firstFixed.x) / 2, (secondFixed.y + firstFixed.y) / 2)
    var tempMinCircle = Circle(middle, middle.distance(firstFixed))
    for (point in points) {
        if (tempMinCircle.contains(point)) continue
        else tempMinCircle = circleByThreePoints(point, firstFixed, secondFixed)
    }
    return tempMinCircle
}  */
fun minContainingCircle(vararg points: Point): Circle {
    require(points.isNotEmpty())
    if (points.size == 1) return Circle(points[0], 0.0)
    val diam = diameter(*points)
    var testCircle = circleByDiameter(diam)
    for (point in points.toSet() - diam.begin - diam.end) {
        if (!testCircle.contains(point)) {
            testCircle = circleByThreePoints(diam.begin, diam.end, point)
        }
    }
    return testCircle
}
