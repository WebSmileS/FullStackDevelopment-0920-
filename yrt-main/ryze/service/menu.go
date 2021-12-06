package service

import (
	"fmt"
	"strconv"
	"sync"

	"bjyixiu.com/ryze/models"
	"github.com/pkg/errors"
)

// 检查母树是否存在
func CheckMotherTree() error {
	// 检查平台树是否存在
	exit, err := kv.Exist(models.KEY_PLATFORM_TREE)
	if err != nil {
		return errors.Wrap(err, "检查平台树出错")
	}
	if !exit {
		if err := CreateMotherTree(models.KEY_PLATFORM_TREE, "平台菜单"); err != nil {
			return errors.Wrap(err, "初始化建立平台树出错")
		}

	}
	// 检查医院树是否存在
	exit, err = kv.Exist(models.KEY_HOSPITAL_TREE)
	if err != nil {
		return errors.Wrap(err, "检查医院树出错")
	}
	if !exit {
		if err = CreateMotherTree(models.KEY_HOSPITAL_TREE, "医院菜单"); err != nil {
			return errors.Wrap(err, "初始化建立医院树出错")
		}

	}
	// 检查厂家树是否存在
	exit, err = kv.Exist(models.KEY_FACTORY_TREE)
	if err != nil {
		return errors.Wrap(err, "检查厂家树出错")
	}
	if !exit {
		if err = CreateMotherTree(models.KEY_FACTORY_TREE, "厂家菜单"); err != nil {
			return errors.Wrap(err, "初始化建立厂家树出错")
		}

	}
	// 检查经销商树是否存在
	exit, err = kv.Exist(models.KEY_DEALER_TREE)
	if err != nil {
		return errors.Wrap(err, "检查经销商树出错")
	}
	if !exit {
		if err = CreateMotherTree(models.KEY_DEALER_TREE, "经销商菜单"); err != nil {
			return errors.Wrap(err, "初始化建立经销商树出错")
		}

	}
	// 检查维修商树是否存在
	return nil
}

// 获取固定角色列表
func GetSystemManagerRoles() []models.SystemRole {
	// 建立为普通角色，方便前端复用，有需要再更改处理方法
	return []models.SystemRole{
		models.SystemRole{
			Role_inner_sn: models.KeyManagerTree(models.TYPE_HOSPITAL),
			Name:          "医院系统管理员",
			System_type:   models.TYPE_HOSPITAL,
			Belong:        0,
			Status:        0,
		},
		models.SystemRole{
			Role_inner_sn: models.KeyManagerTree(models.TYPE_DEALER),
			Name:          "经销商系统管理员",
			System_type:   models.TYPE_DEALER,
			Belong:        0,
			Status:        0,
		},
		models.SystemRole{
			Role_inner_sn: models.KeyManagerTree(models.TYPE_FACTORY),
			Name:          "厂商系统管理员",
			System_type:   models.TYPE_FACTORY,
			Belong:        0,
			Status:        0,
		},
	}
}

// 设定系统固定角色权限树
func UpdateSystemManagerRole() {

}

//
//func CheckRole() error {
//	// 检查医院
//	hostpitalManagerKey := models.KeyManagerTree(models.TYPE_HOSPITAL)
//	exist, err := kv.Exist(hostpitalManagerKey)
//	if err != nil {
//		return errors.Wrap(err, "检查医院管理角色出错")
//	}
//	if !exist {
//		//	TODO 建立固定角色
//
//	}
//
//	// 检查经销商
//	dealerManagerKey := models.KeyManagerTree(models.TYPE_DEALER)
//	// 检查厂商
//	factoryManagerKey := models.KeyManagerTree(models.TYPE_FACTORY)
//	return nil
//}

func CreateMotherTree(rootID string, name string) error {
	// 平台母树
	rootNode := models.Node{
		ID:     rootID,
		Type:   models.TYPE_NODE_CAT,
		Parent: "",
		Name:   name,
		Icon:   "",
		URI:    "/",
		State:  models.STATE_NODE_ENABLE,
	}

	if err := kv.HMSetAll(&rootNode, rootID); err != nil {
		return errors.Wrap(err, "建立菜单树根节点出错")
	}
	return nil
}

