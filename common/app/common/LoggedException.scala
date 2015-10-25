package common

import utilities.StringUtils

final case class LoggedException(userMessage: String, debugMessage: String = null)
    extends RuntimeException( userMessage )
