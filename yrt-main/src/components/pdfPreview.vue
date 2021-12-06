<template>
    <div v-bind:class="pdfSize">
        <pdf class="pdf" :src="url"></pdf>
    </div>
</template>

<script>
import pdf from 'vue-pdf';

export default {
    props: ['url', 'type'],
    components: {
        pdf
    },
    data() {
        return {
            currentPage: 0,
            pageCount: 0
        };
    },
    computed: {
        pdfSize: function () {
            return {
                pdfBig: this.type === 'big',
                pdfMid: this.type === 'mid',
                pdfSml: this.type === 'sml'
            };
        }
    }
};
</script>

<style>
.pdfBig canvas {
    max-height: 624px;
}

.pdfMid canvas {
    height: 170px !important;
}

.pdfMid .pdf {
    height: 170px;
}

.pdfSml canvas {
    height: 66px !important;
}

.pdfSml .pdf {
    height: 66px;
}
</style>
