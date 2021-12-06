<template>
     <div class="common-screen-wrapper">
        <!-- 头部标题 -->
        <div class="headTitle">
            <commonHeader  :headerName="'departmentWarningProcess'" :isGrid="false" :url="'departmentInfoDetail'"/>
        </div>
        <div class="org-process-content-container">
            <div class="org-process-button-list-container">
                <!-- 生产商 -->
                <div v-if="ven.display">
                    <select-options 
                        v-if="ven.openSelection"
                        :iconType="'ven'"
                        @selected="toggleSelected('ven', false)"
                    />
                    <select-button 
                        v-else
                        :iconType="'ven'"
                        @selected="toggleSelected('ven', true)"
                    />
                </div>
                <!-- 箭头 -->
                <div v-if="ven2deaArrow.display">
                    <arrow 
                        v-if="ven2deaArrow.isLeft"
                        :isLeft="true"
                    />
                    <arrow 
                        v-else
                        :isLeft="false"
                    />
                </div>
                <!-- 经销商 -->
                <div v-if="dea.display">
                    <select-options 
                        v-if="dea.openSelection"
                        :iconType="'dea'"
                        @selected="toggleSelected('dea', false)"
                    />
                    <select-button 
                        v-else
                        :iconType="'dea'"
                        @selected="toggleSelected('dea', true)"
                    />
                </div>
                <!-- 箭头 -->
                <div v-if="dea2hosArrow.display">
                    <arrow 
                        v-if="dea2hosArrow.isLeft"
                        :isLeft="true"
                    />
                    <arrow 
                        v-else
                        :isLeft="false"
                    />
                </div>
                <!-- 医院 -->
                <div v-if="hos.display">
                    <select-options 
                        v-if="hos.openSelection"
                        :iconType="'hos'"
                        @selected="toggleSelected('hos', false)"
                    />
                    <select-button 
                        v-else
                        :iconType="'hos'"
                        @selected="toggleSelected('hos', true)"
                    />
                </div>
                <!-- 箭头 -->
                <div v-if="hos2celArrow.display">
                    <arrow 
                        v-if="hos2celArrow.isLeft"
                        :isLeft="true"
                    />
                    <arrow 
                        v-else
                        :isLeft="false"
                    />
                </div>
                <!-- 科室 -->
                <div v-if="cel.display">
                    <select-options 
                        v-if="cel.openSelection"
                        :iconType="'cel'"
                        @selected="toggleSelected('cel', false)"
                    />
                    <select-button 
                        v-else
                        :iconType="'cel'"
                        @selected="toggleSelected('cel', true)"
                    />
                </div>
                <!-- 箭头 -->
                <div v-if="cel2recArrow.display">
                    <arrow 
                        v-if="cel2recArrow.isLeft"
                        :isLeft="true"
                    />
                    <arrow 
                        v-else
                        :isLeft="false"
                    />
                </div>
                <!-- 回收商 -->
                <div v-if="rec.display">
                    <select-options 
                        v-if="rec.openSelection"
                        :iconType="'rec'"
                        @selected="toggleSelected('rec', false)"
                    />
                    <select-button 
                        v-else
                        :iconType="'rec'"
                        @selected="toggleSelected('rec', true)"
                    />
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import Arrow from './components/arrow.vue'
import SelectButton from './components/selectButton.vue'
import SelectOptions from './components/selectOptions.vue'
import commonHeader from '../header/commonHeader.vue'

