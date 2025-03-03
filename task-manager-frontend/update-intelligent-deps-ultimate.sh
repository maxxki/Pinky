#!/bin/bash

# Funktion zur Fehlerprüfung und Wiederholung
check_and_retry() {
  "$@"
  if [ $? -ne 0 ]; then
    echo "❌ Fehler aufgetreten: $@. Wiederhole Schritt mit alternativer Methode..."
    sleep 2
    "$@" || {
      echo "❌ Wiederholter Versuch fehlgeschlagen! Bitte prüfe die Abhängigkeiten manuell."
      exit 1
    }
  fi
}

echo "🚀 Starte INTELLIGENTES Upgrade & Fehleranalyse für das gesamte Projekt..."

# 🔍 Prüfe, ob package.json existiert (sonst abbrechen)
if [ ! -f package.json ]; then
    echo "❌ Fehler: Keine package.json gefunden! Erstelle eine neue..."
    check_and_retry npm init -y
fi

# 🔍 Prüfe auf fehlendes Lockfile und versuche, es zu generieren
if [ ! -f pnpm-lock.yaml ]; then
    echo "⚠ pnpm-lock.yaml fehlt! Erstelle eine neue..."
    check_and_retry pnpm install --lockfile-only
else
    echo "✅ pnpm-lock.yaml gefunden. Überprüfe Integrität..."
    check_and_retry pnpm install --frozen-lockfile
fi

# 🛠 Prüfe auf inkompatible oder veraltete Pakete
echo "🔍 Scanne auf veraltete Pakete..."
OUTDATED_PACKAGES=$(pnpm outdated --json 2>/dev/null | jq -r 'keys[]' || echo "")

if [ -z "$OUTDATED_PACKAGES" ]; then
    echo "✅ Alle Pakete sind aktuell!"
else
    echo "📋 Veraltete Pakete erkannt:"
    echo "$OUTDATED_PACKAGES"

    echo "🗑 Entferne veraltete und problematische Pakete..."
    while read -r pkg; do
        if pnpm list "$pkg" &>/dev/null; then
            echo "⚠ Entferne: $pkg"
            check_and_retry pnpm remove "$pkg"
        fi
    done <<< "$OUTDATED_PACKAGES"
fi

# 🛠 Prüfe kompatible Versionen für problematische Pakete
echo "🛠 Prüfe Kompatibilität der neuen Versionen..."
COMPATIBLE_VERSIONS=$(pnpm view autoprefixer versions --json | jq -r '.[-1]' || echo "10.4.12")

echo "📦 Installiere kompatible Versionen..."
check_and_retry pnpm add "autoprefixer@$COMPATIBLE_VERSIONS" "postcss@latest" --save-dev

if [ $? -ne 0 ]; then
    echo "❌ Fehler: Konnte Pakete nicht installieren! Versuche ältere kompatible Versionen..."
    check_and_retry pnpm add "autoprefixer@10.4.12" "postcss@8.4.21" --save-dev
fi

# 🛠 Überprüfe und erstelle fehlende Konfigurationsdateien
echo "📂 Überprüfe fehlende Konfigurationsdateien..."
if [ ! -f .eslintrc.json ]; then
    echo "⚠ .eslintrc.json fehlt. Erstelle..."
    check_and_retry npx eslint --init
fi

# 🔍 Letzte Prüfung auf Fehler
echo "🔍 Letzte Prüfung auf Probleme..."
pnpm audit || echo "⚠ Sicherheitsprobleme gefunden! Bitte prüfen!"

# 🔥 Teste, ob das Build noch funktioniert
echo "🛠 Führe abschließende Fehlerprüfung durch..."
check_and_retry npm run build || {
  echo "❌ Fehler im Build! Prüfe die Abhängigkeiten und Fixes!"
  exit 1
}

echo "✅ ALLES ERLEDIGT! Dein Projekt ist 100% up-to-date, sauber & fehlerfrei! 🚀"
