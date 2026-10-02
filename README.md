# localhost-anarchia ⚡

Autorski, zaawansowany plugin wprowadzający kompletny system unikalnych przedmiotów, broni i gadżetów bojowych wzorowanych na najpopularniejszym polskim serwerze **Anarchia.GG** (oraz trybach BoxPvP / Anarchia SMP).

## 🌟 Główne Funkcje i Przedmioty

1. **Bombarda Maxima** (`/anarchia daj <gracz> bombarda-maxima`)
   * Rzucany pocisk pirotechniczny o potężnej sile rażenia.
   * Niszczy okoliczne bloki, w tym **Obsydian**!
   * Zadaje wysokie obrażenia i odrzuca pobliskich graczy.

2. **Anty-Cobweb** (`/anarchia daj <gracz> anty-cobweb`)
   * Błyskawicznie niszczy wszystkie pajęczyny w promieniu 5 kratek wokół gracza.
   * Kluczowy gadżet do ucieczki z pułapek i klatek (trapboxów).

3. **Trapbox (Rzucana Pajęczyna)** (`/anarchia daj <gracz> trapbox`)
   * Rzucany pocisk, który w miejscu uderzenia tworzy pajęczynową pułapkę (3x3x3).
   * Pajęczyny po 15 sekundach automatycznie znikają, zapobiegając zaśmiecaniu mapy.

4. **Boski Topór** (`/anarchia daj <gracz> boski-topor`)
   * Legendarny topór z zaklęciem Sharpness VI i Unbreaking V.
   * Prawy przycisk myszy: aktywuje **Boską Ochronę** (chwilowa nieśmiertelność) i wywołuje falę uderzeniową odpychającą wszystkich wrogów.

5. **Excalibur** (`/anarchia daj <gracz> excalibur`)
   * Legendarny miecz z dynamicznym licznikiem zabójstw w lore (`Zabójstwa: X`).
   * Zwiększa zadawane obrażenia wraz z kolejnymi zabitymi graczami.
   * PPM: ciska Świętym Piorunem w namierzony cel, zadając dodatkowe nieuchronne obrażenia.

6. **Krew Wampira** (`/anarchia daj <gracz> krew-wampira`)
   * Użycie: natychmiastowo leczy 6 serc (12 HP) oraz nadaje Regenerację II.
   * Pasywka: 15% szansy na kradzież życia (lifesteal) przy każdym uderzeniu wroga!

7. **Kostka Rubika** (`/anarchia daj <gracz> kostka-rubika`)
   * Przedmiot do trollowania na PvP.
   * Rzucona lub zadana ciosami losowo miesza sloty hotbara ofiary oraz wywołuje silne nudności.

8. **Niszczyciel Bedrocka** (`/anarchia daj <gracz> niszczyciel-bedrocka`)
   * Specjalny ładunek kwasowy.
   * Po kliknięciu na Bedrock rozpoczyna proces 3-sekundowego syczenia, po czym niszczy blok bedrocka.
   * Posiada wbudowaną ochronę przed niszczeniem najniższej warstwy świata (ochrona przed próżnią).

9. **Korona Anarchii** (`/anarchia daj <gracz> korona-anarchii`)
   * Legendarny hełm zapewniający stałe efekty: Siła I, Szybkość I oraz Odporność I.
   * Złota korona cząsteczek nad głową gracza.

10. **Lizak Anarchii** (`/anarchia daj <gracz> lizak-anarchii`)
    * Szybka przekąska bojowa: nadaje Absorpcję IV (złote serca) oraz Szybkość II na 30 sekund.

11. **Anarchiczny Kilof ☆∞** (`/anarchia daj <gracz> kilof-anarchii`)
    * Wyposażony w Wydajność VII, Szczęście IV oraz Niezniszczalność X.
    * **Nie wypada po śmierci!** Gracz zachowuje kilof w ekwipunku po respawnie.

---

## 💻 Komendy i Uprawnienia

| Komenda | Uprawnienie | Opis |
|---|---|---|
| `/anarchia` | `anarchia.use` | Otwiera interaktywne menu GUI ze wszystkimi przedmiotami |
| `/anarchia lista` | `anarchia.use` | Wyświetla listę wszystkich przedmiotów w czacie |
| `/anarchia daj <gracz> <przedmiot> [ilość]` | `anarchia.admin` | Nadaje graczowi wybrany unikalny przedmiot |
| `/anarchia reload` | `anarchia.admin` | Przeładowuje plik `config.yml` oraz receptury |

---

## 🛠️ Receptury Craftingu
Plugin zawiera wbudowany, konfigurowalny system craftingu w `config.yml`. Gracze mogą samodzielnie wytwarzać przedmioty na serwerze Survival lub BoxPvP!
