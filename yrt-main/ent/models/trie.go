package models

type nodeType int

const (
	static   nodeType = iota // 普通节点，默认
	root                     // 根节点
	param                    // 参数路由，比如 /user/:id
	catchAll                 // 匹配所有内容的路由，比如 /article/*key
)

type TNode struct {
	ID        string
	path      string
	wildChild bool
	tType     nodeType
	maxParams uint8
	indices   string
	children  []*TNode
	priority  uint32
}
