<template>
    <div>
        <div class="UpPart">
            <leftUpPart/>
        </div>
        <div class="DownPart">
            <leftDownPart/>
        </div>
    </div>
</template>

<script>
import leftUpPart from './leftUpPart'
import leftDownPart from './leftDownPart'

export default {
    name: 'leftPart',
    components: {
        leftUpPart,
        leftDownPart
    }
}
</script>

<style scpoed>
.UpPart {
    width: 100%;
    height: 3.44rem;
    background: #0071bc2e;
}

.DownPart {
    margin-top: 0.24rem;
    width: 100%;
    height: 9.32rem;
    background: #0071bc2e;
}
</style>
