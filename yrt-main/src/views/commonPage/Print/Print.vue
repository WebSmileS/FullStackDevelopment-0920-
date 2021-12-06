<template>
    <div id="printWrapper">
        <print-editor>
            <template slot="printContent">
                <div id="printContent">
                    <router-view />
                </div>
            </template>
        </print-editor>
    </div>
</template>

<script>
import PrintEditor from './components/PrintEditor/PrintEditor.vue';

export default {
    name: 'Print',
    components: {
        PrintEditor
    }
};
</script>

<style lang="less">
#printWrapper {
    min-height: 100%;
    background-color: #888888;
}
#printContent {
    padding: 24px;
}
@media print {
    html,
    body,
    #app,
    #printWrapper,
    #printContent {
        display: block;
        box-sizing: content-box;
        padding: 0;
        margin: 0;
        border: none;
        background-color: #ffffff;
    }
}
</style>
