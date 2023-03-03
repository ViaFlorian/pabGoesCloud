echo "Wechsel in Frontend-Ordner"
cd ./pab
echo "Installiere Abhängigkeiten"
npm install --silent
echo "Führe Eslint aus"
npm run lint:output