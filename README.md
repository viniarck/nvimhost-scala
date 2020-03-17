[![pipeline status](https://gitlab.com/viniarck/nvimhost-scala/badges/master/pipeline.svg)](https://gitlab.com/viniarck/nvimhost-scala/commits/master)![Maven Central](https://img.shields.io/maven-central/v/io.github.viniarck/nvimhost-scala_2.13.svg?style=plastic)

## nvimhost-scala

Neovim (nvim) host plugin provider and API client library in [Scala](https://www.scala-lang.org/).

![nvimhost-scala](./design/nvimhostscala.png)

## Screencast

![screencast](https://s3.gifyu.com/images/ezgif.com-crop7c17fcbf5b8ea8fd.gif)

## Goals

- Provide an API for other projects to integrate with nvim.
- Provide a library for high-performance plugins with Scala static types.
- Pay the JVM startup cost only once (when the plugin is first called).

## Docs

- You can find the API client code on [Api.scala](./src/main/scala/io/github/viniarck/nvimhost/Api.scala), it's fully asynchronous based on `scala.concurrent.Future`
- If you want to develop a plugin you should read [how to write a plugin](docs/plugin_how_to.md).

### Docs TLDR for Scala 2.13

```
libraryDependencies += "io.github.viniarck" %% "nvimhost-scala" % "1.1.0"
```

## How to compile with SBT

- In the [CI yml file](./.gitlab-ci.yml) there's a complete example how to compile, source (from Neovim) and run the plugin.
- In the test folder, on [build.sbt](./src/test/build.sbt) you can find a base configuration to start your build.sbt configuration.
