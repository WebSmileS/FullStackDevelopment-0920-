package config

import (
	"encoding/json"
	"os"

	"github.com/pkg/errors"
)

type Version struct {
	AndroidVer  string   `json:"androidVer"`
	IOSVer      string   `json:"iosVer"`
	Changelog   []string `json:"changelog"`
	ApkDownload string   `json:"apkDownload"`
}

type AppVersion struct {
	Version   string   `json:"ver"`
	Changelog []string `json:"changelog"`
	Download  string   `json:"download"`
}

// 根据输入读取配置文件
func loadVersion() (*Version, error) {
	ver := new(Version)

	versionFile, err := os.Open(VersionFile)
	defer versionFile.Close()

	if err != nil {
		return ver, errors.Wrap(err, "获取版本信息错误")
	}

	decoder := json.NewDecoder(versionFile)
	if err := decoder.Decode(ver); err != nil {
		return ver, errors.Wrap(err, "配置版本信息错误")
	}

	return ver, nil
}

func GetAndroidVersion() (*AppVersion, error) {
	version, err := loadVersion()
	if err != nil {
		return nil, errors.Wrap(err, "")
	}
	android := &AppVersion{
		Version:   version.AndroidVer,
		Changelog: version.Changelog,
		Download:  version.ApkDownload,
	}

	return android, nil
}

func GetIOSVersion() (*AppVersion, error) {
	version, err := loadVersion()
	if err != nil {
		return nil, errors.Wrap(err, "")
	}
	ios := &AppVersion{
		Version:   version.IOSVer,
		Changelog: version.Changelog,
		Download:  "",
	}

	return ios, nil
}

func GetAppVersion() (*Version, error) {
	return loadVersion()
}
