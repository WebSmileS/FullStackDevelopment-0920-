function setVisible(_this, key, visible) {
    if (_this.modalsVisible[key] === undefined) {
        _this.$set(_this.modalsVisible, key, visible);
    } else {
        _this.modalsVisible[key] = visible;
    }
}

export default {
    data() {
        return {
            modalsVisible: {}
        };
    },
    methods: {
        handleOpenModals(key) {
            setVisible(this, key, true);
        },
        handleCloseModals(key) {
            setVisible(this, key, false);
        }
    }
};
