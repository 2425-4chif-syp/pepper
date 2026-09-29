import pytest
import time
import os
from selenium import webdriver
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait, Select
from selenium.webdriver.support import expected_conditions as EC
from selenium.common.exceptions import TimeoutException

# Dieser Test fahrt gegen ein echtes Deployment. Zugangsdaten kommen aus der
# Umgebung, damit keine Produktionskennung im Repository liegt:
#   E2E_BASE_URL=... E2E_USERNAME=... E2E_PASSWORD=... pytest e2e-test/
BASE_URL = os.environ.get("E2E_BASE_URL", "http://localhost:4200")
USERNAME = os.environ.get("E2E_USERNAME")
PASSWORD = os.environ.get("E2E_PASSWORD")

if not USERNAME or not PASSWORD:
    pytest.skip(
        "E2E_USERNAME und E2E_PASSWORD muessen gesetzt sein",
        allow_module_level=True,
    )


@pytest.fixture
def driver():
    options = webdriver.ChromeOptions()
    options.add_argument("--start-maximized")
    driver = webdriver.Chrome(options=options)
    yield driver
    driver.quit()


def test_login_and_create_person(driver):
    wait = WebDriverWait(driver, 40)

    # ========================
    # 1) LOGIN (Keycloak)
    # ========================
    driver.get(BASE_URL)

    wait.until(EC.visibility_of_element_located((By.ID, "username"))).send_keys(USERNAME)
    driver.find_element(By.ID, "password").send_keys(PASSWORD)
    wait.until(EC.element_to_be_clickable((By.CSS_SELECTOR, "button[type='submit']"))).click()

    # ✅ Warten bis wir NICHT mehr auf Keycloak (/auth/...) sind
    try:
        wait.until(lambda d: "/auth/" not in d.current_url)
    except TimeoutException:
        raise AssertionError(f"Nach Login immer noch auf Keycloak? URL ist: {driver.current_url}")

    # ========================
    # 2) Zur Residents-Seite (gezielt)
    # ========================
    driver.get(BASE_URL + "/residents")

    # Warten bis die Seite wirklich geladen ist (Button oder Überschrift)
    try:
        wait.until(EC.visibility_of_element_located(
            (By.XPATH, "//*[contains(.,'Bewohner') or contains(.,'Residents') or contains(.,'Neue Person')]")
        ))
    except TimeoutException:
        raise AssertionError(
            "Residents-Seite lädt nicht / kein erwarteter Text gefunden.\n"
            f"Aktuelle URL: {driver.current_url}"
        )

    # Button "Neue Person anlegen" klicken (Text kann mit + davor sein)
    wait.until(EC.element_to_be_clickable(
        (By.XPATH, "//button[contains(.,'Neue Person')]")
    )).click()

    # Add-Seite muss kommen
    wait.until(EC.url_contains("/residentAdd"))
    wait.until(EC.visibility_of_element_located(
        (By.XPATH, "//*[self::h1 or self::h2][contains(.,'Neue Person')]")
    ))

    # ========================
    # 3) Person anlegen (ohne data-testid)
    # ========================
    stamp = str(int(time.time()))
    first_name = f"Max{stamp}"
    last_name = f"Mustermann{stamp}"

    # optional: Profilbild (falls vorhanden)
    image_path = os.path.abspath("e2e-test/testdata/profile_picture.png")
    if os.path.exists(image_path):
        try:
            file_input = driver.find_element(By.XPATH, "//input[@type='file' and @accept='image/*']")
            file_input.send_keys(image_path)
        except Exception:
            pass

    # Vorname
    wait.until(EC.visibility_of_element_located((
        By.XPATH,
        "//label[contains(normalize-space(.),'Vorname')]/following::input[@type='text'][1]"
    ))).send_keys(first_name)

    # Nachname
    driver.find_element(
        By.XPATH,
        "//label[contains(normalize-space(.),'Nachname')]/following::input[@type='text'][1]"
    ).send_keys(last_name)

    # Rolle
    role_select = driver.find_element(By.XPATH, "//select")
    Select(role_select).select_by_value("bewohner")

    # Geburtstag
    dob_input = driver.find_element(By.XPATH, "//input[@type='date']")
    driver.execute_script("""
        arguments[0].value = '2000-01-01';
        arguments[0].dispatchEvent(new Event('input', {bubbles: true}));
        arguments[0].dispatchEvent(new Event('change', {bubbles: true}));
    """, dob_input)

    # Passwort
    driver.find_element(
        By.XPATH,
        "//label[contains(normalize-space(.),'Passwort')]/following::input[@type='password'][1]"
    ).send_keys("Test1234!")

    # Zimmer
    driver.find_element(
        By.XPATH,
        "//label[contains(normalize-space(.),'Zimmer')]/following::input[@type='text'][1]"
    ).send_keys("101")

    # Speichern
    add_btn = driver.find_element(By.XPATH, "//button[contains(.,'Person hinzufügen')]")
    wait.until(lambda d: add_btn.is_enabled())
    add_btn.click()

    # Erfolg: meistens zurück zur Liste
    wait.until(EC.any_of(
        EC.url_contains("/residents"),
        EC.url_changes(BASE_URL + "/residentAdd")
    ))

    assert BASE_URL in driver.current_url
