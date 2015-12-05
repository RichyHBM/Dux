package common.utilities

object StringUtils {
  def isEmpty(str: String): Boolean = {
    str == null || str.isEmpty()
  }

  def isNotEmpty(str: String): Boolean = {
    str != null && !str.isEmpty()
  }
}
