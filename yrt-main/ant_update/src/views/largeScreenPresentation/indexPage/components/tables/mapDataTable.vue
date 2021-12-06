<template>
    <div>
        <table>
            <tbody v-if="isProvince">
                <tr>
                    <td class="table-first-row province">昆明市 </td>
                    <td class="table-second-row quantity">22</td>
                    <td class="table-first-row province">普洱市 </td>
                    <td class="table-second-row quantity">152</td>
                    <td class="table-first-row province">大理白族自治州 </td>
                    <td class="table-second-row quantity">152</td>
                </tr>
                <tr>
                    <td class="table-first-row province">曲靖市 </td>
                    <td class="table-second-row quantity">250</td>
                    <td class="table-first-row province">临沧市 </td>
                    <td class="table-second-row quantity">250</td>
                    <td class="table-first-row province">德宏傣族景颇族自治州 </td>
                    <td class="table-second-row quantity">250</td>
                </tr>
                <tr>
                    <td class="table-first-row province">玉溪市 </td>
                    <td class="table-second-row quantity">421</td>
                    <td class="table-first-row province">楚雄彝族自治州 </td>
                    <td class="table-second-row quantity">421</td>
                    <td class="table-first-row province">怒江傈僳族自治州 </td>
                    <td class="table-second-row quantity">421</td>
                </tr>
                <tr>
                    <td class="table-first-row province">保山市 </td>
                    <td class="table-second-row quantity">232</td>
                    <td class="table-first-row province">红河哈尼族彝族自治州 </td>
                    <td class="table-second-row quantity">232</td>
                    <td class="table-first-row province">迪庆藏族自治州 </td>
                    <td class="table-second-row quantity">232</td>
                </tr>
                <tr>
                    <td class="table-first-row province">昭通市 </td>
                    <td class="table-second-row quantity">125</td>
                    <td class="table-first-row province">文山壮族自治州 </td>
                    <td class="table-second-row quantity">125</td>
                </tr>
                <tr>
                    <td class="table-first-row province">丽江市 </td>
                    <td class="table-second-row quantity">173</td>
                    <td class="table-first-row province">西双版纳傣族自治州 </td>
                    <td class="table-second-row quantity">173</td>
                </tr>
            </tbody>
            <tbody v-else>
                <tr>
                    <td class="table-first-row province">河北省 </td>
                    <td class="table-second-row quantity">152</td>
                    <td class="table-first-row province">浙江省 </td>
                    <td class="table-second-row quantity">152</td>
                    <td class="table-first-row province">湖北省 </td>
                    <td class="table-second-row quantity">152</td>
                </tr>
                <tr>
                    <td class="table-first-row province">山西省 </td>
                    <td class="table-second-row quantity">250</td>
                    <td class="table-first-row province">安徽省 </td>
                    <td class="table-second-row quantity">250</td>
                    <td class="table-first-row province">湖南省 </td>
                    <td class="table-second-row quantity">250</td>
                </tr>
                <tr>
                    <td class="table-first-row province">辽宁省 </td>
                    <td class="table-second-row quantity">421</td>
                    <td class="table-first-row province">福建省 </td>
                    <td class="table-second-row quantity">421</td>
                    <td class="table-first-row province">广东省 </td>
                    <td class="table-second-row quantity">421</td>
                </tr>
                <tr>
                    <td class="table-first-row province">吉林省 </td>
                    <td class="table-second-row quantity">232</td>
                    <td class="table-first-row province">江西省 </td>
                    <td class="table-second-row quantity">232</td>
                    <td class="table-first-row province">海南省 </td>
                    <td class="table-second-row quantity">232</td>
                </tr>
                <tr>
                    <td class="table-first-row province">黑龙江省 </td>
                    <td class="table-second-row quantity">125</td>
                    <td class="table-first-row province">山东省 </td>
                    <td class="table-second-row quantity">125</td>
                    <td class="table-first-row province">四川省 </td>
                    <td class="table-second-row quantity">125</td>
                </tr>
                <tr>
                    <td class="table-first-row province">江苏省 </td>
                    <td class="table-second-row quantity">173</td>
                    <td class="table-first-row province">河南省 </td>
                    <td class="table-second-row quantity">173</td>
                    <td class="table-first-row province">云南省 </td>
                    <td class="table-second-row quantity">173</td>
                </tr>
            </tbody>
        </table>
    </div>
</template>

<script>
export default {
    props: {
        isProvince: Boolean
    }
}
</script>

<style scoped>
td {
    padding: 0px;
}

.province {
font-size: 0.22rem;
font-family: PingFang-SC-Medium;
text-align: center;
color: #9ad5ef;
line-height: 0.52rem;
}

.quantity {
font-size: 0.22rem;
font-family: SFProDisplay-Regular;
text-align: left;
color: #ffffff;
line-height: 0.52rem;
}

.table-first-row {
  width: 3rem;
  height: 0.51rem;
  opacity: 1;
  border-bottom: 0.01rem solid #2c97d159;
}

.table-second-row {
  width: 2rem;
  height: 0.51rem;
  opacity: 1;
  border-bottom: 0.01rem solid #2c97d159;
  text-align: center;

}
</style>
