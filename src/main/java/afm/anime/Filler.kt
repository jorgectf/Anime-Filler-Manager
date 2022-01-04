package afm.anime

import afm.utils.isNumeric
import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import org.jsoup.Jsoup
import java.io.IOException

internal data class Filler(val start: Int, val end: Int) : Comparable<Filler> {

    operator fun contains(n: Int): Boolean = n in start..end

    override fun toString(): String = if (start == end) end.toString() else "$start-$end"

    // This smaller -> negative result
    override operator fun compareTo(other: Filler): Int =
        if (start != other.start) start - other.start else end - other.end

    companion object {
        // Start, End, Object
        private val CACHE: Table<Int, Int, Filler> = HashBasedTable.create()

        private fun of(start: Int, end: Int = start): Filler {
            var cached = CACHE[start, end]

            if (cached == null) {
                cached = Filler(start, end)
                CACHE.put(start, end, cached)
            }

            return cached
        }

        @JvmStatic
        fun valueOf(s: String): Filler {
            val divPos = s.indexOf('-')

            // single episode filler
            if (divPos == -1)
                return of(s.toInt())

            val start = s.substring(0, divPos).toInt()
            val end = s.substring(divPos + 1).toInt()
            return of(start, end)
        }

        @JvmStatic
        fun getFillers(name: String): List<Filler> {
            try {
                // replace all non-alphanumeric characters with a dash (which is what AFL does)
                val doc = Jsoup.connect("https://www.animefillerlist.com/shows/${formatName(name)}").get()

                // the filler element is always the last episode element
                val episodeElements = doc.select("span.episodes")

                // the anime has no filler
                if (episodeElements.isEmpty())
                    return emptyList()

                val fillerStrings = episodeElements.last()!!.text().split(", ")
                return fillerStrings.map(Filler::valueOf)

            } catch (io: IOException) {
                // the page doesn't exist, likely the MAL name is different to the AFL name
                return emptyList()
            }
        }

        // TODO: ref to kotlin
        private fun formatName(name: String): String {
            // replace all non-alphanumeric characters with a dash (which is what AFL does)
            var formattedName = replaceNonAlphaNumericWithDash(name.lowercase())
            // name.toLowerCase().replaceAll("[^a-zA-Z0-9]+", "-")

            // get rid of leading/trailing dashes (due to formatting above)
            fun String.trimDashes(): String = trim { it == '-' }

            formattedName = formattedName.trimDashes()

            // basically if name includes a year, remove it
            if (formattedName.length > 6 && formattedName.takeLast(4).isNumeric()) {
                formattedName = formattedName.dropLast(4)
                formattedName = formattedName.trimDashes()
            }

            return formattedName
        }

        // this took avg ~600ns vs regex ~6-7k ns
        private fun replaceNonAlphaNumericWithDash(s: String): String {
            val sb = StringBuilder()

            // help with if multiple characters in a row are non-alphanumeric
            var lastWasNonAlpha = false
            for (ch in s) {
                if (ch.isLetterOrDigit()) {
                    sb.append(ch)
                    lastWasNonAlpha = false
                } else if (!lastWasNonAlpha) {
                    sb.append('-')
                    lastWasNonAlpha = true
                }
            }
            return sb.toString()
        }
    }
}