// 根据ID获得节点
func GetNodeByID(id string) (*models.Node, error) {
	var n models.Node
	//n := new(models.Node)
	n.ID = id
	// var err error

	var ns []*models.Node
	ns = append(ns, &n)

	if err := kv.HMGet(&ns, n.ID, "Parent", "Type", "Name", "Icon", "URI", "State"); err != nil {
		return nil, errors.Wrap(err, "获取节点出错")
	}

	// 子节点ID
	if err := kv.HGetToJSON(n.ID, "ChildrenIDs", &n.ChildrenIDs); err != nil {
		return nil, errors.Wrap(err, "获取子节点错误")
	}
	return ns[0], nil
}

// 根据ID获取节点，包括Feature
func GetNodeDetailByID(id string) (*models.Node, error) {
	// var n models.Node
	n := new(models.Node)
	n.ID = id
	// var err error

	var ns []*models.Node
	ns = append(ns, n)

	if err := kv.HMGet(&ns, n.ID, "Parent", "Type", "Name", "Icon", "URI", "State"); err != nil {
		return nil, errors.Wrap(err, "获取节点出错")
	}

	// 子节点ID
	if err := kv.HGetToJSON(n.ID, "ChildrenIDs", &n.ChildrenIDs); err != nil {
		return nil, errors.Wrap(err, "获取子节点错误")
	}

	//// Feature
	//if err := kv.HGetToJSON(n.ID, "FeatureIDs", &n.FeatureIDs); err != nil {
	//	return nil, errors.Wrap(err, "获取子节点FeatureID出错")
	//}

	// 获取所有Feaure详细
	var err error
	n.Features, err = GetFeaturesByID(n.ID)
	if err != nil {
		return nil, errors.Wrap(err, "获取子节点Feature错误")
	}
	//
	//for _, fid := range n.FeatureIDs {
	//	feature, err := GetFeatureByID(fid)
	//	if err != nil {
	//		return nil, errors.Wrap(err, "获取子节点Feature详细出错")
	//	}
	//	n.Features = append(n.Features, feature)
	//}

	return ns[0], nil

}

// 启用Node
func EnableNode(node *models.Node) error {
	// 获取父节点，如果不存在则修改本节点后返回
	if len(node.Parent) > 0 {
		parent, err := GetNodeByID(node.Parent)
		if err != nil {
			return errors.Wrap(err, "启用节点,获取父节点出错")
		}
		if err := EnableNode(parent); err != nil {
			return errors.Wrap(err, "启用节点,启用父节点出错")
		}
	}
	if err := kv.HSet(node.ID, "State", models.STATE_NODE_ENABLE); err != nil {
		return errors.Wrap(err, "启用节点出错")
	}
	return nil
}

// 禁用Node
func DisableNode(node *models.Node) error {
	// 获取子节点，禁用所有子节点
	if len(node.ChildrenIDs) > 0 {
		for _, id := range node.ChildrenIDs {
			children, err := GetNodeByID(id)
			if err != nil {
				return errors.Wrap(err, "禁用节点，获取子节点出错")
			}
			if err := DisableNode(children); err != nil {
				return errors.Wrap(err, "禁用节点，禁用子节点出错")
			}
		}
	}

	if err := kv.HSet(node.ID, "State", models.STATE_NODE_DISABLE); err != nil {
		return errors.Wrap(err, "禁用节点出错")
	}

	return nil
}

// 启用所有子节点
func EnableChildrenNode(node *models.Node) error {
	// 获取子节点，启用所有子节点
	if len(node.ChildrenIDs) > 0 {
		for _, id := range node.ChildrenIDs {
			children, err := GetNodeByID(id)
			if err != nil {
				return errors.Wrap(err, "启用节点，获取子节点出错")
			}
			if err := EnableChildrenNode(children); err != nil {
				return errors.Wrap(err, "启用节点，启用子节点出错")
			}
		}
	}

	if err := kv.HSet(node.ID, "State", models.STATE_NODE_ENABLE); err != nil {
		return errors.Wrap(err, "启用节点出错")
	}

	return nil
}

