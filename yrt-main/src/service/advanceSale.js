import axios from './axios';

const advanceSaleAPIPath = '/v1/advance/sale';

/**
 * 预售单列表
 * @param {number} page
 * @param {number} pageSize
 * @param {object} searchParams 查询参数
 * @returns {Promise}
 */
export function advanceSaleOrderListAPI(page, pageSize, searchParams) {
    return axios.post(`${advanceSaleAPIPath}/list`, searchParams, { params: { pageNum: page, pageSize } });
}

/**
 * 预售单详细信息
 * @param {number} id 预售单id
 * @returns {Promise}
 */
export function advanceSaleOrderInfoAPI(id) {
    return axios.get(`${advanceSaleAPIPath}/getAdvanceById/${id}`);
}

/**
 * 预售单详情
 * @param {number} id 预售单id
 * @returns {Promise}
 */
export function advanceSaleOrderDetailAPI(id) {
    return axios.get(`${advanceSaleAPIPath}/getDetailsByAdvanceId/${id}`);
}

/**
 * 新增预售单
 * @param {object} data
 * @returns {Promise}
 */
export function advanceSaleOrderAddAPI(data) {
    return axios.post(`${advanceSaleAPIPath}/addOrUpdate`, data);
}

/**
 * 编辑预售单
 * @param {number} id 预售单id
 * @param {object} data
 * @returns {Promise}
 */
export function advanceSaleOrderEditAPI(id, data) {
    data.id = id;
    return axios.post(`${advanceSaleAPIPath}/addOrUpdate`, data);
}

/**
 * 审核预售单
 * @param {number} id 预售单id
 * @returns {Promise}
 */
export function advanceSaleOrderApproveAPI(id) {
    return axios.put(`${advanceSaleAPIPath}/checkAdvanceSale/${id}`);
}

/**
 * 删除预售单
 * @param {number} id 预售单id
 * @returns {Promise}
 */
export function advanceSaleOrderDeleteAPI(id) {
    return axios.put(`${advanceSaleAPIPath}/deleteAdvanceSale/${id}`);
}

/**
 * 预售单回库
 * @param {object[]} details 预售单明细
 * @param {string} details[].saleDetailId
 * @param {number | string} details[].quantity
 * @returns {Promise<{ code: Number, msg: String }>}
 */
export function advanceSaleOrderCancelAPI(details) {
    return axios.put(`${advanceSaleAPIPath}/cancel`, details);
}

/**
 * 生成出库单
 * @param {object[]} details 预售单明细
 * @param {string} details[].saleDetailId
 * @param {number | string} details[].quantity
 * @returns {Promise<{ code: Number, msg: String }>}
 */
export function advanceSaleOrderSoldAPI(details) {
    return axios.put(`${advanceSaleAPIPath}/sold`, details);
}
