/*
 * Uebernimmt die im Dashboard gewaehlte Hell-/Dunkel-Einstellung.
 *
 * Das Dashboard legt seine Wahl unter "pepper-theme" im localStorage ab. In der
 * Produktion liegt Keycloak unter /auth auf derselben Herkunft wie die Anwendung,
 * der Wert ist dort also lesbar - die Anmeldeseite sieht dann genauso aus wie die
 * Seite, von der man kam. In der lokalen Entwicklung laufen beide auf
 * verschiedenen Ports; dann greift die Systemeinstellung, genau wie bei Keycloak
 * selbst. Beides ist ein gueltiger Zustand, nichts bricht.
 *
 * Keycloak schaltet den Dunkelmodus ueber die Klasse "pf-v5-theme-dark" und
 * richtet sich dabei allein nach der Systemeinstellung. Hat der Nutzer im
 * Dashboard etwas anderes gewaehlt, wird diese Klasse hier nachgezogen.
 */
(function () {
  var STORAGE_KEY = 'pepper-theme';
  var LIGHT = 'pepper';
  var DARK = 'pepper-dark';
  var KC_DARK_CLASS = 'pf-v5-theme-dark';

  var root = document.documentElement;

  function stored() {
    try {
      var value = window.localStorage.getItem(STORAGE_KEY);
      return value === LIGHT || value === DARK ? value : null;
    } catch (e) {
      // Privater Modus oder blockierter Storage
      return null;
    }
  }

  function systemPrefersDark() {
    return !!(window.matchMedia && window.matchMedia('(prefers-color-scheme: dark)').matches);
  }

  function desired() {
    return stored() || (systemPrefersDark() ? DARK : LIGHT);
  }

  function apply() {
    var theme = desired();
    root.setAttribute('data-pepper-theme', theme);
    root.style.colorScheme = theme === DARK ? 'dark' : 'light';

    // Keycloaks eigene Klasse mitfuehren, damit nicht halb hell, halb dunkel entsteht
    var shouldBeDark = theme === DARK;
    if (root.classList.contains(KC_DARK_CLASS) !== shouldBeDark) {
      root.classList.toggle(KC_DARK_CLASS, shouldBeDark);
    }
  }

  apply();

  // Keycloaks Skript laeuft spaeter und setzt die Klasse nach der Systemeinstellung.
  // Weicht sie von der Wahl des Nutzers ab, wird sie hier wieder zurechtgerueckt.
  // Die Pruefung in apply() verhindert, dass sich das gegenseitig aufschaukelt.
  if (window.MutationObserver) {
    new MutationObserver(apply).observe(root, {
      attributes: true,
      attributeFilter: ['class'],
    });
  }

  if (window.matchMedia) {
    var query = window.matchMedia('(prefers-color-scheme: dark)');
    var onChange = function () { apply(); };
    if (query.addEventListener) {
      query.addEventListener('change', onChange);
    } else if (query.addListener) {
      query.addListener(onChange);
    }
  }
})();