// 获得所有完整菜单
// controller层进行限制，平台理论上可以获得所有母树，但是机构最高只能获得对应的Tree
func AllMotherTree() ([]*models.Node, error) {
	tree := make([]*models.Node, 4)

	var wg sync.WaitGroup
	wg.Add(4)
	// 平台
	go func() {
		t, err := MotherTree(models.KEY_PLATFORM_TREE, true, models.TYPE_PLATFORM)
		if err != nil {
			fmt.Println("获取平台母树错误", err)
			return
		}
		tree[models.TYPE_PLATFORM] = t
		wg.Done()
	}()
	// 医院
	go func() {
		t, err := MotherTree(models.KEY_HOSPITAL_TREE, true, models.TYPE_HOSPITAL)
		if err != nil {
			fmt.Println("获取医院母树错误", err)
			return
		}
		tree[models.TYPE_HOSPITAL] = t
		wg.Done()
	}()
	// 厂商
	go func() {
		t, err := MotherTree(models.KEY_FACTORY_TREE, true, models.TYPE_FACTORY)
		if err != nil {
			fmt.Println("获取厂商母树错误", err)
			return
		}
		tree[models.TYPE_FACTORY] = t
		wg.Done()
	}()
	// 经销商
	go func() {
		t, err := MotherTree(models.KEY_DEALER_TREE, true, models.TYPE_DEALER)
		if err != nil {
			fmt.Println("获取经销商母树错误", err)

		}
		tree[models.TYPE_DEALER] = t
		wg.Done()
	}()
	wg.Wait()
	// 拿子节点
	return tree, nil
}

func MotherTree(rootID string, includeDisable bool, index int) (*models.Node, error) {
	rootNode, err := GetNodeByID(rootID)
	if err != nil {
		return nil, err
	}
	rootNode.Index = strconv.Itoa(index)
	// 拿子节点
	GetChildrenNode(rootNode, includeDisable)
	return rootNode, nil
}

//func PlatformMotherTree() (*models.Node, error) {
//	rootNode, err := GetNodeByID(models.KEY_PLATFORM_TREE)
//	if err != nil {
//		return nil, err
//	}
//	rootNode.Index = "0"
//	// 拿子节点
//	GetChildrenNode(rootNode)
//	return rootNode, nil
//}

func GetChildrenIDs(id string) ([]string, error) {

	var childrenIDs []string // 子节点ID
	if len(id) <= 0 {
		return childrenIDs, errors.New("节点ID不能为空")
	}

	if err := kv.HGetToJSON(id, "ChildrenIDs", &childrenIDs); err != nil {
		return childrenIDs, errors.Wrap(err, "获取子节点错误")
	}

	return childrenIDs, nil
}

//func GetChildrenNode(n *models.Node, includeDisable bool) {
//	size := len(n.ChildrenIDs)
//	n.Children = make([]*models.Node, size)
//	if size > 0 {
//		var wg sync.WaitGroup
//		wg.Add(size)
//		for i := 0; i < size; i++ {
//			id := n.ChildrenIDs[i]
//			index := i
//			go func() {
//				childrenNode, err := GetNodeByID(id)
//
//				if !includeDisable {
//					if childrenNode.State == models.STATE_NODE_DISABLE {
//						wg.Done()
//						return
//					}
//				}
//
//				if err != nil {
//					fmt.Println("获取节点出错：", err)
//					return
//				}
//				childrenNode.Index = n.Index + "." + strconv.Itoa(index)
//				GetChildrenNode(childrenNode, includeDisable)
//
//				n.Children[index] = childrenNode
//				wg.Done()
//			}()
//		}
//		wg.Wait()
//	}
//}

func GetChildrenNode(n *models.Node, includeDisable bool) {
	size := len(n.ChildrenIDs)
	n.Children = make([]*models.Node, 0)
	if size > 0 {
		index := 0 // 给前端的索引值
		for i := 0; i < size; i++ {
			id := n.ChildrenIDs[i]
			childrenNode, err := GetNodeByID(id)

			if err != nil {
				fmt.Println("获取节点出错：", err)
				return
			}
			if !includeDisable {
				if childrenNode.State == models.STATE_NODE_DISABLE {
					continue
				}
			}
			childrenNode.Index = n.Index + "." + strconv.Itoa(index)
			index++
			GetChildrenNode(childrenNode, includeDisable)
			n.Children = append(n.Children, childrenNode)
		}
	}
}

func GetRoleTreeByID(tid string) ([]*models.Node, error) {
	//	读取角色树
	menus := make([]models.RoleTreeNode, 0)
	if err := kv.HGetToJSON(tid, "tree", &menus); err != nil {
		return nil, errors.Wrap(err, "读取角色树出错")
	}
	//  TODO 遇到母树没有的节点则从当前树中删除
	return GetRoleNode(menus, "0"), nil
}

