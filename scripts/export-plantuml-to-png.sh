#!/bin/bash

# Check for plantuml installation
PLANTUML_PATH=""
check_plantuml_installed() {
  if command -v plantuml &>/dev/null; then
    PLANTUML_PATH=$(command -v plantuml)
    return 0 # plantuml via PATH
  else
    return 1 # not found
  fi
}

if ! check_plantuml_installed; then
  echo "Error: 'plantuml' was not found. Make sure, that plantuml is installed."
  echo "https://plantuml.com/starting"
  exit 1
else
  echo "PlantUML installation found at: $PLANTUML_PATH"
fi

# Compare source (.puml document) and exported file png on mtime
needs_export() {
  local puml_file="$1"
  local png_file="$2"

  # If no file found, export
  if [[ ! -f "$png_file" ]]; then
    return 0
  fi

  if [[ "$puml_file" -nt "$png_file" ]]; then
    return 0
  else
    return 1
  fi
}

# If (re-)export is necessary export with plantuml cli
export_file() {
  local puml_file="$1"
  local png_file="${puml_file%.puml}.png"

  if needs_export "$puml_file" "$png_file"; then
    echo "↻ Exporting: $puml_file"
    "$PLANTUML_PATH" -tpng "$puml_file"
  else
    echo "✓ Up-to-date: $puml_file"
  fi
}

# Iterate over all .puml files
main() {
  # Source - https://stackoverflow.com/a/9612232
  # Posted by Kevin, modified by community. See post 'Timeline' for change history
  # Retrieved 2026-03-09, License - CC BY-SA 4.0
  find . -name '*.puml' -print0 | 
    while IFS= read -r -d '' line; do 
        export_file "$line"
    done

  echo "✓ Exported all files"
}

# Start script at entry point
main