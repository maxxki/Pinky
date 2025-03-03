#!/bin/bash

# Konfigurationsdatei
CONFIG_FILE="package-manager.config.json"

# Hilfsfunktionen

# Funktion zur Fehlerprüfung und Wiederholung
check_and_retry() {
  if [ $? -ne 0 ]; then
    echo "Fehler aufgetreten. Wiederhole Schritt mit alternativer Methode..."
    "$@" || {
      echo "Wiederholter Versuch fehlgeschlagen. Beende Skript."
      exit 1
    }
  fi
}

# Funktion zur Überprüfung, ob ein Befehl installiert ist
command_exists() {
  command -v "$1" &>/dev/null
}

# Funktion zur Installation von jq
install_jq() {
  echo "jq wird installiert..."
  sudo apt-get update && sudo apt-get install -y jq || {
    echo "Fehler bei der Installation von jq. Bitte installieren Sie jq manuell."
    exit 1
  }
  echo "jq erfolgreich installiert."
}

# Funktion zur Installation von semver
install_semver() {
  echo "semver wird installiert..."
  if jq -e '.devDependencies.semver' package.json 2>/dev/null || jq -e '.dependencies.semver' package.json 2>/dev/null; then
    pnpm add semver --save-dev || {
      echo "Fehler bei der Installation von semver. Bitte installieren Sie semver manuell."
      exit 1
    }
  else
    npm install -g semver || {
      echo "Fehler bei der Installation von semver. Bitte installieren Sie semver manuell."
      exit 1
    }
  fi
  echo "semver erfolgreich installiert."
}

# Funktion zur Analyse von Versionsbereichen
analyze_version_range() {
  local package="$1"
  local version_range="$2"
  local latest_version=$(pnpm view "$package"@latest version 2>/dev/null)

  if [[ -n "$latest_version" ]]; then
    if command_exists semver; then
      if semver -r "$version_range" "$latest_version"; then
        echo "✅ $package@$latest_version ist kompatibel mit $version_range"
        echo "$latest_version"
      else
        echo "❌ $package@$latest_version ist inkompatibel mit $version_range"
        echo ""
      fi
    else
      echo "⚠ semver ist nicht installiert. Überspringe Kompatibilitätsprüfung."
      echo "$latest_version"
    fi
  else
    echo "❌ Konnte neueste Version von $package nicht abrufen"
    echo ""
  fi
}

# Funktion zur Überprüfung bekannter Inkompatibilitäten
check_incompatibilities() {
  local package="$1"
  local version="$2"

  if [ -f "$CONFIG_FILE" ] && command_exists jq; then
    if jq -e ".incompatiblePackages.\"$package\".\"$version\"" "$CONFIG_FILE" &>/dev/null; then
      echo "❌ Bekannte Inkompatibilität für $package@$version gefunden"
      return 1
    else
      return 0
    fi
  else
    return 0
  fi
}

# Hauptskript
echo "Starte INTELLIGENTE Paketverwaltung..."

# Installation von jq (falls nicht vorhanden)
if ! command_exists jq; then
  install_jq
fi

# Laden der Konfiguration
if [ -f "$CONFIG_FILE" ] && command_exists jq; then
  echo "Lade Konfiguration..."
  if ! jq -e '.' "$CONFIG_FILE" &>/dev/null; then
    echo "❌ Fehler: Ungültige JSON-Konfigurationsdatei. Erstelle Standardkonfiguration..."
    echo '{ "incompatiblePackages": {} }' >"$CONFIG_FILE"
  fi
else
  echo "Erstelle Standardkonfiguration..."
  echo '{ "incompatiblePackages": {} }' >"$CONFIG_FILE"
fi

# Lockfile-Prüfung
if [ ! -f pnpm-lock.yaml ]; then
  echo "⚠ pnpm-lock.yaml fehlt! Erstelle..."
  pnpm install --no-frozen-lockfile
else
  echo "Lockfile vorhanden. Überprüfe Lockfile-Integrität..."
  check_and_retry pnpm install --frozen-lockfile --rejectUnauthorized=false
fi

# Veraltete Pakete
OUTDATED_PACKAGES=$(pnpm outdated --json 2>/dev/null | jq -r 'keys[]' || echo "")
if [ -n "$OUTDATED_PACKAGES" ]; then
  echo "Veraltete Pakete erkannt: $OUTDATED_PACKAGES"
  while read -r pkg; do
    if pnpm list "$pkg" &>/dev/null; then
      echo "⚠ Entferne: $pkg"
      pnpm remove "$pkg" || echo "⚠ Konnte $pkg nicht entfernen, möglicherweise bereits gelöscht."
    fi
  done <<<"$OUTDATED_PACKAGES"
fi

# Installation von semver (falls nicht vorhanden)
if ! command_exists semver; then
  install_semver
fi

# Kompatibilitätsprüfung und Installation
echo "Prüfe Kompatibilität und installiere Pakete..."
for pkg in $(jq -r '.dependencies | keys[]' package.json 2>/dev/null); do
  version_range=$(jq -r ".dependencies.\"$pkg\"" package.json 2>/dev/null)
  compatible_version=$(analyze_version_range "$pkg" "$version_range")

  if [ -n "$compatible_version" ]; then
    if check_incompatibilities "$pkg" "$compatible_version"; then
      echo "Überspringe Installation von $pkg@$compatible_version aufgrund von Inkompatibilität"
    else
      installed_version=$(pnpm list "$pkg" --depth=0 --json 2>/dev/null | jq -r '.[0].dependencies."'$pkg'".version')
      if [[ "$installed_version" != "$compatible_version" ]]; then
        pnpm add "$pkg@$compatible_version"
      fi
    fi
  fi
done

# Sicherheitsprüfung und "kaputte" Pakete
pnpm audit || echo "⚠ Sicherheitsprobleme gefunden! Bitte prüfen!"
pnpm doctor || {
  echo "⚠ Allgemeine Probleme gefunden! Bitte prüfen:"
  pnpm doctor
}
pnpm check || echo "⚠ Probleme mit installierten Paketen gefunden! Bitte prüfen!"

echo "✅ Paketverwaltung abgeschlossen."
