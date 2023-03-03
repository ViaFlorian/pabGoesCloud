echo "Wechsel in Frontend-Ordner"
cd ./pab
echo "Installiere Abhängigkeiten"
npm install --silent
echo "Führe Prettier aus"
npm run prettier
echo "Änderungen auflisten"
git status
echo "Änderungen pushen"
git config --global user.email "actions@github.com"
git config --global user.name "GitHub Action"
git commit -am "chore: Prettify sourcecode"
git push