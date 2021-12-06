package logger

type Logger interface {
	//  Println(str ...string)
	Panic(str ...string)
	Fatal(str ...string)
	Error(str ...string)
	Warm(str ...string)
	Info(str ...string)
	Debug(str ...string)
}
