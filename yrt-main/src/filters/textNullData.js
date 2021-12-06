export default function textNullData(value) {
    if (value === undefined || value === null) {
        return '--';
    }
    return value;
}
