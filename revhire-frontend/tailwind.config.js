/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,ts}",
  ],
  theme: {
    extend: {
      colors: {
        naukri: {
          blue: '#275df5',
          dark: '#121224',
          gray: '#f8f9fa',
          orange: '#f05537',
          text: '#474d6a',
          hover: '#1a4bdb'
        }
      },
      fontFamily: {
        sans: ['Inter', 'sans-serif'],
      }
    },
  },
  plugins: [],
}
