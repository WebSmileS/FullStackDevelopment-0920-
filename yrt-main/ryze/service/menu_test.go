package service

import (
	"fmt"
	"log"
	"testing"
	"time"

	"bjyixiu.com/ryze/models"
)

func SetDB() {
	InitService()
}

func Test_CheckMotherTree(t *testing.T) {
	SetDB()
	CheckMotherTree()
	since := time.Now()
	platformTree, err := PlatformMotherTree()
	fmt.Println(time.Now().Sub(since))
	if err != nil {
		t.Fatal("获取平台树错误:", err)
	}
	if len(platformTree.ID) == 0 {
		t.Fatal("平台树没有建立！")
	}
	t.Log("获取平台树成功：", platformTree.ID)
}

func Test_AddPlatformNode(t *testing.T) {
	SetDB()
	platformTree, err := PlatformMotherTree()
	if err != nil {
		t.Fatal("获取平台树错误:", err)
	}
	since := time.Now()
	// 第一级增加一个node
	var n1 models.AddNode
	n1.Parent = platformTree.ID
	n1.Name = time.Now().Format("0102T15:04:05")
	n1.URI = "/n1"
	var n1ID string
	if n1ID, err = AddNode(&n1); err != nil {
		t.Fatal("增加节点错误")
	}
	// 第二级增加一个node
	var n2 models.AddNode
	n2.Parent = n1ID
	n2.Name = time.Now().Format("0102T15:04:05")
	n2.URI = "/n1/n2"
	var n2ID string
	if n2ID, err = AddNode(&n2); err != nil {
		t.Fatal("增加节点错误")
	}
	fmt.Println(time.Now().Sub(since))
	// 查询平台树，验证节点
	platformTree, err = PlatformMotherTree()
	if err != nil {
		t.Fatal("获取平台树错误:", err)
	}
	fmt.Println(time.Now().Sub(since))
	newN1 := platformTree.Children[len(platformTree.Children)-1]
	if newN1.ID != n1ID {
		t.Fatal("插入N1后验证失败", newN1.ID, n1ID)
	}
	newN2 := newN1.Children[len(newN1.Children)-1]
	if newN2.ID != n2ID {
		t.Fatal("插入N2错误")
	}
	fmt.Println(len(platformTree.ChildrenIDs))
	fmt.Println(len(platformTree.Children))
	fmt.Println(time.Now().Sub(since))
	fmt.Println(platformTree)
}

func Test_DelNode(t *testing.T) {
	// 获得所有母树
	// 随机删除母树上的一个节点
}

func Test_UpdateNode(t *testing.T) {

}

func Benchmark_CheckMotherTree(b *testing.B) {
	b.StopTimer()
	SetDB()
	b.StartTimer()
	for i := 0; i < b.N; i++ {
		platformTree, err := PlatformMotherTree()
		if err != nil {
			log.Fatal("获取平台树错误:", err)
		}
		if len(platformTree.ID) == 0 {
			log.Fatal("平台树没有建立！")
		}
	}
}
