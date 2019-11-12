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

/*fun main() {
ПОМОГИТЕ
    val p1 = Point(-632.0, -632.0)
    val p2 = Point(0.9411807068960312, 0.24262048974096473)
    val p3 = Point(0.9654551026953924, 0.018026749024876)
    val p4 = Point(-632.0, 0.6150936273859192)
    val p5 = Point(0.6509315334764997, 0.20301608356326495)
    val p6 = Point(0.13151523852328462, -632.0)
    val p7 = Point(0.4050555622764477, 0.35948888380976995)
    val p8 = Point(0.07875755434592724, 0.8978424618589593)
    val p9 = Point(0.9799646636545054, 0.7094708222398678)
    val p10 = Point(0.4787703008451043, 0.6455765964500987)
    val p11 = Point(0.3175257490606408, 0.5355998786535994)
    val p12 = Point(-632.0, -632.0)
    val p13 = Point(0.47396725833924724, 0.0)
    val p14 = Point(2.220446049250313e-16, -632.0)
    val p15 = Point(0.5349887484426901, 0.8320297833844905)
    val p16 = Point(0.7049030277683747, -632.0)
    val p17 = Point(0.2776160829985631, 5e-324)
    val p18 = Point(0.02207503853526327, 0.6811604156796618)
    val p19 = Point(2.220446049250313e-16, 0.20514743033346616)
    val p20 = Point(0.248456941848875, 0.7864093608849441)
    val p21 = Point(0.763286208634464, 0.0234195457075681)
    val p22 = Point(-632.0, -632.0)
    val p23 = Point(0.9374683787481295, 0.49882568632269864)
    val p24 = Point(0.21097436408950876, 5e-324)
    val p25 = Point(0.12584008561147864, 0.0)
    val p26 = Point(5e-324, -632.0)
    val p27 = Point(-632.0, 0.4710972938300373)
    val p28 = Point(2.220446049250313e-16, 5e-324)
    val p29 = Point(0.9623064095794293, 0.8751672452312513)
    val p30 = Point(2.220446049250313e-16, 5e-324)
    val p31 = Point(0.5371981632299996, 0.34041032942956984)
    val p32 = Point(-2.220446049250313e-16, 0.9301093504836988)
    val p33 = Point(-632.0, -632.0)
    val p34 = Point(-2.220446049250313e-16, 2.220446049250313e-16)
    val p35 = Point(-632.0, 5e-324)
    val p36 = Point(0.4936797104011381, 0.9434646875117227)
    val p37 = Point(0.15677622750110887, 0.7875992219339196)
    val p38 = Point(-2.220446049250313e-16, 0.2903442939432266)
    val p39 = Point(0.8078411975116571, 5e-324)
    val p40 = Point(-5e-324, 0.5621893979552561)
    val p41 = Point(0.41921525562913886, 0.6289678819941725)
    val p42 = Point(0.010196356354627567, 0.4828333537104399)
    val p43 = Point(0.2690688273361166, 0.1922545399310558)
    val p44 = Point(5e-324, 0.8241528339473233)
    val p45 = Point(0.030880171236884912, 0.8497499881739126)
    val p46 = Point(0.7674837086052284, 0.1245830105534047)
    val p47 = Point(2.220446049250313e-16, -632.0)
    val p48 = Point(-632.0, 2.220446049250313e-16)
    val p49 = Point(0.0, 0.0)
    val p50 = Point(2.220446049250313e-16, 2.220446049250313e-16)
    val p51 = Point(0.26841900547744246, 0.6713892732632792)
    val p52 = Point(-632.0, 0.9063390674402898)
    val p53 = Point(0.621953183869507, -632.0)
    val p54 = Point(-5e-324, -632.0)
    val p55 = Point(-632.0, 0.7349478244395107)
    val p56 = Point(0.515246094086135, 0.25686215555173575)
    val p57 = Point(0.6567911468410101, -632.0)
    val p58 = Point(2.220446049250313e-16, 5e-324)
    val p59 = Point(0.0, 0.6871158188509888)
    val p60 = Point(-5e-324, 2.220446049250313e-16)
    val p61 = Point(-632.0, 0.6108492226448645)
    val p62 = Point(-2.220446049250313e-16, -2.220446049250313e-16)
    val p63 = Point(-632.0, 5e-324)
    val p64 = Point(0.0, 0.7333306086339908)
    val p65 = Point(-632.0, 0.11139866397663134)
    val p66 = Point(0.0, 0.329129802352384)
    val p67 = Point(0.16580263024208108, 0.24957430083306142)
    val p68 = Point(-632.0, 0.7310570419249229)
    val p69 = Point(0.10102607893394278, -5e-324)
    val p70 = Point(0.0, -632.0)
    val p71 = Point(0.0, 0.39002394750621006)
    val p72 = Point(0.6315731800929771, 0.0)
    val p73 = Point(0.9934363706410263, 0.6359961782827329)
    val p74 = Point(0.6526398126635151, 0.6318240897432198)
    println(
        minContainingCircle(
            p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12,
            p13, p14, p15, p16, p17, p18, p19, p20, p21, p22, p23, p24, p25, p26, p27, p28, p29, p30, p31, p32, p33,
            p34, p35, p36, p37, p38, p39, p40, p41, p42, p43, p44, p45, p46, p47, p48, p49, p50, p51, p52, p53, p54,
            p55, p56, p57, p58, p59, p60, p61, p62, p63, p64, p65, p66, p67, p68, p69, p70, p71, p72, p73, p74
        )
    )
}*/
/*fun main() {
    val p1 = Point(-632.0, 0.8050317828064927)
    val p2 = Point(0.0, -632.0)
    val p3 = Point(-632.0, -2.220446049250313e-16)
    val p4 = Point(0.6420241922765781, 0.6782796648311007)
    val p5 = Point(0.4799755103405119, -2.220446049250313e-16)
    val p6 = Point(0.4386978523272347, 0.2789036274375052)
    val p7 = Point(2.220446049250313e-16, 0.3217797935156028)
    val p8 = Point(0.40132758779491573, 0.2507054943289473)
    val p9 = Point(0.0, 0.16293403441180876)
    val p10 = Point(-632.0, -5e-324)
    val p11 = Point(0.6339419374855758, 0.0)
    val p12 = Point(0.8011567898523145, -632.0)
    val p13 = Point(0.7147901267704037, 0.8209759398881262)
    val p14 = Point(0.0, 0.8339089373277871)
    val p15 = Point(-632.0, 0.30884107332528266)
    val p16 = Point(0.03879070139568819, 0.3649109444011581)
    val p17 = Point(0.3864101960415818, 0.294344568087093)
    val p18 = Point(-632.0, 0.7406746074615942)
    val p19 = Point(0.2580487239145476, -632.0)
    val p20 = Point(0.2826417449199319, -632.0)
    val p21 = Point(0.9403286536879756, -5e-324)
    val p22 = Point(0.7064629075242836, 0.08301500837896403)
    val p23 = Point(0.3150957773271038, 0.9628173200187997)
    val p24 = Point(0.7861184102506047, 2.220446049250313e-16)
    val p25 = Point(-632.0, 0.9586914914269127)
    val p26 = Point(2.220446049250313e-16, 0.15400556289600897)
    val p27 = Point(0.4708433266041697, 0.8665115355659835)
    val p28 = Point(-632.0, 0.7438910762962831)
    val p29 = Point(-632.0, 0.18416821825263552)
    val p30 = Point(-632.0, 0.5080123858978958)
    println(
        minContainingCircle(
            p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11, p12,
            p13, p14, p15, p16, p17, p18, p19, p20, p21, p22, p23, p24, p25, p26, p27, p28, p29, p30
        )
    )
} */

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
/* Построчно реализованный алгоритм, описанный на https://codeforces.com/blog/entry/3229
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
} */
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