func GetRoleTree(rid int) ([]*models.Node, error) {
	//	读取角色树
	menus := make([]models.RoleTreeNode, 0)
	if err := kv.HGetToJSON(models.KeyRoleTree(rid), "tree", &menus); err != nil {
		return nil, errors.Wrap(err, "读取角色树出错")
	}

	//  TODO 遇到母树没有的节点则从当前树中删除
	return GetRoleNode(menus, "0"), nil
}

func GetRoleTreeIDs(rid int) ([]models.RoleTreeNode, error) {
	//	读取角色树
	return GetRoleTreeIDsByID(models.KeyRoleTree(rid))

	//idsTree := make([]models.RoleTreeNode, 0)
	//if err := kv.HGetToJSON(models.KeyRoleTree(rid), "tree", &idsTree); err != nil {
	//	return nil, errors.Wrap(err, "读取角色树出错")
	//}
	//return idsTree, nil
}

func GetRoleTreeIDsByID(tid string) ([]models.RoleTreeNode, error) {
	//	读取角色树
	idsTree := make([]models.RoleTreeNode, 0)
	if err := kv.HGetToJSON(tid, "tree", &idsTree); err != nil {
		return nil, errors.Wrap(err, "读取角色树出错")
	}
	return idsTree, nil
}

// 并发版本
//func GetRoleNode(roleNodes []models.RoleTreeNode, parentIndex string) []*models.Node {
//	size := len(roleNodes)
//	children := make([]*models.Node, size)
//	if size > 0 {
//		var wg sync.WaitGroup
//		wg.Add(size)
//		for i := 0; i < size; i++ {
//			id := roleNodes[i].ID
//			index := i
//			go func() {
//				menuNode, err := GetNodeByID(id)
//				if err != nil {
//					// TODO 节点不存在的（被删除），从role children中删除
//					fmt.Println("获取节点出错：", err)
//					return
//				}
//				// TODO 还需要取出Feature
//				menuNode.Index = parentIndex + "." + strconv.Itoa(index)
//				menuNode.Children = GetRoleNode(roleNodes[index].ChildrenIDs, menuNode.Index)
//				children[index] = menuNode
//				wg.Done()
//			}()
//		}
//		wg.Wait()
//	}
//	return children
//}

func GetRoleNode(roleNodes []models.RoleTreeNode, parentIndex string) []*models.Node {
	size := len(roleNodes)
	children := make([]*models.Node, 0)
	if size > 0 {
		index := 0
		for i := 0; i < size; i++ {
			id := roleNodes[i].ID
			menuNode, err := GetNodeByID(id)
			if err != nil {
				// TODO 节点不存在的（被删除），从role children中删除
				fmt.Println("获取节点出错：", err)
				continue
			}
			// 过滤被禁用的节点
			if menuNode.State == models.STATE_NODE_DISABLE {
				continue
			}

			// TODO 还需要取出Feature
			menuNode.Index = parentIndex + "." + strconv.Itoa(index)
			index++
			menuNode.Children = GetRoleNode(roleNodes[i].ChildrenIDs, menuNode.Index)
			children = append(children, menuNode)
		}
	}
	return children
}

// 增加分类
func AddCatalog(catalog *models.AddCatalog) (string, error) {
	n := &models.AddNode{
		Icon: catalog.Icon,
		Name: catalog.Name,
		Type: models.TYPE_NODE_CAT,
	}
	return AddNode(n)
}

// 增加菜单
func AddLeaf(leaf *models.AddLeaf) (string, error) {
	n := &models.AddNode{
		Icon: leaf.Icon,
		Name: leaf.Name,
		Type: models.TYPE_NODE_LEAF,
		URI:  leaf.URI,
	}
	return AddNode(n)
}

