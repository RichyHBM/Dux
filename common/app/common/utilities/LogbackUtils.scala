package common.utilities

import ch.qos.logback.classic.{Level, Logger}
import org.slf4j.LoggerFactory

object LogbackUtils {
  def setLevel(loggerName: String, level: Level) = {
    val logger: Logger = LoggerFactory.getLogger(loggerName).asInstanceOf[Logger]
    val previousLevel: Level = logger.getLevel
    logger.setLevel(level)
    previousLevel
  }
}
