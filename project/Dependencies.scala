import sbt._
/**
  * Created by
  * Author: fayaz.sanaulla@gmail.com
  * Date: 28.08.17
  */
object Dependencies {

  final val dep = Seq(
    "com.typesafe.akka"   %%   "akka-http"              %   Versions.akkaHttp,
    "com.typesafe.akka"   %%   "akka-http-spray-json"   %   Versions.akkaHttp,
    "org.scalatest"       %%   "scalatest"              %   Versions.scalaTest   % "test",
    "com.storm-enroute"   %%   "scalameter"             %   Versions.scalaMeter  % "test"
  )

}