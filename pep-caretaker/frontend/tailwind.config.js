/** @type {import('tailwindcss').Config} */
module.exports = {
  mode: 'jit',
  content: [
    "./src/**/*.{html,ts}"
  ],
  // dark:-Varianten folgen dem App-Toggle, nicht der OS-Einstellung
  darkMode: ['selector', '[data-theme="pepper-dark"]'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['var(--font-ui)'],
      },
      borderRadius: {
        control: 'var(--r-control)',
        card: 'var(--r-card)',
        sheet: 'var(--r-sheet)',
        pill: 'var(--r-pill)',
      },
      boxShadow: {
        card: 'var(--shadow-card)',
        'card-hover': 'var(--shadow-card-hover)',
        sheet: 'var(--shadow-sheet)',
      },
      transitionTimingFunction: {
        standard: 'var(--ease-standard)',
        exit: 'var(--ease-exit)',
      },
    },
  },
  plugins: [
    require('daisyui'),
  ],
  daisyui: {
    themes: [
      {
        // Helles Theme: neutrale Flaechen, genau ein Akzent (Pepper-Teal).
        // Farbe bedeutet hier etwas - sie markiert die primaere Aktion.
        pepper: {
          'primary': '#2f8080',
          'primary-content': '#ffffff',
          'secondary': '#f5f5f7',
          'secondary-content': '#1d1d1f',
          'accent': '#2f8080',
          'accent-content': '#ffffff',
          'neutral': '#1d1d1f',
          'neutral-content': '#f5f5f7',
          'base-100': '#ffffff',
          'base-200': '#f5f5f7',
          'base-300': '#d2d2d7',
          'base-content': '#1d1d1f',
          'info': '#0071e3',
          'info-content': '#ffffff',
          'success': '#34c759',
          'success-content': '#ffffff',
          'warning': '#ff9500',
          'warning-content': '#ffffff',
          'error': '#ff3b30',
          'error-content': '#ffffff',
          '--rounded-box': '0.875rem',
          '--rounded-btn': '980px',
          '--rounded-badge': '980px',
          '--border-btn': '1px',
          '--animation-btn': '0.2s',
          '--animation-input': '0.2s',
          '--btn-focus-scale': '0.97',
          '--tab-radius': '0.625rem',
        },
      },
      {
        // Dunkles Theme: gleiche Semantik, angehobene Akzente fuer Kontrast auf Schwarz
        'pepper-dark': {
          'primary': '#4db6b6',
          'primary-content': '#00201f',
          'secondary': '#1c1c1e',
          'secondary-content': '#f5f5f7',
          'accent': '#4db6b6',
          'accent-content': '#00201f',
          'neutral': '#f5f5f7',
          'neutral-content': '#1d1d1f',
          // base-100 traegt die Karten und liegt deshalb UEBER base-200 (der Seitenflaeche).
          // Im Dunkelmodus heisst das: die Karte ist heller als der Hintergrund, sonst wirkt
          // sie wie ein Loch statt wie eine angehobene Flaeche.
          'base-100': '#1c1c1e',
          'base-200': '#000000',
          'base-300': '#3a3a3c',
          'base-content': '#f5f5f7',
          'info': '#0a84ff',
          'info-content': '#ffffff',
          'success': '#30d158',
          'success-content': '#00210a',
          'warning': '#ff9f0a',
          'warning-content': '#231000',
          'error': '#ff453a',
          'error-content': '#ffffff',
          '--rounded-box': '0.875rem',
          '--rounded-btn': '980px',
          '--rounded-badge': '980px',
          '--border-btn': '1px',
          '--animation-btn': '0.2s',
          '--animation-input': '0.2s',
          '--btn-focus-scale': '0.97',
          '--tab-radius': '0.625rem',
        },
      },
    ],
    darkTheme: 'pepper-dark',
  },
}
