package sql

import (
	"fmt"
	"log"

	"github.com/jinzhu/gorm"

	"bjyixiu.com/ent/models"
	"github.com/pkg/errors"
)

// 新建terminal端
// Params:
//		key: terminal key(unique)
//		name: terminal name
// Return:
// 		error: 错误返回
func (sql *SQLService) CreateTerminal(key, name string) error {
	exist, err := sql.terminalExist(key)
	if err != nil {
		return errors.Wrap(err, "增加terminal")
	}
	if exist {
		return errors.New("Terminal Key已经存在")
	}
	// 新增
	if err := sql.setTerminal(key, name); err != nil {
		return errors.Wrap(err, "增加terminal")
	}
	return nil
}

// 修改terminal端
// Params:
//		key: terminal key(unique)
//		name: terminal name
// Return:
// 		error: 错误返回
func (sql *SQLService) UpdateTerminal(key, name string) error {
	// 检查是否已经存在key值
	exist, err := sql.terminalExist(key)
	if err != nil {
		return errors.Wrap(err, "修改terminal")
	}
	if !exist {
		return errors.New("terminal key不存在")
	}
	terminal := models.Terminal{
		Key:  key,
		Name: name,
	}

	// 修改
	if err := sql.db.Model(&terminal).Update("name", name).Error; err != nil {
		return fmt.Errorf("修改terminal名称出错: %w", err)
	}
	return nil
}

// 增加/更新terminal端，如果key值不存在则新增
// Params:
//		key: terminal key(unique)
//		name: terminal name
// Return:
// 		error: 错误返回
func (sql *SQLService) setTerminal(key, name string) error {
	terminal := models.Terminal{
		Key:  key,
		Name: name,
	}
	if err := sql.db.Create(&terminal).Error; err != nil {
		return errors.Wrap(err, "增加/修改terminal出错")
	}
	return nil
}

// 检查terminal是否存在
// Params:
//		key: terminal key(unique)
// Return:
//		exist：存在-true，不存在-false
// 		error: 错误返回
func (sql *SQLService) terminalExist(key string) (bool, error) {
	// 检查是否已经存在Key值
	var count int64
	if err := sql.db.Model(&models.Terminal{}).Where("ID = ?", key).Count(&count).Error; err != nil {
		return false, errors.Wrap(err, "增加terminal，检查是否有重复key出错")
	}

	// 存在返回错误
	log.Println("count", count)
	if count > 0 {
		return true, nil
	}

	return false, nil
}

// 删除（物理）terminal，如果有母树引用则不运行删除，需要先删除母树
// Params:
//		key: terminal key(unique)
// Return:
// 		error: 错误返回
func (sql *SQLService) DelTerminal(key string) error {
	// 判断是否有母树引用
	exist, err := sql.TerminalTreeExist(key)
	if err != nil {
		return fmt.Errorf("删除Terminal: %w", err)
	}
	if exist {
		return errors.New("Terminal下还有树关联，请先删除树")
	}

	if len(key) == 0 {
		return errors.New("要删除的Terminal Key为空")
	}
	// 无，删除
	if err := sql.db.Delete(&models.Terminal{Key: key}).Error; err != nil {
		return fmt.Errorf("删除terminal出错 : %w", err)
	}
	return nil
}

// 获取所有端
// Params:
//		none
// Return:
//		terminals: 所有终端数组
// 		error: 错误返回
func (sql *SQLService) GetAllTerminal() ([]models.Terminal, error) {
	var terminals []models.Terminal
	if err := sql.db.Find(&terminals).Error; err != nil {
		return terminals, errors.Wrap(err, "获取所有端")
	}

	return terminals, nil
}

// 获取指定端
// Params:
//		terminalKey : 指定端key
// Return:
//		terminal: 终端
// 		error: 错误返回
func (sql SQLService) GetTerminalByKey(key string) (*models.Terminal, error) {
	var terminal models.Terminal
	if err := sql.db.Take(&terminal, "id = ?", key).Error; err != nil {
		return nil, fmt.Errorf("获取端信息出错:%w", err)
	}
	return &terminal, nil
}

// 获取指定端下所有树(信息)
// Params:
//		key: terminal key
// Return:
//		trees: 树列表
// 		error: 错误返回
func (sql *SQLService) GetTreesByTerminal(key string) ([]*models.Tree, error) {
	//var tree models.Tree
	//if err := sql.db.First(&tree).Error; err != nil{
	//	return nil,
	//}
	return nil, nil
}

// 指定端下是否还有树关联
// Params:
//		key: terminal key
// Return:
//		exist: 树存在
// 		error: 错误返回
func (sql *SQLService) TerminalTreeExist(terminal string) (bool, error) {
	var tree models.Tree
	if err := sql.db.First(&tree, "terminal = ?", terminal).Error; err != nil {
		if gorm.IsRecordNotFoundError(err) {
			return false, nil
		}
		return false, fmt.Errorf("检查端-树关联出错: %w", err)
	}
	if tree.ID > 0 {
		return true, nil
	}
	return false, nil
}
