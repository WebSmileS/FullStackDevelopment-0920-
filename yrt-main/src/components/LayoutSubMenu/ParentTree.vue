<template>
    <a-menu
        :inlineCollapsed="collapsed"
        :openKeys="openKeys"
        v-model="selectedKeys"
        @select="handleSelectMenuItem"
        @openChange="handleOpenMenu"
        mode="inline"
        theme="light"
    >
        <template v-for="item in userMenu">
            <a-menu-item v-if="item.children && !item.children.length" :key="String(item.id)">
                <i :class="item.runes" class="yrt"></i>
                <span>{{ item.name }}</span>
            </a-menu-item>
            <sub-menu v-else :menu-info="item" :key="String(item.id)" />
        </template>
    </a-menu>
</template>

<script>
import SubMenu from '@/components/LayoutSubMenu/SubTree';

export default {
    name: 'ParentTree',
    props: ['collapsed'],
    data() {
        return {
            selectedKeys: [],
            openKeys: []
        };
    },
    components: {
        SubMenu
    },
    computed: {
        userMenu() {
            return this.$store.state.userMenu;
        },
        userMenuMap() {
            return this.$store.getters.userMenuMap;
        }
    },
    methods: {
        // 选中菜单项
        handleSelectMenuItem(menuItem) {
            const targetKey = menuItem.key;
            const { userMenuMap } = this;
            for (let [content, value] of userMenuMap) {
                if (String(value.id) === targetKey) {
                    this.$router.push(content);
                    this.selectedKeys = [targetKey];
                    break;
                }
            }
        },
        // 展开菜单项
        handleOpenMenu(keys) {
            this.openKeys = keys;
        },
        // 显示第一个菜单项（菜单树的第一个叶子节点）对应的页面
        showFirstPage() {
            const { userMenuMap } = this;
            // userMenuMap 菜单树叶子节点构成的map
            for (let [key] of userMenuMap) {
                if (typeof key === 'string' && key !== '') {
                    this.$router.push(key);
                    break;
                }
            }
        },
        // 根据路由的path展开菜单项
        expandMenuByPath(path) {
            const { userMenuMap } = this;
            const targetNode = userMenuMap.get(path);
            if (targetNode) {
                this.openKeys = targetNode.path.split('/').filter((_) => _ !== '');
            }
        },
        // 更新菜单的展开及选中状态
        // 同时根据用户菜单列表进行路由过滤
        // ***注意***：严格的权限过滤应该在路由视图组件中进行过滤，此处的地址过滤并不安全，
        // 完成视图组件处的路由过滤逻辑后，此处的过滤逻辑应删除；同时，第一页的默认展示也应该由路由组件完成
        setMenuState() {
            const { path } = this.$route;
            // 路由地址为'/'时，选中菜单第一项
            if (path === '/') {
                this.showFirstPage();
            } else {
                // 非'/'的路由地址，并且该路由包含在用户菜单里（即有权限），展开并选中该菜单项
                const targetNode = this.userMenuMap.get(path);
                if (targetNode) {
                    this.expandMenuByPath(path);
                    this.selectedKeys = [String(targetNode.id)];
                    return;
                }
                // 无权限则跳转至404页面
                this.$router.push('/error');
            }
        }
    },
    watch: {
        '$route'() {
            this.setMenuState();
        },
        userMenuMap() {
            this.setMenuState();
        }
    },
    created() {
        this.setMenuState();
    }
};
</script>
