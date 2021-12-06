document.addEventListener("DOMContentLoaded", function () {
    var link = document.createElement('link');
    link.type = 'text/css';
    link.rel = 'stylesheet';
    // link.href = 'https://ynsj.gov.cn/Media/Default/%E5%85%AC%E5%91%8A/gonggao.css';
    link.href = 'gonggao.css';
    var head = document.getElementsByTagName('head')[0];
    head.appendChild(link);
    document.getElementById('btn-close').onclick = function () {
        document.getElementById('dialog_btn').style.display = 'none';
    }
    var domain = toLowerCase(document.domain);
    console.log(domain);
    if (domain === 'baitian.com' || domain === 'www.baitian.com') {
        document.getElementById('popWindow').style.display = 'none';
        document.getElementById('dialog_btn').style.display = 'none';
    }
});