//增加节点
func AddNode(n *models.AddNode) (string, error) {
	// 检查父节点，存在则准备向父节点加入子节点ID

	parent, err := GetNodeByID(n.Parent)
	if err != nil {
		return "", errors.Wrap(err, "增加节点出错，检查父节点出错")
	}
	if len(parent.ID) == 0 {
		return "", errors.New("父节点不存在")
	}

	// 增加菜单或分类必须是分类
	if parent.Type != models.TYPE_NODE_CAT {
		return "", errors.New("只能在分类下增加子项")
	}

	if err := kv.HGetToJSON(n.Parent, "ChildrenIDs", &parent.ChildrenIDs); err != nil {
		return "", err
	}
	// abandoned 不需要检查
	// 检查父节点中是否已经存在同名

	// 生成新ID
	id := models.NewID()

	node := models.Node{
		ID:     id,
		Type:   n.Type,
		Parent: n.Parent,
		Name:   n.Name,
		Icon:   n.Icon,
		URI:    n.URI,
		State:  n.State,
	}

	// Node状态
	//if n.State {
	//	node.State = models.STATE_NODE_ENABLE
	//} else {
	//	node.State = models.STATE_NODE_DISABLE
	//}

	if err := UpdateFeatures(node.ID, n.Features); err != nil {
		return "", errors.Wrap(err, "增加Node时，增加Features错误")
	}

	//// 增加Feature
	//featureIDs := make([]string, 0)
	//for _, f := range n.Features {
	//	if err := AddFeature(id, f); err != nil {
	//		return "", errors.Wrap(err, "增加Node的Feature出错")
	//	}
	//	featureIDs = append(featureIDs, f.ID)
	//}
	//
	// 存入新节点
	if err := kv.HMSetAll(&node, id); err != nil {
		return "", err
	}
	//
	//// 存入FeatureID
	//if len(featureIDs) > 0 {
	//	if err := kv.HSetJSON(id, "Features", featureIDs); err != nil {
	//		return "", errors.Wrap(err, "增加Node的FeatureID出错")
	//	}
	//}

	// 更新父节点children列表
	parent.ChildrenIDs = append(parent.ChildrenIDs, id)

	return id, kv.HSetJSON(n.Parent, "ChildrenIDs", parent.ChildrenIDs)
}

// 删除节点
func RemoveNode(id string) error {
	// 检查Node是否存在
	node, err := GetNodeByID(id)
	if err != nil {
		return errors.Wrap(err, "没找到对应的节点")
	}
	// 检查Node下是否还有子节点，如有则返回错误，不允许删除
	if len(node.ChildrenIDs) > 0 {
		return errors.New("该节点下还有子节点，如要删除，请先删除子节点")
	}
	// 检查Node下所有Feature
	// 删除Node下Feature
	// 删除Node
	if err := kv.Del(id); err != nil {
		return errors.Wrap(err, "删除节点失败")
	}
	// 在Parent中删除Node
	parent, err := GetNodeByID(node.Parent)
	if err != nil {
		return errors.Wrap(err, "该节点没有父节点")
	}

	size := len(parent.ChildrenIDs)
	if size == 1 && parent.ChildrenIDs[0] == id {
		parent.ChildrenIDs = []string{}
	} else {
		for i := 0; i < size; i++ {
			if parent.ChildrenIDs[i] == id {
				parent.ChildrenIDs = append(parent.ChildrenIDs[:i], parent.ChildrenIDs[i+1:]...)
				break
			}
		}
	}

	if err := kv.HSetJSON(parent.ID, "ChildrenIDs", parent.ChildrenIDs); err != nil {
		return errors.Wrap(err, "从父节点中删除子节点失败")
	}

	return nil
}

// 可以更新节点的Name, URI, Icon
func UpdateNode(n *models.UpdateNode) error {
	// 检查节点，获取节点
	node, err := GetNodeByID(n.ID)
	if err != nil {
		return errors.Wrap(err, "没找到对应的节点")
	}

	// 修改对应的字段
	values := make(map[string]string)

	values["Name"] = n.Name
	values["Icon"] = n.Icon
	values["URI"] = n.URI

	if err := kv.HMSet(node.ID, values); err != nil {
		return errors.Wrap(err, "更改节点属性错误")
	}

	if n.State == models.STATE_NODE_ENABLE {
		if err := EnableNode(node); err != nil {
			return errors.Wrap(err, "启用节点错误")
		}
	} else {
		if err := DisableNode(node); err != nil {
			return errors.Wrap(err, "禁用节点错误")
		}
	}

	//更新Feature
	if err := UpdateFeatures(n.ID, n.Features); err != nil {
		return errors.Wrap(err, "更新")
	}

	////查找增加和删除的列表
	//old := make([]string, 0)
	//if err := kv.HGetToJSON(n.ID, "FeatureIDs", &old); err != nil {
	//	return errors.Wrap(err, "获取节点FeaturesID出错")
	//}
	//
	//// 确定新增和修改的
	//add := make([]*models.Feature, 0)
	//update := make([]string, 0)
	//for _, f := range n.Features {
	//	if len(f.ID) == 0 {
	//		add = append(add, f)
	//		if err := AddFeature(n.ID, f); err != nil {
	//			return errors.Wrap(err, "增加节点Feature出错")
	//		}
	//	} else {
	//		update = append(update, f.ID)
	//		if err := UpdateFeature(f); err != nil {
	//			return errors.Wrap(err, "更新节点Feature出错")
	//		}
	//	}
	//}
	//// 要删除的
	//_, remove := diffSlice(update, old)
	//if len(remove) > 0 {
	//	for _, id := range remove {
	//		if err := RemoveFeature(n.ID, id); err != nil {
	//			return errors.Wrap(err, "更新节点Feature删除出错")
	//		}
	//	}
	//}

	// 返回
	return nil
}

