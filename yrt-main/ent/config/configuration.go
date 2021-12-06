package config

type Config struct {
	Pika
	MySQL
}

type Pika struct {
	Host     string
	Password string
}

type MySQL struct {
	Host     string
	Port     string
	User     string
	Password string
	Database string
}
