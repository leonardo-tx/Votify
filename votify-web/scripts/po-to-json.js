const fs = require('fs');
const path = require('path');
const gettextParser = require('gettext-parser');

const lang = 'pt-BR';
const poFilePath = path.resolve(__dirname, `../src/locales/${lang}/translation.po`);
const outputPath = path.resolve(__dirname, `../src/locales/${lang}/translation.json`);

const input = fs.readFileSync(poFilePath);
const po = gettextParser.po.parse(input);

const messages = {};

for (const key in po.translations[""]) {
  if (key.length === 0) continue;
  messages[key] = po.translations[""][key].msgstr[0];
}

fs.writeFileSync(outputPath, JSON.stringify(messages, null, 2));
console.log(`✔ Arquivo JSON gerado em: ${outputPath}`);