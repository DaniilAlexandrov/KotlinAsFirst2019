@file:Suppress("UNUSED_PARAMETER", "ConvertCallChainIntoSequence")

package lesson6.task1

import lesson2.task2.daysInMonth
import java.lang.Exception
import java.lang.IllegalArgumentException
import java.lang.NumberFormatException
import kotlin.math.max

/**
 * Пример
 *
 * Время представлено строкой вида "11:34:45", содержащей часы, минуты и секунды, разделённые двоеточием.
 * Разобрать эту строку и рассчитать количество секунд, прошедшее с начала дня.
 */
fun timeStrToSeconds(str: String): Int {
    val parts = str.split(":")
    var result = 0
    for (part in parts) {
        val number = part.toInt()
        result = result * 60 + number
    }
    return result
}

/**
 * Пример
 *
 * Дано число n от 0 до 99.
 * Вернуть его же в виде двухсимвольной строки, от "00" до "99"
 */
fun twoDigitStr(n: Int) = if (n in 0..9) "0$n" else "$n"

/**
 * Пример
 *
 * Дано seconds -- время в секундах, прошедшее с начала дня.
 * Вернуть текущее время в виде строки в формате "ЧЧ:ММ:СС".
 */
fun timeSecondsToStr(seconds: Int): String {
    val hour = seconds / 3600
    val minute = (seconds % 3600) / 60
    val second = seconds % 60
    return String.format("%02d:%02d:%02d", hour, minute, second)
}

/**
 * Пример: консольный ввод
 */
fun main() {
    println("Введите время в формате ЧЧ:ММ:СС")
    val line = readLine()
    if (line != null) {
        val seconds = timeStrToSeconds(line)
        if (seconds == -1) {
            println("Введённая строка $line не соответствует формату ЧЧ:ММ:СС")
        } else {
            println("Прошло секунд с начала суток: $seconds")
        }
    } else {
        println("Достигнут <конец файла> в процессе чтения строки. Программа прервана")
    }
}


/**
 * Средняя
 *
 * Дата представлена строкой вида "15 июля 2016".
 * Перевести её в цифровой формат "15.07.2016".
 * День и месяц всегда представлять двумя цифрами, например: 03.04.2011.
 * При неверном формате входной строки вернуть пустую строку.
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30.02.2009) считается неверными
 * входными данными.
 */
val monthList = listOf(
    "января",
    "февраля",
    "марта",
    "апреля",
    "мая",
    "июня",
    "июля",
    "августа",
    "сентября",
    "октября",
    "ноября",
    "декабря"
)

fun dateStrToDigit(str: String): String {
    val parts = str.split(" ")
    try {
        val year = parts[2].toInt()
        val day = parts[0].toInt()
        val month = if (parts[1] !in monthList) return "" else monthList.indexOf(parts[1]) + 1
        return if ((year < 0) || (day < 0) || (day > daysInMonth(month, year))) ""
        else "%02d.%02d.%d".format(day, month, year)
    } catch (e: Exception) {
        return ""
    }

}

/**
 * Средняя
 *
 * Дата представлена строкой вида "15.07.2016".
 * Перевести её в строковый формат вида "15 июля 2016".
 * При неверном формате входной строки вернуть пустую строку
 *
 * Обратите внимание: некорректная с точки зрения календаря дата (например, 30 февраля 2009) считается неверными
 * входными данными.
 */
fun dateDigitToStr(digital: String): String {
    val parts = digital.split(".")
    try {
        val year = parts[2].toInt()
        val month = parts[1].toInt()
        val day = parts[0].toInt()
        if ((day > daysInMonth(month, year)) || (month !in 1..12) || (parts.size != 3)) return ""
        return String.format("%d %s %d", day, monthList[month - 1], year)
    } catch (e: Exception) {
        return ""
    }

}

/**
 * Средняя
 *
 * Номер телефона задан строкой вида "+7 (921) 123-45-67".
 * Префикс (+7) может отсутствовать, код города (в скобках) также может отсутствовать.
 * Может присутствовать неограниченное количество пробелов и чёрточек,
 * например, номер 12 --  34- 5 -- 67 -89 тоже следует считать легальным.
 * Перевести номер в формат без скобок, пробелов и чёрточек (но с +), например,
 * "+79211234567" или "123456789" для приведённых примеров.
 * Все символы в номере, кроме цифр, пробелов и +-(), считать недопустимыми.
 * При неверном формате вернуть пустую строку.
 *
 * PS: Дополнительные примеры работы функции можно посмотреть в соответствующих тестах.
 */
fun flattenPhoneNumber(phone: String): String =
    if (!Regex("""([+][0-9])?[[0-9] -]*([(][[0-9] -]+[)])?[[0-9] -]*""").matches(phone)) ""
    else Regex("""[\s-()]""").replace(phone, "")


