

abstract class HiggsTwitter extends Serializable {
    protected val appName = "HiggsTwitter"
    protected val rootPath = scala.util.Properties.envOrElse("HOME", "~/" + appName)
}
