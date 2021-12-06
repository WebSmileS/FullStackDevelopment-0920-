<template>
    <a-list
        v-if="loadingSkeletonShow"
        class="slider-menu-skeleton"
        :class="{ 'slider-menu-skeleton-1024': collapsed }"
        itemLayout="vertical"
        size="small"
        :dataSource="listData"
    >
        <a-list-item slot="renderItem">
            <a-skeleton :loading="loadingSkeletonShow" active avatar :paragraph="false"> </a-skeleton>
        </a-list-item>
    </a-list>
</template>

<script>
export default {
    name: 'LayoutMenuSkeleton',
    props: {
        loadingSkeletonShow: {
            type: Boolean,
            default: false
        },
        collapsed: {
            type: Boolean,
            default: false
        }
    },
    data() {
        return {
            listData: [
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                },
                {
                    href: '',
                    title: ''
                }
            ]
        };
    }
};
</script>

<style lang="less">
/* 骨架屏区域 */
.slider-menu-skeleton {
    .ant-list-item {
        .ant-list-item-content {
            width: 190px;
            margin: 0 auto;
        }

        .ant-skeleton {
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .ant-skeleton-avatar-square {
            width: 30px;
            height: 30px;
            line-height: 30px;
            border-radius: 50%;
        }
    }

    // 当屏幕小于等于1024的时候，骨架屏的展现方式
    &.slider-menu-skeleton-1024 {
        .ant-list-item {
            .ant-skeleton {
                justify-content: flex-start;
            }

            .ant-skeleton-content {
                display: none;
            }
        }
    }
}
</style>
