// 点击延时 => 防止表格行重复点击 多次发送请求
class ClickDebounce {
    constructor() {
        this.timer = null;
    }

    debounce(func, time) {
        let delay = time || 300;
        return function () {
            let args = arguments;
            const timer = this.timer;
            if (timer) {
                window.clearTimeout(timer);
            }
            this.timer = window.setTimeout(() => {
                this.timer = null;
                func.apply(this, args);
            }, delay);
        };
    }

    getTimer() {
        return this.timer;
    }

    clearTimer() {
        window.clearTimeout(this.timer);
        this.timer = null;
    }
}

export default new ClickDebounce();
