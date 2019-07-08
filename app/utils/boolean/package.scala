package utils

package object boolean {
  implicit class BooleanOps(val self: Boolean) extends AnyVal {

    /**
     * if expression is true, returns executed value wrapped into Some, else returns None
     * @param value
     * @tparam A
     * @return
     */
    def toOption[A](value: => A): Option[A] =
      if (self) Some(value) else None
  }
}