/**
 * Средняя
 *
 * Результаты спортсмена на соревнованиях в прыжках в длину представлены строкой вида
 * "706 - % 717 % 703".
 * В строке могут присутствовать числа, черточки - и знаки процента %, разделённые пробелами;
 * число соответствует удачному прыжку, - пропущенной попытке, % заступу.
 * Прочитать строку и вернуть максимальное присутствующее в ней число (717 в примере).
 * При нарушении формата входной строки или при отсутствии в ней чисел, вернуть -1.
 */
fun bestLongJump(jumps: String): Int {
    val parts = jumps.split(" ")
    var bestJump = -10  //Некоторое значение, которое при любом корректном вводе меньше результата.
    for (element in parts) {
        if ((element != "-") && (element != "%"))
            try {
                bestJump = if (bestJump == -10) element.toInt() else max(bestJump, element.toInt())
            } catch (e: NumberFormatException) {
                return -1
            }
    }
    return if (bestJump != -10) bestJump else -1
}

/**
 * Сложная
 *
 * Результаты спортсмена на соревнованиях в прыжках в высоту представлены строкой вида
 * "220 + 224 %+ 228 %- 230 + 232 %%- 234 %".
 * Здесь + соответствует удачной попытке, % неудачной, - пропущенной.
 * Высота и соответствующие ей попытки разделяются пробелом.
 * Прочитать строку и вернуть максимальную взятую высоту (230 в примере).
 * При нарушении формата входной строки, а также в случае отсутствия удачных попыток,
 * вернуть -1.
 */
fun bestHighJump(jumps: String): Int {
    val parts = jumps.split(" ")
    val numbersToChooseFrom = mutableSetOf<Int>()
    val loopLimit = parts.size
    for (i in 1 until loopLimit step 2) {
        if (parts[i].contains("+")) numbersToChooseFrom.add(parts[i - 1].toInt())
    }
    return if (numbersToChooseFrom.isNotEmpty()) numbersToChooseFrom.max()!!.toInt() else -1
}

/**
 * Сложная
 *
 * В строке представлено выражение вида "2 + 31 - 40 + 13",
 * использующее целые положительные числа, плюсы и минусы, разделённые пробелами.
 * Наличие двух знаков подряд "13 + + 10" или двух чисел подряд "1 2" не допускается.
 * Вернуть значение выражения (6 для примера).
 * Про нарушении формата входной строки бросить исключение IllegalArgumentException
 */
fun plusMinus(expression: String): Int {
    val parts = expression.split(" ")
    var sign = 1
    var res = 0
    require(expression.isNotEmpty() && expression.toList().toSet().first() != ' ')
    for (i in parts.indices) {
        if (i % 2 == 0) {
            require(parts[i].all { it in '0'..'9' })
            res += parts[i].toInt() * sign
        } else {
            require(!((parts[i] != "-") && (parts[i] != "+")))
            sign = if (parts[i] == "-") -1 else 1
        }

    }
    return res
}


/**
 * Сложная
 *
 * Строка состоит из набора слов, отделённых друг от друга одним пробелом.
 * Определить, имеются ли в строке повторяющиеся слова, идущие друг за другом.
 * Слова, отличающиеся только регистром, считать совпадающими.
 * Вернуть индекс начала первого повторяющегося слова, или -1, если повторов нет.
 * Пример: "Он пошёл в в школу" => результат 9 (индекс первого 'в')
 */
fun firstDuplicateIndex(str: String): Int {
    val parts = str.split(" ")
    var res = 0
    var currentElement = ""
    for (element in parts) {
        val nextElement = element.toLowerCase()
        if (currentElement == nextElement) return res - 1
        res += currentElement.length
        currentElement = nextElement
        res += 1
    }
    return -1
}

/**
 * Сложная
 *
 * Строка содержит названия товаров и цены на них в формате вида
 * "Хлеб 39.9; Молоко 62; Курица 184.0; Конфеты 89.9".
 * То есть, название товара отделено от цены пробелом,
 * а цена отделена от названия следующего товара точкой с запятой и пробелом.
 * Вернуть название самого дорогого товара в списке (в примере это Курица),
 * или пустую строку при нарушении формата строки.
 * Все цены должны быть больше либо равны нуля.
 */
fun mostExpensive(description: String): String {
    val parts = description.split("; ")
    var res = ""
    var maxPrice = -10.0 //Некоторое значение, которое при любом корректном вводе меньше результата.
    if (description.isEmpty()) return res
    for (element in parts) {
        val currentElementPrice = element.split(" ")[1].toDouble()
        val currentElementName = element.split(" ")[0]
        if (currentElementPrice < 0) return ""
        if ((currentElementPrice) > maxPrice) {
            maxPrice = currentElementPrice
            res = currentElementName
        }
    }
    return res
}

