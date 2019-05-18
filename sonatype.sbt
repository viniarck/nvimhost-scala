sonatypeProfileName := "io.github.viniarck"
publishMavenStyle := true
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0.txt"))

import xerial.sbt.Sonatype._
sonatypeProjectHosting := Some(GitHubHosting("viniarck", "nvimhost-scala", "viniarck@gmail.com"))

homepage := Some(url("https://www.github.com/viniarck/nvimhost-scala"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/viniarck/nvimhost-scala"),
    "scm:git@github.com:viniarck/nvimhost-scala.git"
  )
)
developers := List(
  Developer(id="viniarck", name="Vinicius Arcanjo", email="viniarck@gmail.com", url=url("https://www.github.com/viniarck/nvimhost-scala"))
)
