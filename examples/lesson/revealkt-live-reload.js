var revealKtRevision = Number(document.currentScript.dataset.revision);
var source = new EventSource('/sse');
source.addEventListener('PageUpdated', function (event) {
    if (Number(event.data) !== revealKtRevision) location.reload();
});
source.addEventListener('RenderError', function (event) {
    var panel = document.getElementById('revealkt-error');
    if (!panel) {
        panel = document.createElement('details');
        panel.id = 'revealkt-error';
        panel.open = true;
        panel.setAttribute('role', 'alert');
        panel.style.cssText = 'position:fixed;inset:12px 12px auto;z-index:10000;' +
            'max-height:40vh;overflow:auto;padding:16px;border:2px solid #ff9a8b;' +
            'border-radius:8px;background:#321b20;color:#fff;font:16px/1.5 sans-serif;text-align:left;';
        var summary = document.createElement('summary');
        summary.textContent = 'Update failed — showing the last working presentation';
        panel.appendChild(summary);
        var diagnostics = document.createElement('pre');
        diagnostics.style.cssText = 'white-space:pre-wrap;font:14px/1.5 monospace;';
        panel.appendChild(diagnostics);
        var hint = document.createElement('p');
        hint.textContent = 'Fix the script and save it. The presentation will update automatically.';
        panel.appendChild(hint);
        document.body.appendChild(panel);
    }
    panel.querySelector('pre').textContent = event.data;
    panel.open = true;
});
