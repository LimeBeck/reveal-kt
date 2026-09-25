(() => {
  'use strict';
  const examples = window.revealKtExamples;
  let example = examples[0];
  const tabs = [...document.querySelectorAll('[data-example]')];
  const escape = value => value.replace(/[&<>"']/g, char => ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[char]));
  // Highlight tokens before escaping; user-provided HTML is never interpreted.
  function highlight(source) {
    return source.split(/("""[\s\S]*?"""|"(?:\\.|[^"\\])*"|\/\/[^\n]*|\b(?:import|val|fun|false|true|null)\b)/g).map(token => {
      const kind = token.startsWith('"') ? 'string' : token.startsWith('//') ? 'comment' : /^(import|val|fun|false|true|null)$/.test(token) ? 'keyword' : '';
      return kind ? `<span class="syntax-${kind}">${escape(token)}</span>` : escape(token);
    }).join('');
  }
  function selectExample(id) {
    example = examples.find(item => item.id === id);
    tabs.forEach(tab => {
      const selected = tab.dataset.example === id;
      tab.setAttribute('aria-selected', String(selected));
      tab.tabIndex = selected ? 0 : -1;
    });
    document.querySelector('#example-panel').setAttribute('aria-labelledby', `tab-${id}`);
    const deckUrl = `examples/${id}/index.html`;
    const frame = document.querySelector('#example-frame');
    // Let the real Reveal.js bundle handle navigation and fragments inside its frame.
    if (frame.getAttribute('src') !== deckUrl) frame.src = deckUrl;
    frame.title = `${example.name} — RevealKt presentation`;
    document.querySelector('#example-open').href = deckUrl;
    document.querySelector('#source-name').textContent = `${example.name}.reveal.kts`;
    document.querySelector('#example-code').innerHTML = highlight(example.source);
    document.querySelector('.example-source pre').scrollTop = 0;
    document.querySelector('#example-description').textContent = example.description;
    document.querySelector('#example-command').textContent = `revealkt init ${example.name} --example ${id}
revealkt run ${example.name}/presentation/${example.name}.reveal.kts --host 127.0.0.1`;
    document.querySelector('#example-source-link').href = `https://github.com/LimeBeck/reveal-kt/tree/master/reveal-kt/app/src/jvmMain/resources/examples/${id}`;
  }
  tabs.forEach((tab, index) => {
    tab.addEventListener('click', () => selectExample(tab.dataset.example));
    tab.addEventListener('keydown', event => {
      let target;
      if (event.key === 'ArrowRight') target = (index + 1) % tabs.length;
      if (event.key === 'ArrowLeft') target = (index + tabs.length - 1) % tabs.length;
      if (event.key === 'Home') target = 0;
      if (event.key === 'End') target = tabs.length - 1;
      if (target === undefined) return;
      event.preventDefault();
      tabs[target].focus();
      selectExample(tabs[target].dataset.example);
    });
  });
  async function copyText(text) {
    if (navigator.clipboard && window.isSecureContext) {
      try { await navigator.clipboard.writeText(text); return; } catch (_) { /* Local-file / permission fallback below. */ }
    }
    const focused = document.activeElement;
    const textarea = document.createElement('textarea');
    textarea.value = text;
    textarea.style.cssText = 'position:fixed;top:0;left:-9999px;';
    document.body.append(textarea);
    textarea.select();
    let copied;
    try { copied = document.execCommand('copy'); }
    finally { textarea.remove(); focused?.focus({preventScroll:true}); }
    if (!copied) throw new Error('Clipboard unavailable');
  }
  document.querySelectorAll('.copy').forEach(button => {
    let timer;
    button.addEventListener('click', async () => {
      const source = button.id === 'copy-example' ? example.source : button.closest('.code-block').querySelector('code').textContent;
      clearTimeout(timer);
      try {
        await copyText(source);
        button.textContent = 'Copied ✓';
        document.querySelector('#copy-status').textContent = 'Code copied to clipboard.';
      } catch (_) {
        button.textContent = 'Select code to copy';
        document.querySelector('#copy-status').textContent = 'Clipboard unavailable. Select and copy the code manually.';
      }
      timer = setTimeout(() => { button.textContent = 'Copy'; }, 2400);
    });
  });
  const menu = document.querySelector('#menu-toggle');
  const sidebar = document.querySelector('#sidebar');
  function closeMenu() { sidebar.classList.remove('open'); menu.setAttribute('aria-expanded', 'false'); }
  menu.addEventListener('click', () => menu.setAttribute('aria-expanded', String(sidebar.classList.toggle('open'))));
  sidebar.querySelectorAll('a').forEach(link => link.addEventListener('click', closeMenu));
  document.addEventListener('keydown', event => { if (event.key === 'Escape' && sidebar.classList.contains('open')) { closeMenu(); menu.focus(); } });
  document.addEventListener('click', event => { if (!sidebar.contains(event.target) && !menu.contains(event.target)) closeMenu(); });
  const links = [...sidebar.querySelectorAll('nav a')];
  const sections = [...document.querySelectorAll('main section')];
  let scheduled = false;
  function updateNavigation() {
    let current = sections[0];
    for (const section of sections) if (section.getBoundingClientRect().top <= 160) current = section;
    if (window.innerHeight + window.scrollY >= document.documentElement.scrollHeight - 5) current = sections[sections.length - 1];
    links.forEach(link => {
      const active = link.hash === `#${current.id}`;
      link.classList.toggle('active', active);
      if (active) link.setAttribute('aria-current', 'location'); else link.removeAttribute('aria-current');
    });
    scheduled = false;
  }
  window.addEventListener('scroll', () => { if (!scheduled) { scheduled = true; requestAnimationFrame(updateNavigation); } }, {passive:true});
  selectExample('technical');
  updateNavigation();
})();
