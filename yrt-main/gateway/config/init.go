package config

var ConfigFile string
var VersionFile string

func init() {
	VersionFile = "version.json"
	ConfigFile = "config.json"
}
