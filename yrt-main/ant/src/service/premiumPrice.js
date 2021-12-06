import axios from './axios';

const premiumPriceAPIPath = '/v1/premium/rule';

/**
 * 规则列表页
 * @param {number} pageNum 页码
 * @param {number} pageSize 页大小
 * @param {object} searchParams 查询参数
 * @returns {Promise<object>}
 */
export function premiumPriceRuleListAPI(pageNum, pageSize, searchParams) {
    return axios.get(`${premiumPriceAPIPath}/list`, { params: { ...searchParams, pageNum, pageSize } });
}

/**
 * 新增规则
 * @param {object} data 规则数据
 * @param {number} data.maxPrice 最高价
 * @param {number} data.minPrice 最低价
 * @param {number} data.percent 溢价比例
 * @param {string} data.remake 备注
 * @returns {Promise<object>}
 */
export function premiumPriceRuleAddAPI(data) {
    let postData = { ...data };
    delete postData.id;
    return axios.post(`${premiumPriceAPIPath}/addOrUpdate`, postData);
}

/**
 * 修改规则
 * @param {number} id 规则id
 * @param {object} data 规则数据
 * @param {number} data.maxPrice 最高价
 * @param {number} data.minPrice 最低价
 * @param {number} data.percent 溢价比例
 * @param {string} data.remake 备注
 * @returns {Promise<object>}
 */
export function premiumPriceRuleEditAPI(id, data) {
    return axios.post(`${premiumPriceAPIPath}/addOrUpdate`, { ...data, id });
}

/**
 * 删除规则
 * @param {number} 规则id
 * @returns {Promise<object>}
 */
export function premiumPriceRuleDeleteAPI(id) {
    return axios.get(`${premiumPriceAPIPath}/delete/${id}`);
}