// 修改菜单子节点的顺序
func UpdateChildrenOrder(n *models.UpdateChildren) error {
	return nil
}

// 菜单子节点下降
func ChildrenMoveDown(n *models.UpdateChildrenOrder) error {
	// 获取节点
	childrenIDs, err := GetChildrenIDs(n.ID)
	if err != nil {
		return errors.Wrap(err, "子节点排序下降，获取子节点错误")
	}

	// 只有一个节点则不用操作
	if len(childrenIDs) == 1 {
		return errors.New("只有一个节点")
	}

	// 查找子节点
	for i := 0; i < len(childrenIDs); i++ {
		if n.ChildrenID == childrenIDs[i] {
			// 调整顺序
			if i == len(childrenIDs)-1 {
				return errors.New("已经是最后一节点")
			}
			id := childrenIDs[i]
			childrenIDs[i] = childrenIDs[i+1]
			childrenIDs[i+1] = id

			// 存入
			return kv.HSetJSON(n.ID, "ChildrenIDs", &childrenIDs)
		}
	}

	return errors.New("没找到对于的子节点")
}

// 子节点排序上升
func ChildrenMoveUp(n *models.UpdateChildrenOrder) error {
	// 获取节点
	childrenIDs, err := GetChildrenIDs(n.ID)
	if err != nil {
		return errors.Wrap(err, "子节点排序上升，获取子节点错误")
	}

	// 只有一个节点则不用操作
	if len(childrenIDs) == 1 {
		return errors.New("只有一个节点")
	}

	// 查找子节点
	for i := 0; i < len(childrenIDs); i++ {
		if n.ChildrenID == childrenIDs[i] {
			// 调整顺序
			if i == 0 {
				return errors.New("已经是第一个节点")
			}
			id := childrenIDs[i]
			childrenIDs[i] = childrenIDs[i-1]
			childrenIDs[i-1] = id

			// 存入
			return kv.HSetJSON(n.ID, "ChildrenIDs", &childrenIDs)
		}
	}

	return errors.New("没找到对应的子节点")
}

// 用户菜单
func UserMenu(info *models.RepUserInfo) ([]*models.Node, error) {

	var rootKey string
	switch info.UserInfo.System_type {
	case models.TYPE_PLATFORM:
		rootKey = models.KEY_PLATFORM_TREE
	case models.TYPE_HOSPITAL:
		rootKey = models.KEY_HOSPITAL_TREE
	case models.TYPE_DEALER:
		rootKey = models.KEY_DEALER_TREE
	case models.TYPE_FACTORY:
		rootKey = models.KEY_FACTORY_TREE
	default:
		return nil, errors.New("获取用户菜单出错，用户类型错误")
	}

	menu, err := MotherTree(rootKey, false, 0)
	if err != nil {
		return nil, errors.Wrap(err, "获取用户菜单出错")
	}

	return menu.Children, nil
}

// 机构所有菜单
func OrgMenu(systemType int) (*models.Node, error) {

	var rootKey string
	switch systemType {
	case models.TYPE_PLATFORM:
		rootKey = models.KEY_PLATFORM_TREE
	case models.TYPE_HOSPITAL:
		rootKey = models.KEY_HOSPITAL_TREE
	case models.TYPE_DEALER:
		rootKey = models.KEY_DEALER_TREE
	case models.TYPE_FACTORY:
		rootKey = models.KEY_FACTORY_TREE
	default:
		return nil, errors.New("获取机构菜单树出错，用户类型错误")
	}

	menu, err := MotherTree(rootKey, false, 0)
	if err != nil {
		return nil, errors.Wrap(err, "获取机构菜单树出错")
	}

	return menu, nil
}

