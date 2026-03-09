#!/bin/bash

# Check for draw.io installation
DRAWIO_PATH=""
check_drawio_installed() {
  if command -v drawio &>/dev/null; then
    DRAWIO_PATH=$(command -v drawio)
    return 0  # drawio via PATH
  elif [[ -x "/Applications/draw.io.app/Contents/MacOS/draw.io" ]]; then
    DRAWIO_PATH="/Applications/draw.io.app/Contents/MacOS/draw.io"
    return 0  # For macOS check at default path
  else
    return 1  # not found
  fi
}

if ! check_drawio_installed; then
  echo "Error: 'drawio' was not found. Make sure, that the draw.io cli is installed."
  echo "https://www.diagrams.net/"
  exit 1
else
  echo "Draw.io installation found at: $DRAWIO_PATH"
fi

needs_export() {
  local drawio_file="$1"
  local svg_file="$2"

  # If no file found, export
  if [[ ! -f "$svg_file" ]]; then
    return 0
  fi

  # Compare source (draw.io document) and exported file on mtime
  if [[ "$drawio_file" -nt "$svg_file" ]]; then
    return 0
  else
    return 1
  fi
}

export_file() {
  local drawio_file="$1"
  local svg_file="${drawio_file%.drawio}.svg"

  if needs_export "$drawio_file" "$svg_file"; then
    echo "↻ Exportiere: $drawio_file"
    echo "$svg_file"
    echo "$drawio_file"
    
    "$DRAWIO_PATH" --export --format svg --transparent --output "$svg_file" "$drawio_file"
  else
    echo "✓ Up-to-date: $drawio_file"
  fi
}

main() {
  # Source - https://stackoverflow.com/a/9612232
  # Posted by Kevin, modified by community. See post 'Timeline' for change history
  # Retrieved 2026-03-09, License - CC BY-SA 4.0
  find . -name '*.drawio' -print0 | 
    while IFS= read -r -d '' line; do 
        export_file "$line"
    done

  echo "✓ Exported all files"
}

# Start script at entry point
main
