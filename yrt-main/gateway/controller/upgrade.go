package controller

import (
	"net/http"

	"github.com/labstack/echo/v4"
)

func LinkRoleUser(c echo.Context) error {
	//	读取所有用户
	//users, err := ryzeService.GetAllUsers()
	//if err != nil {
	//	return fmt.Errorf("读取所有用户出错: %", err)
	//}
	//// 已升级的角色索引
	//upgradedRoles := make(map[string]bool)
	////	建立角色-用户对应
	//for _, user := range users {
	//	if user.Type != ryzeModels.USER_TYPE_ADMIN {
	//		groupID := user.GroupKey()
	//		userID := strconv.FormatInt(user.User_inner_sn, 10)
	//		if len(userID) == 0 {
	//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, "用户ID未找到"))
	//		}
	//		//	读取角色
	//		roleID, err := ent.GetUserRoleKey(userID)
	//		if err != nil {
	//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//		}
	//		if len(roleID) == 0 {
	//			continue
	//		}
	//
	//		if err := ent.LinkRoleUser(roleID, userID); err != nil {
	//			return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//		}
	//
	//		//  检查是否已经处理过角色
	//		if _, ok := upgradedRoles[roleID]; !ok {
	//			if err := LinkNode2Role(roleID, groupID); err != nil {
	//				return c.JSON(http.StatusOK, protocol.ErrMsg(protocol.CODE_ERR_DB_FAILED, protocol.FlatErrMeg(err)))
	//			}
	//		}
	//
	//		//	标记已经处理
	//		upgradedRoles[roleID] = true
	//	}
	//
	//}
	return c.String(http.StatusOK, "OK")
}

func LinkNode2Role(roleID, groupID string) error {
	//	读取角色
	//trees, err := ent.GetRoleTreesIndex(roleID)
	//if err != nil {
	//	return fmt.Errorf("读取角色所有树出错: %w", err)
	//}
	//for _, tree := range trees {
	//	var oldTree []models.NodeIndex
	//	if err := ent.UpdateNodeRoleLink(roleID, groupID, oldTree, tree.Tree); err != nil {
	//		return fmt.Errorf("设置节点-角色联结出错: %w", err)
	//	}
	//}

	return nil
}
