@file:Suppress("UNUSED_PARAMETER")

package lesson12.task1

/**
 * Класс "Телефонная книга".
 *
 * Общая сложность задания -- средняя.
 * Объект класса хранит список людей и номеров их телефонов,
 * при чём у каждого человека может быть более одного номера телефона.
 * Человек задаётся строкой вида "Фамилия Имя".
 * Телефон задаётся строкой из цифр, +, *, #, -.
 * Поддерживаемые методы: добавление / удаление человека,
 * добавление / удаление телефона для заданного человека,
 * поиск номера(ов) телефона по заданному имени человека,
 * поиск человека по заданному номеру телефона.
 *
 * Класс должен иметь конструктор по умолчанию (без параметров).
 */
class PhoneBook {
    private val book = mutableMapOf<String, Set<String>>()
    private val nameTemplate = Regex("""([A-ZА-ЯЁ][a-zа-яё]*) [A-ZА-ЯЁ][a-zа-я]*""")
    private val phoneRestrictions = Regex("""[^\d+*#\-]""")
    /**
     * Добавить человека.
     * Возвращает true, если человек был успешно добавлен,
     * и false, если человек с таким именем уже был в телефонной книге
     * (во втором случае телефонная книга не должна меняться).
     */
    fun addHuman(name: String): Boolean {
        require(name.matches(nameTemplate))
        if (name in book) {
            return false
        }
        book[name] = mutableSetOf()
        return true
    }

    /**
     * Убрать человека.
     * Возвращает true, если человек был успешно удалён,
     * и false, если человек с таким именем отсутствовал в телефонной книге
     * (во втором случае телефонная книга не должна меняться).
     */
    fun removeHuman(name: String): Boolean {
        require(name.matches(nameTemplate))
        if (name in book) {
            book.remove(name)
            return true
        }
        return false
    }

    /**
     * Добавить номер телефона.
     * Возвращает true, если номер был успешно добавлен,
     * и false, если человек с таким именем отсутствовал в телефонной книге,
     * либо у него уже был такой номер телефона,
     * либо такой номер телефона зарегистрирован за другим человеком.
     */

    fun addPhone(name: String, phone: String): Boolean {
        require(name.matches(nameTemplate) && !phone.contains(phoneRestrictions))
        if (book.values.any { phone in it }) return false
        if (book[name] != null) {
            book[name] = book[name]!! + phone
            return true
        }
        return false
    }

    /**
     * Убрать номер телефона.
     * Возвращает true, если номер был успешно удалён,
     * и false, если человек с таким именем отсутствовал в телефонной книге
     * либо у него не было такого номера телефона.
     */
    fun removePhone(name: String, phone: String): Boolean {
        require(name.matches(nameTemplate) && !phone.contains(phoneRestrictions))
        val personPhones = book[name] ?: return false
        if (phone in personPhones) {
            book[name] = personPhones - phone
            return true
        }
        return false
    }

    /**
     * Вернуть все номера телефона заданного человека.
     * Если этого человека нет в книге, вернуть пустой список
     */
    fun phones(name: String): Set<String> {
        require(name.matches(nameTemplate))
        return book.getOrDefault(name, emptySet())
    }


    /**
     * Вернуть имя человека по заданному номеру телефона.
     * Если такого номера нет в книге, вернуть null.
     */
    fun humanByPhone(phone: String): String? {
        require(!phone.contains(phoneRestrictions))
        for ((human, numbers) in book) {
            if (phone in numbers) {
                return human
            }
        }
        return null
    }

    /**
     * Две телефонные книги равны, если в них хранится одинаковый набор людей,
     * и каждому человеку соответствует одинаковый набор телефонов.
     * Порядок людей / порядок телефонов в книге не должен иметь значения.
     */

    override fun equals(other: Any?): Boolean {
        return if (other is PhoneBook) {
            this.book == other.book
        } else false
    }

    override fun hashCode(): Int = book.hashCode()
}