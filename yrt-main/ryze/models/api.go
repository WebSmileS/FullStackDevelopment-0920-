package models

//type ApiNode struct {
//	// 节点ID
//	ID string
//	// 节点路径，比如上面的s，earch，和upport
//	path string
//	// 节点是否是参数节点，比如上面的:post
//	wildChild bool
//	// 节点类型，包括static, root, param, catchAll
//	// static: 静态节点，比如上面的s，earch等节点
//	// root: 树的根节点
//	// catchAll: 有*匹配的节点
//	// param: 参数节点
//	nType nodeType
//	// 路径上最大参数个数
//	maxParams uint8
//	// 和children字段对应, 保存的是分裂的分支的第一个字符
//	// 例如search和support, 那么s节点的indices对应的"eu"
//	// 代表有两个分支, 分支的首字母分别是e和u
//	indices string
//	// 儿子节点
//	children []*ApiNode
//	// 处理函数
//	handlers HandlersChain
//	// 优先级，子节点注册的handler数量
//	priority int
//
//	Roles []string
//}
