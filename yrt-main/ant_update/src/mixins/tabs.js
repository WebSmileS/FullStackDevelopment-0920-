export default {
    data() {
        return {
            activeTabKey: ''
        };
    },
    methods: {
        handleToggleTab(key) {
            this.activeTabKey = key;
        }
    }
};