// 新建Feature
func AddFeature(nodeID string, f *models.Feature) error {
	// 检查节点是否存在，节点ID为空则是无菜单功能，加入根节点
	// 检查节点下是否有同名的Feature, 有则警告，不允许加入 (在node检查)
	// 生成新的ID
	f.ID = models.NewID()
	f.NodeID = nodeID
	// 存入新功能值到节点中
	if err := kv.HMSetAll(f, f.ID); err != nil {
		return errors.Wrap(err, "数据库存储feature出错")
	}
	// 更新 API - Feature 对应表
	if err := kv.HSetJSON(f.ID, "APIs", f.APIs); err != nil {
		return errors.Wrap(err, "数据库存储feature apis出错")
	}

	//TODO 增加API到feature对应

	return nil
}

// 删除Feature
func RemoveFeature(nodeID string, featureID string) error {
	// 检查节点是否存在，并检查feature是否存在于该节点下
	featureIDs := make([]string, 0)
	if err := kv.HGetToJSON(nodeID, "FeatureIDs", featureIDs); err != nil {
		return errors.Wrap(err, "获取节点FeatureID出错")
	}

	tempFs := []string{nodeID}

	_, keep := diffSlice(tempFs, featureIDs)
	if err := kv.HSetJSON(nodeID, "FeatureIDs", keep); err != nil {
		return errors.Wrap(err, "更新节点FeatureID出错")
	}
	// 从API - Feature 对应表中删除 Feature
	feature, err := GetFeatureByID(featureID)
	if err != nil {
		return errors.Wrap(err, "获取featue出错")
	}
	DelMappingAPI2Feature(feature.APIs)

	if err := kv.Del(featureID); err != nil {
		return errors.Wrap(err, "删除Feature出错")
	}
	// 获取所有具有该Feature的Role，从Role的私有Tree中删除Feature
	// 删除 Feature - Role 对应表

	return nil
}

func GetFeaturesByID(nid string) (*models.Features, error) {
	//internalID, err := kv.HGet(fid, "ID")
	//if err != nil {
	//	return nil, errors.Wrap(err, "读取Features internal ID错误")
	//}

	internal, err := GetFeatureByID(models.KeyInternalFeatures(nid))
	if err != nil {
		return nil, errors.Wrap(err, "读取Features internal feature错误")
	}

	additionalIDs := make([]string, 0)
	if err := kv.HGetToJSON(models.KeyNodeFeatures(nid), "IDs", &additionalIDs); err != nil {
		return nil, errors.Wrap(err, "读取Features Additional出错")
	}

	additional := make([]*models.Feature, 0)
	for _, aid := range additionalIDs {
		feature, err := GetFeatureByID(aid)
		if err != nil {
			return nil, errors.Wrap(err, "读取Features Additional详细出错")
		}
		additional = append(additional, feature)
	}

	features := &models.Features{
		Internal:   internal,
		IDs:        additionalIDs,
		Additional: additional,
	}

	return features, nil
}

func UpdateFeatures(nid string, features *models.Features) error {
	// 添加 or 更新internal feature
	if features == nil {
		return nil
	}
	// 防止前端没有传回ID，internal feature ID是固定格式的
	features.Internal.ID = models.KeyInternalFeatures(nid)
	if err := UpdateFeature(features.Internal); err != nil {
		return errors.Wrap(err, "更新Features的internal feature 错误")
	}
	// 更新additional features
	addintalIDs := []string{}
	update := []string{}
	new := []string{}
	for _, f := range features.Additional {
		// 原有已有Feature 进行更新
		if len(f.ID) > 0 {
			update = append(update, f.ID)
		} else {
			// 没有feature ID值的需要设置为新建
			f.ID = models.NewID()
			f.NodeID = nid
			new = append(new, f.ID)
		}
		if err := UpdateFeature(f); err != nil {
			return errors.Wrap(err, "更新Features的additional feature 错误")
		}
		addintalIDs = append(addintalIDs, f.ID)
	}
	// 已有feature ID值的没有提交的要删除
	fsid := models.KeyNodeFeatures(nid)
	old := make([]string, 0)
	if err := kv.HGetToJSON(fsid, "Additional", old); err != nil {
		return errors.Wrap(err, "更新Features时获取additional列表出错")
	}

	_, delete := diffSlice(update, old)
	for _, fid := range delete {
		if err := DeleteFeature(fid); err != nil {
			return errors.Wrap(err, "更新Features时删除additional出错")
		}
	}

	if err := kv.HSetJSON(fsid, "IDs", addintalIDs); err != nil {
		return errors.Wrap(err, "更新Features时存储additional id list出错")
	}

	return nil
}

