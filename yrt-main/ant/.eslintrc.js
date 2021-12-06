module.exports = {
    root: true,
    parserOptions: {
        parser: 'babel-eslint',
        sourceType: 'module'
    },
    env: {
        node: true
    },
    extends: ['eslint:recommended', 'plugin:import/recommended', 'plugin:vue/essential', '@vue/prettier'],
    // required to lint *.vue files
    plugins: [],
    globals: {
        ga: true, // Google Analytics
        cordova: true,
        __statics: true
    },
    // add your custom rules here
    rules: {
        // 未使用的变量
        'no-unused-vars': 1,
        'vue/no-unused-vars': 1,
        // switch case局部变量
        'no-case-declarations': 1,
        // allow async-await
        'generator-star-spacing': 'off',
        'no-trailing-spaces': 0, // 不允许代码后多余空格
        'one-var': 0,

        // import
        'import/first': 2,
        'import/named': 2,
        'import/namespace': 2,
        'import/default': 2,
        'import/export': 2,
        'import/extensions': 0,
        'import/no-unresolved': 0,
        'import/no-extraneous-dependencies': 0,

        // allow debugger during development
        'no-debugger': process.env.NODE_ENV === 'production' ? 2 : 0
    },
    overrides: [
        {
            files: ['**/__mocks__/*.js', '**/__tests__/*.{j,t}s?(x)', '**/tests/unit/**/*.spec.{j,t}s?(x)'],
            env: {
                jest: true
            }
        }
    ]
};
