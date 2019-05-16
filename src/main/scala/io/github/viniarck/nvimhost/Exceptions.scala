package nvimhost

final case class PluginParsingException(
    private val message: String = "",
    private val cause: Throwable = None.orNull
) extends Exception(message, cause)
