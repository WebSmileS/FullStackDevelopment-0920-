// Configuration for your app
module.exports = {
    // 基本路径
    // 用publicPath替代
    // baseUrl: process.env.NODE_ENV === 'production'
    //     ? '/'
    //     : '/',
    // 输出文件目录
    outputDir: 'dist', // 默认dist
    // 用于嵌套生成的静态资产（js,css,img,fonts）目录
    assetsDir: 'static',
    // 指定生成的 index.html 的输出路径 (相对于 outputDir)。也可以是一个绝对路径
    indexPath: 'index.html', // Default: 'index.html'
    filenameHashing: true,
    // eslint-loader是否在保存的时候检查
    lintOnSave: true,
    // 如果你不需要生产环境的 source map，可以将其设置为 false 以加速生产环境构建。
    productionSourceMap: false,
    // css相关配置
    css: {
        sourceMap: process.env.NODE_ENV === 'development',
        loaderOptions: {
            less: {
                javascriptEnabled: true
            }
        }
    },
    configureWebpack: {
        devtool: 'source-map'
    },
    devServer: {
        disableHostCheck: true, // 启用热更新，不用手动刷新浏览器
        hot: false,
        liveReload: false,
        open: false, // 本地开发环境启动后自动打开页面
        port: 8080,
        proxy: {
            '/': {
                // target: 'http://192.168.50.124',
                // target: 'http://localhost', // target host
                target: 'http://yrtdev.bjyixiu.com', // target host
                ws: false, // proxy websockets
                changeOrigin: true, // needed for virtual hosted sites
                pathRewrite: {
                    '^/': '' // rewrite path
                }
            }
        }
    },
    chainWebpack: (config) => {
        // 修复HMR
        config.resolve.symlinks(true);

        // 移除 prefetch、preload 插件
        config.plugins.delete('prefetch');
        config.plugins.delete('preload');

        // development
        if (process.env.NODE_ENV === 'development') {
            config.plugins.delete('copy');
        }

        // production
        if (process.env.NODE_ENV === 'production') {
            // 代码压缩
            config.optimization.minimize(true);
            // 分割代码
            config.optimization.splitChunks({
                chunks: 'all'
            });
            config.externals({
                'vue': 'Vue',
                'vue-router': 'VueRouter',
                'vuex': 'Vuex',
                'axios': 'axios',
                'jsbarcode': 'JsBarcode',
                'big.js': 'Big'
            });
        }
    }
};