/**
 * Сложная
 *
 * Перевести число roman, заданное в римской системе счисления,
 * в десятичную систему и вернуть как результат.
 * Римские цифры: 1 = I, 4 = IV, 5 = V, 9 = IX, 10 = X, 40 = XL, 50 = L,
 * 90 = XC, 100 = C, 400 = CD, 500 = D, 900 = CM, 1000 = M.
 * Например: XXIII = 23, XLIV = 44, C = 100
 *
 * Вернуть -1, если roman не является корректным римским числом
 */
val mapOfRomans = mapOf(
    'M' to 1000,
    'D' to 500,
    'C' to 100,
    'L' to 50,
    'X' to 10,
    'V' to 5,
    'I' to 1
)

fun fromRoman(roman: String): Int {
    if ((!roman.matches(Regex("""M*(CM)?D*(CD)?C*(XC)?L*(XL)?X*(IX)?V*(IV)*I*""")))
        || (roman.isEmpty())
    ) return -1
    var res = 0
    val romanSize = roman.length - 1
    for (i in 0 until romanSize) {
        val currentValue = mapOfRomans[roman[i]] ?: error("")
        val nextValue = mapOfRomans[roman[i + 1]] ?: error("")
        if (currentValue < nextValue) res -= currentValue
        else res += currentValue
    }
    res += mapOfRomans[roman[romanSize]] ?: error("")
    return res
}

/**
 * Очень сложная
 *
 * Имеется специальное устройство, представляющее собой
 * конвейер из cells ячеек (нумеруются от 0 до cells - 1 слева направо) и датчик, двигающийся над этим конвейером.
 * Строка commands содержит последовательность команд, выполняемых данным устройством, например +>+>+>+>+
 * Каждая команда кодируется одним специальным символом:
 *	> - сдвиг датчика вправо на 1 ячейку;
 *  < - сдвиг датчика влево на 1 ячейку;
 *	+ - увеличение значения в ячейке под датчиком на 1 ед.;
 *	- - уменьшение значения в ячейке под датчиком на 1 ед.;
 *	[ - если значение под датчиком равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей следующей командой ']' (с учётом вложенности);
 *	] - если значение под датчиком не равно 0, в качестве следующей команды следует воспринимать
 *  	не следующую по порядку, а идущую за соответствующей предыдущей командой '[' (с учётом вложенности);
 *      (комбинация [] имитирует цикл)
 *  пробел - пустая команда
 *
 * Изначально все ячейки заполнены значением 0 и датчик стоит на ячейке с номером N/2 (округлять вниз)
 *
 * После выполнения limit команд или всех команд из commands следует прекратить выполнение последовательности команд.
 * Учитываются все команды, в том числе несостоявшиеся переходы ("[" при значении под датчиком не равном 0 и "]" при
 * значении под датчиком равном 0) и пробелы.
 *
 * Вернуть список размера cells, содержащий элементы ячеек устройства после завершения выполнения последовательности.
 * Например, для 10 ячеек и командной строки +>+>+>+>+ результат должен быть 0,0,0,0,0,1,1,1,1,1
 *
 * Все прочие символы следует считать ошибочными и формировать исключение IllegalArgumentException.
 * То же исключение формируется, если у символов [ ] не оказывается пары.
 * Выход за границу конвейера также следует считать ошибкой и формировать исключение IllegalStateException.
 * Считать, что ошибочные символы и непарные скобки являются более приоритетной ошибкой чем выход за границу ленты,
 * то есть если в программе присутствует некорректный символ или непарная скобка, то должно быть выброшено
 * IllegalArgumentException.
 * IllegalArgumentException должен бросаться даже если ошибочная команда не была достигнута в ходе выполнения.
 *
 */

fun computeDeviceCells(cells: Int, commands: String, limit: Int): List<Int> {
    val allowedLength = 0 until cells
    var position = cells / 2
    val list = mutableListOf<Int>()
    var aux = 0 // Вспомогательная переменная, используемая при действиях
    var commandCounter = 0
    var commandIndex = 0

    for (char in commands) {
        when (char) {
            '[' -> aux++
            ']' -> aux--
        }
    }
    require(aux == 0)

    for (i in allowedLength) list.add(0)

    while ((commandCounter < limit) && (commandIndex < commands.length)) {
        check(position in allowedLength)
        when (commands[commandIndex]) {
            '>' -> position++
            '<' -> position--
            '+' -> list[position]++
            '-' -> list[position]--
            ' ' -> {
            }
            '[' -> if (list[position] == 0) {
                aux = 1
                while (aux > 0) {
                    commandIndex++
                    if (commands[commandIndex] == '[') aux++
                    else if (commands[commandIndex] == ']') aux--
                }
            }

            ']' -> if (list[position] != 0) {
                aux = 1
                while (aux > 0) {
                    commandIndex--
                    if (commands[commandIndex] == ']') aux++
                    else if (commands[commandIndex] == '[') aux--
                }
            }
            else -> throw IllegalArgumentException()
        }
        commandIndex++
        commandCounter++
    }
    check(position in allowedLength)
    return list
}
