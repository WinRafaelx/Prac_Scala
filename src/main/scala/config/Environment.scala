package config

import io.github.cdimascio.dotenv.Dotenv

/**
 * Environment configuration loader
 * Handles loading of environment variables and .env file
 */
object Environment {
  // Load .env file if present, otherwise use environment variables
  private val dotenv = Dotenv.configure()
    .ignoreIfMissing()
    .load()

  /**
   * Get environment variable
   * @param key The variable name
   * @param defaultValue Optional default value if not found
   * @return The variable value or default
   */
  def get(key: String, defaultValue: String = ""): String = {
    // Try system environment variables first, then .env file
    sys.env.getOrElse(key, dotenv.get(key, defaultValue))
  }

  /**
   * Get required environment variable, throws if not found
   * @param key The variable name
   * @return The variable value
   * @throws IllegalArgumentException if variable not found
   */
  def getRequired(key: String): String = {
    val value = get(key, "")
    if (value.isEmpty) {
      throw new IllegalArgumentException(s"Required environment variable $key not found")
    }
    value
  }

  /**
   * Get environment variable as Int
   */
  def getInt(key: String, defaultValue: Int): Int = {
    try {
      get(key, "").toInt
    } catch {
      case _: NumberFormatException => defaultValue
    }
  }
}