// 获取Feature
func GetFeatureByID(fid string) (*models.Feature, error) {
	feature := new(models.Feature)
	fs := []*models.Feature{}

	fs = append(fs, feature)

	exist, err := kv.Exist(fid)
	if err != nil {
		return nil, errors.Wrap(err, "判断获取Feature出错")
	}

	if !exist {
		return nil, nil
	}

	feature.ID = fid
	if err := kv.HMGet(&fs, fid, "Name", "Flag", "Desc"); err != nil {
		return nil, errors.Wrap(err, "数据库获取Feature出错")
	}
	// 获取api list
	if err := kv.HGetToJSON(feature.ID, "APIs", &feature.APIs); err != nil {
		return nil, errors.Wrap(err, "获取feature api出错")
	}
	return feature, nil
}

// 更新Feature
func UpdateFeature(newFeature *models.Feature) error {
	// 读取原值
	old, err := GetFeatureByID(newFeature.ID)
	if err != nil {
		return errors.Wrap(err, "更新feature出错")
	}

	// 比对API
	newList := newFeature.APIs
	oldList := []string{}
	if old != nil && len(old.APIs) > 0 {
		oldList = old.APIs
	}
	addList, delList := diffSlice(newList, oldList)

	// 提交API与Feature对应表进行更新
	AddMappingAPI2Feature(addList)
	DelMappingAPI2Feature(delList)

	// 更新feature
	if err := kv.HMSetAll(newFeature, newFeature.ID); err != nil {
		return errors.Wrap(err, "数据库更新feature出错")
	}
	// 更新 API - Feature 对应表
	if err := kv.HSetJSON(newFeature.ID, "APIs", newFeature.APIs); err != nil {
		return errors.Wrap(err, "数据库更新feature apis出错")
	}

	return nil
}

// 删除Feature
func DeleteFeature(fid string) error {
	return nil
}

// TODO：增加API与Feature对应关系
func AddMappingAPI2Feature(apis []string) {

}

// TODO：删除API与Feature对应关系
func DelMappingAPI2Feature(apis []string) {

}

// 对比string slice差异
func diffSlice(new, old []string) ([]string, []string) {

	for newIndex, n := range new {
		//found := false
		for oldIndex, o := range old {
			if n == o {
				new = append(new[:newIndex], new[newIndex+1:]...)
				old = append(old[:oldIndex], old[oldIndex+1:]...)
				break
			}
		}
	}
	// new：比原值多的(要增加的)， old: 新列表中没有的(要删除的)
	return new, old

}

// 修改（增加/删除）角色权限树
func UpdateRoleTreeNode(rid int, roleTree []*models.RoleTreeNode) error {
	// TODO 与原值判断,从Feature与Role对应中删除/增加角色对应
	// 直接存储
	key := models.KeyRoleTree(rid)
	if err := kv.HSetJSON(key, "tree", roleTree); err != nil {
		return errors.Wrap(err, "保存角色树出错")
	}
	return nil
}

// 修改（增加/删除）系统固定角色权限树
func UpdateSystemManagerRoleTree(key string, roleTree []*models.RoleTreeNode) error {
	// TODO 与原值判断,从Feature与Role对应中删除/增加角色对应
	// 直接存储
	if err := kv.HSetJSON(key, "tree", roleTree); err != nil {
		return errors.Wrap(err, "保存角色树出错")
	}
	return nil
}

// 删除角色权限树
func DeleteRoleTree(rid int) error {
	key := models.KeyRoleTree(rid)
	if err := kv.Del(key); err != nil {
		return errors.Wrap(err, "删除角色树出错")
	}
	return nil
}

// 从API - Feature 对应表中删除 Feature

// 获取所有具有改Feature的Role，从Role的私有Tree中删除Feature
// 删除 Feature - Role 对应表

// 在节点下删除

// 更新Feature

// 删除

// 导出菜单母树
// func ExportMotherTree() error {
// 	//// 获取所有菜单母树
// 	all, err := AllMotherTree()
// 	if err != nil {
// 		return errors.Wrap(err, "导出菜单树，读取所有母树出错")
// 	}

// 	for _, tree := range all{
// 		tree.Name
// 	}

// }

func ImportMotherTree() {

}

// 导出角色子树
func ExportRoleTree() {

}

func ImportRoleTree() {

}

// 导出组织子树
func ExportGroupTree() {

}

func ImportGroupTree() {

}
