# Spring Boot backend pro aplikaci průvodce muzei
Spring Boot rest API, které spolu s webovým a mobilním rozhraním tvoří aplikaci průvodce muzei.

Cílem zadání bylo vytvořit platformu, která by umožňovala komunitní překlad popisků vystavovaných exponátů a jejich následné bezproblémové zobrazování návštěvníky.

**Spuštění**:
- pro spuštění se předpokládá běžící databázový server MySQL Community Server 8 a přítomnost nástroje MySQL Workbench
- dále by na stroji měla být řádně nainstalovaná Java 11 a Apache Maven (v3.8.4)
- v souboru src/main/resources/application.properties musí být uvedena správná konfigruace pro spuštění
  - vlastnost **spring.datasource.url** definuje připojení k databázi a musí být nakonfigurována tak, aby odpovídala potřebám běžícího databázového serveru
  - vlastnosti definující přihlašovací údaje do databáze jsou **spring.datasource.username** a **spring.datasource.password**, tudíž je také potřeba je příslušne změnit
  - v případě potřeby lze také změnit údaje o adminstrátorovi, jsou to vlastnosti:
    - **cts.admin.name**
    - **cts.admin.password**
    - **cts.admin.email**
- pomocí nástroje MySQL Workbench je potřeba vytvořit nové schéma **museum_guide_db** s parametry:
  - **charset**: utf8mb4
  - **collation**: utf8mb4_czech_ci
- závěrem stačí v kořenovém adresáři spustit příkaz **mvn spring-boot:run**
- webové rohraní je následně dostupné z adresy: http://localhost:8080
