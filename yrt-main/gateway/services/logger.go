package services

import (
	"os"

	"github.com/go-kit/kit/log"
)

var logger log.Logger

func initLogger() {
	logger = log.NewLogfmtLogger(os.Stderr)
}
