

Uruchomienie projektu:
1. mvn clean package
2. java -jar target/zadanieRekrutacyjne-1.0.0-SNAPSHOT-fat.jar -conf src/main/conf/my-application-conf.json

Aby login był unikatowy należy wykonać komendę w shellu mongo:
1. baziwo.user.createIndex( { "login": 1 }, { unique: true } )

API:
1. POST localhost:3000/login – nieutoryzowany endpoint do zalogowania się. Zwraca token.
2. POST localhost:3000/register – nieutoryzowany endpoint do utworzenia konta poprzez podanie w request body loginu i hasła.
3. POST localhost:3000/items – autoryzowany endpoint do utworzenia itemu użytkownika poprzez podanie jego nazwy w request body.
4. GET localhost:3000/items – autoryzowany endpoint do pobrania listy itemów użytkownika.

Brak testów.
