/**
 * 判断菜单节点是否为叶子节点
 * 菜单节点对象的content属性值为非空字符串时，则该节点为叶子节点
 * @param {object} node 菜单节点
 */
function isLeafNode(node) {
    if (typeof node.content === 'string' && node.content !== '') {
        return true;
    }
    return false;
}

/**
 * 生成菜单map
 * 并非全部菜单节点，仅由菜单树的叶子节点构成
 * @param {Array<object>} userMenu
 */
export function createUserMenuMap(userMenu) {
    const userMenuMap = new Map();
    function recursion(nodes) {
        for (let i = 0, l = nodes.length; i < l; i++) {
            if (isLeafNode(nodes[i])) {
                userMenuMap.set(nodes[i].content, nodes[i]);
            }
            // 理论上叶子节点不应该有子节点，不需要继续遍历
            // 但实际使用时可以让一个中间节点有自己的路由内容，同时其还包含子路由
            if (nodes[i].children && nodes[i].children.length > 0) {
                recursion(nodes[i].children);
            }
        }
    }
    recursion(userMenu);
    return userMenuMap;
}

/**
 * 检测目标path在用户菜单中有无匹配项，有匹配项则返回匹配的菜单项，否则返回false
 * @param {string} path 目标path
 * @param {Map<string, object>} userMenuMap 用户菜单map
 */
export function isMatchedWithUserMenu(path, userMenuMap) {
    if (userMenuMap.has(path)) {
        return userMenuMap.get(path);
    }
    return false;
}