export default {
    props: {
        // entry_org: String
    },
    components: {
        commonHeader,
        SelectButton,
        SelectOptions,
        Arrow 
    },
    data () {
        return {
            /**
             * @example
             * 组织名称： {
             *     是否显示： Boolean
             *     是否选中： Boolean
             * }
             */
            // 生产商
            ven: {
                display: false,
                openSelection: false
            },
            // 经销商
            dea: {
                display: false,
                openSelection: false
            },
            // 医院
            hos: {
                display: false,
                openSelection: false
            },
            // 科室
            cel: {
                display: false,
                openSelection: false
            },
            // 回收商
            rec: {
                display: false,
                openSelection: false
            },
            /**
             * @example
             * 箭头名称： {
             *     是否显示： Boolean
             *     是否向左： Boolean
             * }
             */
            // 生产商 <-> 经销商
            ven2deaArrow: {
                display: false,
                isLeft: false                
            },
            // 经销商 <-> 医院
            dea2hosArrow: {
                display: false,
                isLeft: false                
            },
            // 医院 <-> 科室
            hos2celArrow: {
                display: false,
                isLeft: false                
            },
            // 科室 <-> 回收商
            cel2recArrow: {
                display: false,
                isLeft: false                    
            },
            entry_org: 'cel'
        }
    },
    methods: {
        /**
         *              toggleSelected
         * -----------------------------------------
         * @abstract 选中机构按钮或者列表的回调方法
         *           由子组件的@selected 方法触发
         *           主要作用为开启或关闭选择列表
         *           并在选中选择列表项目时，激活下一步选择
         * -----------------------------------------
         * @param {String} org 
         * 机构类型 [ven, dea, hos, cel, rec]
         * -----------------------------------------
         * @param {Boolean} isOpening 
         * 是否是打开选择列表操作，打开为true，关闭为false
         * -----------------------------------------
         */
        toggleSelected (org, isOpening) {
            // 无法选择当前的机构类型
            if (org === this.entry_org) { return }

            switch (org) {
            // 生产商
            case 'ven':
                this.ven.openSelection = !this.ven.openSelection 
                break
            // 经销商
            case 'dea':
                this.dea.openSelection = !this.dea.openSelection 
                if (!isOpening) {
                    if (this.entry_org === 'ven') {
                        // 位于流程右侧
                        this.hos.display = true
                        this.hos.openSelection = true

                        this.dea2hosArrow.display = true
                        this.dea2hosArrow.isLeft = false
                    } else {
                        // 位于流程左侧
                        this.ven.display = true
                        this.ven.openSelection = true

                        this.ven2deaArrow.display = true
                        this.ven2deaArrow.isLeft = true
                    }
                }
                break
            // 医院
            case 'hos':
                this.hos.openSelection = !this.hos.openSelection 
                if (!isOpening) {
                    if (this.entry_org === 'ven' || this.entry_org === 'dea') {
                        // 位于流程右侧
                        this.cel.display = true
                        this.cel.openSelection = true

                        this.hos2celArrow.display = true
                        this.hos2celArrow.isLeft = false
                    } else {
                        // 位于流程左侧
                        this.dea.display = true
                        this.dea.openSelection = true

                        this.dea2hosArrow.display = true
                        this.dea2hosArrow.isLeft = true
                    }
                }
                break
            // 科室
            case 'cel':
                this.cel.openSelection = !this.cel.openSelection 
                if (!isOpening) {
                    if (this.entry_org === 'rec') {
                        // 位于流程右侧
                        this.hos.display = true
                        this.hos.openSelection = true

                        this.hos2celArrow.display = true
                        this.hos2celArrow.isLeft = true
                    } else {
                        // 位于流程左侧
                        this.rec.display = true
                        this.rec.openSelection = true

                        this.cel2recArrow.display = true
                        this.cel2recArrow.isLeft = false
                    }
                }
                break
            // 回收商
            case 'rec':
                this.rec.openSelection = !this.rec.openSelection 
                break
            }
        },
        /**
         *              initialDisplay
         * -----------------------------------------
         * @abstract 初始化显示界面
         *           由 mounted方法调用
         *           通过props中的entry_org决定显示元素
         * -----------------------------------------
         */
        initialDisplay () {
            switch (this.entry_org) {
            // 经销商
            case 'ven':
                // 显示自身
                this.ven.display = true
    
                // 显示邻居
                this.dea.display = true
                this.dea.openSelection = true

                // 显示箭头
                this.ven2deaArrow.display = true
                this.ven2deaArrow.isLeft = false
                break
            // 生产商
            case 'dea':
                
                // 显示自身
                this.dea.display = true
    
                // 显示邻居
                this.ven.display = true
                this.ven.openSelection = true
                this.hos.display = true
                this.hos.openSelection = true

                // 显示箭头
                this.ven2deaArrow.display = true
                this.ven2deaArrow.isLeft = true
                this.dea2hosArrow.display = true
                this.dea2hosArrow.isLeft = false
                break
            // 医院
            case 'hos':
                // 显示自身
                this.hos.display = true
    
                // 显示邻居
                this.ven.display = true
                this.ven.openSelection = true
                this.cel.display = true
                this.cel.openSelection = true

                // 显示箭头
                this.dea2hosArrow.display = true
                this.dea2hosArrow.isLeft = true
                this.hos2celArrow.display = true
                this.hos2celArrow.isLeft = false
                break
            // 科室
            case 'cel':
                // 显示自身
                this.cel.display = true
    
                // 显示邻居
                this.hos.display = true
                this.hos.openSelection = true
                this.rec.display = true
                this.rec.openSelection = true

                // 显示箭头
                this.hos2celArrow.display = true
                this.hos2celArrow.isLeft = true
                this.cel2recArrow.display = true
                this.cel2recArrow.isLeft = false
                break
            // 回收商
            case 'rec':
                // 显示自身
                this.rec.display = true
    
                // 显示邻居
                this.cel.display = true
                this.cel.openSelection = true

                // 显示箭头
                this.cel2recArrow.display = true
                this.cel2recArrow.isLeft = true
                break
            }
        }
    },
    mounted () {
        // 根据父组件初始化显示初始机构
        this.initialDisplay()
    },
    computed: {
    }
}
</script>

<style>
.common-screen-wrapper {
        position: absolute;
        top: 1.03rem;
        left: 0.68rem;
    }
.org-process-button-list-container {
    display: flex;
    flex-direction: row;
    align-items: center;
    justify-content: space-evenly;
    height: 13rem;
    width: 24rem;
    padding: 7.22rem 2rem 0rem 2rem;

}
</style>
