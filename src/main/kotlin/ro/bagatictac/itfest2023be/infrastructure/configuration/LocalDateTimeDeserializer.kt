package ro.bagatictac.itfest2023be.infrastructure.configuration

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import java.io.IOException
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class LocalDateTimeDeserializer : JsonDeserializer<LocalDateTime>() {

    @Throws(IOException::class)
    override fun deserialize(parser: JsonParser, context: DeserializationContext): LocalDateTime {
        val instant = Instant.ofEpochMilli(parser.longValue) // Assuming the long value is in milliseconds
        val localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm") // Adjust the pattern as needed
        val formattedDateTime = localDateTime.format(formatter)

        return LocalDateTime.parse(formattedDateTime, formatter)
    }
}