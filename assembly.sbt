import sbtassembly.Plugin.AssemblyKeys._

assemblySettings

jarName in assembly := "PortexAnalyzer.jar"

test in assembly := {}

mergeStrategy in assembly <<= (mergeStrategy in assembly) { (old) =>
  {
    case x if x.endsWith(".exe") => MergeStrategy.discard
    case PathList("importreports", xs @ _*) => MergeStrategy.discard
    case PathList("testfiles", xs @ _*) => MergeStrategy.discard
    case PathList("reports", xs @ _*) => MergeStrategy.discard
    case PathList("exportreports", xs @ _*) => MergeStrategy.discard
    case PathList("tinype", xs @ _*) => MergeStrategy.discard
    case PathList("yoda", xs @ _*) => MergeStrategy.discard
    case PathList("corkami", xs @ _*) => MergeStrategy.discard
    case PathList("unusualfiles", xs @ _*) => MergeStrategy.discard
    case PathList("x64viruses", xs @ _*) => MergeStrategy.discard
    case x => old(x)
  }
}

mainClass in assembly := Some("com.github.struppigel.tools.PortExAnalyzer")
