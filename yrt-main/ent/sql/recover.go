package sql

import (
	"fmt"

	"github.com/jinzhu/gorm"
)

func deferTransaction(err error, tx *gorm.DB, desc string) error {
	if err != nil {
		tx.Rollback()
		return fmt.Errorf(desc+":%w", err)
	}
	r := recover()
	if r != nil {
		tx.Rollback()
		return fmt.Errorf(desc+":%w", r)
	}
	return nil
}
