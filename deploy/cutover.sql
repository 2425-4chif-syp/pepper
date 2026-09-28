-- Einmaliger Datenschnitt beim Umstieg auf die MinIO-Version (pep-caretaker).
--
-- Bewusst KEIN "drop database": in derselben Datenbank `db` liegen auch die
-- Mealplan-Tabellen (pe_food, pe_allergen, pe_mealplan, pe_foodallergen), und in
-- derselben Postgres-Instanz liegt die Keycloak-Datenbank mit 16 echten Konten.
--
-- Auf ausdrueckliche Entscheidung wird auch Mealplan-Material mitgeloescht, das
-- ueber Fremdschluessel am Caretaker haengt:
--   pe_order        -> zeigt auf pe_person  (58 Bestellungen)
--   pe_food.f_i_id  -> zeigt auf pe_image   (104 Speisen verlieren ihr Bild)
--
-- Erhalten bleiben pe_game_type und pe_move (Stammdaten). Die Seed-Datei
-- import.sql laeuft nur bei der Strategie create/drop-and-create; in Produktion
-- gilt update, ohne diese beiden Tabellen liessen sich keine Spiele anlegen.
--
-- Ausfuehren:
--   docker exec -i postgres psql -U app -d db -v ON_ERROR_STOP=1 < cutover.sql

BEGIN;

-- Mealplan-Daten, die auf Caretaker-Tabellen zeigen
DELETE FROM pe_order;
UPDATE pe_food SET f_i_id = NULL;

-- Caretaker-Daten
DELETE FROM pe_game_score;
DELETE FROM pe_step;
UPDATE pe_game SET g_story_icon = NULL;
DELETE FROM pe_game;
DELETE FROM pe_image;
DELETE FROM pe_person;

COMMIT;
