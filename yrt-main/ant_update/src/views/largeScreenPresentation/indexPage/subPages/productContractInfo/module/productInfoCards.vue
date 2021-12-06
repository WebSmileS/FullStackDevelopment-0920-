<template>
    <div :class="{   'pink-card':    CardType === 'money',
                'yellow-card':  CardType === 'batch',
                'green-card':   CardType === 'dealer',
                'blue-card':    CardType === 'cel',
                'product-info-card-container': true}"
            @click="onClickMore">
        <div class="infocard-more">
            <a-icon type="ellipsis" />
        </div>
        <div class="infocard-title-container">
            <div class="infocard-title-icon">
                <img class="infocard-title-icon-img" :src="iconPath">
            </div>
            <div class="infocard-title-words">
                <div class="infocard-title">{{ title }}</div>
                <div class="infocard-title-english">{{ titleEnglish }}</div>

            </div>
        </div>
        <hr class="divider">
        <div class="infocard-details">本产品销售至今 的经销商总数</div>
        <div class="infocard-details-number">{{ value }}</div>
    </div>
</template>

<script>
    export default {
        props: {
            CardType: String
        },
        data () {
            return {
                title: '',
                titleEnglish: '',
                value: '',
                iconPath: ''
            }
        },
        methods: {
            onClickMore () {
                switch (this.CardType) {
                    case 'money': {
                        this.$router.replace({path: '/largeScreen/productMoneyInfo'})
                        break
                    }
                    case 'batch': {
                        this.$router.replace({path: '/largeScreen/productBatchInfo'})
                        break
                    }
                    case 'dealer': {
                        this.$router.replace({path: '/largeScreen/productDealerInfo'})
                        break
                    }
                    case 'cel': {
                        this.$router.replace({path: '/largeScreen/productCelInfo'})
                        break
                    }
                }
            }
        },
        mounted () {
            switch (this.CardType) {
                case 'money': {
                    this.title = '金额'
                    this.titleEnglish = 'SALES AMOUNT'
                    this.value = '26252.5'
                    this.iconPath = '/images/largeScreen/icon/组 756.png'
                    break
                }
                case 'batch': {
                    this.title = '批次'
                    this.titleEnglish = 'PRODUCT BATCH'
                    this.value = '682'
                    this.iconPath = '/images/largeScreen/icon/组 766.png'
                    break
                }
                case 'dealer': {
                    this.title = '经销商'
                    this.titleEnglish = 'DISTRIBUTOR'
                    this.value = '246'
                    this.iconPath = '/images/largeScreen/icon/组 768.png'
                    break
                }
                case 'cel': {
                    this.title = '科室'
                    this.titleEnglish = 'DEPARTMENT'
                    this.value = '42'
                    this.iconPath = '/images/largeScreen/icon/组 770.png'
                    break
                }
            }
        }
    }
</script>

<style>
    .product-info-card-container {
        width: 4.13rem;
        height: 5.36rem;
        border-radius: 0.45rem;
        display: flex;
        flex-direction: column;
        justify-content: space-around;
        align-items: center;
        padding: 0.2rem 0.2rem 0.2rem 0.2rem;
    }

    .pink-card {
        background: linear-gradient(#fba993 0%, #ef77a8 100%);
    }

    .yellow-card {
        background: linear-gradient(#facd20 0%, #fbb03b 100%);
    }

    .green-card {
        background: linear-gradient(#0fe18a 0%, #00a99d 100%);
    }

    .blue-card {
        background: linear-gradient(#0fe1d9 0%, #29abe2 100%);
    }

    .divider {
        border-top: 1px solid rgb(255, 255, 255);
        border-bottom: none;
        width: 100%;
    }

    .infocard-more {
        align-self: flex-end;
        margin-right: 0.4rem;
    }

    .infocard-more i {
        font-size: 0.5rem;
    }

    .infocard-more svg {
        color: #fff;
    }

    .infocard-title-container {
        display: flex;
        flex-direction: row;
        align-items: flex-end;
        width: 100%;
        justify-content: space-around;

    }

    .infocard-title-icon {
        width: 0.8rem;
    }

    .infocard-title-words {
        display: flex;
        flex-direction: column;
        align-items: flex-start;
    }

    .infocard-title {
        font-family: "PingFang SC";
        font-weight: bold;
        font-size: 0.59rem;
        line-height: 0.47rem;
        text-align: left;
        color: #fff;
        margin-bottom: 20px;

    }

    .infocard-title-english {
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.25rem;
        line-height: 0.29rem;
        text-align: right;
        color: #fff;

    }


    .infocard-details {
        width: 4rem;
        font-family: "PingFang SC";
        font-weight: normal;
        font-size: 0.44rem;
        line-height: 0.65rem;
        text-align: right;
        color: #fff;

    }

    .infocard-details-number {
        font-family: "PingFang SC";
        font-weight: bold;
        font-size: 0.73rem;
        line-height: 0.71rem;
        text-align: right;
        color: #fff;
        align-self: flex-end;
        margin-right: 0.4rem;

    }
</style>