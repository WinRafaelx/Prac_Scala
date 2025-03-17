import Dependencies._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.rafacalvo"
ThisBuild / organizationName := "Rafaelx"

lazy val root = (project in file("."))
  .settings(
    name := "prac",
    libraryDependencies += munit % Test
  )

// Versions
val AkkaVersion = "2.8.0"
val AkkaHttpVersion = "10.5.0"
val CirceVersion = "0.14.6"
val SlickVersion = "3.4.1"
val JwtVersion = "9.4.6"

// =============================
// ✅ Akka Dependencies
// =============================
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion,
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % AkkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % AkkaVersion % Test
)

// =============================
// ✅ Circe (JSON) Dependencies
// =============================
libraryDependencies ++= Seq(
  "de.heikoseeberger" %% "akka-http-circe" % "1.39.2", // JSON support for Akka HTTP
  "io.circe" %% "circe-generic" % CirceVersion,
  "io.circe" %% "circe-parser" % CirceVersion
)

// =============================
// ✅ Spray JSON (Keep this since you added it)
// =============================
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-spray-json" % AkkaHttpVersion
)

// =============================
// ✅ Slick (Postgres) Dependencies
// =============================
libraryDependencies ++= Seq(
  "com.typesafe.slick" %% "slick" % SlickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % SlickVersion,
  "org.postgresql" % "postgresql" % "42.7.1"
)

// =============================
// ✅ JWT Authentication
// =============================
libraryDependencies ++= Seq(
  "com.github.jwt-scala" %% "jwt-core" % JwtVersion,
  "com.github.jwt-scala" %% "jwt-circe" % JwtVersion
)

// =============================
// ✅ Password Hashing
// =============================
libraryDependencies += "com.github.t3hnar" %% "scala-bcrypt" % "4.3.0"

// =============================
// ✅ Config and Environment
// =============================
libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.3", // For application.conf
  "io.github.cdimascio" % "dotenv-java" % "2.3.2" // For loading .env files
)

// =============================
// ✅ Testing Dependencies
// =============================
libraryDependencies ++= Seq(
  "org.scalatest" %% "scalatest" % "3.2.18" % Test
)
