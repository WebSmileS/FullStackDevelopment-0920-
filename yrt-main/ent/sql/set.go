package sql

import mapset "github.com/deckarep/golang-set"

// 比对两个权限数组的值，返回新增加的权限和要删除的权限数组
// Params:
//		new: 新权限数组
//		old: 旧权限数组
// Return:
//		add: 新增加的
//		abandoned: 删除的
func diff(new, old []int) (add []int, abandoned []int) {
	//oldSet := mapset.NewSetFromSlice((old).(interface))
	oldSet := mapset.NewSet()
	for _, oldID := range old {
		oldSet.Add(oldID)
	}

	for _, id := range new {
		if oldSet.Contains(id) {
			oldSet.Remove(id)
		} else {
			add = append(add, id)
		}
	}
	for id := range oldSet.Iter() {
		abandoned = append(abandoned, id.(int))
	}

	return
}
