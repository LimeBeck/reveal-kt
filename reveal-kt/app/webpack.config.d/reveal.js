// Reveal.js 6 declares type:module but its require exports contain UMD code.
// Let Webpack recognize module.exports in these published CommonJS files.
config.module.rules.push({
    test: /reveal\.js[\\/]dist[\\/].*\.js$/,
    type: 'javascript/auto'
});
