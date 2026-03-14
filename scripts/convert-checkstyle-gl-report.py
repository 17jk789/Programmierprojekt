# Skript to convert findings of checkstyle into code quality report for gitlab
import xml.etree.ElementTree as ET, json, hashlib

findings = []
for source in ["main", "test"]:
    try:
        tree = ET.parse(f"build/reports/checkstyle/{source}.xml")
        for file in tree.findall("file"):
            path = file.get("name")
            for error in file.findall("error"):
                line = int(error.get("line", 1))
                msg = error.get("message", "")
                fingerprint = hashlib.md5(f"{path}{line}{msg}".encode()).hexdigest()
                findings.append({
                    "type": "issue",
                    "check_name": error.get("source", "checkstyle"),
                    "description": msg,
                    "severity": "minor",
                    "fingerprint": fingerprint,
                    "location": {
                        "path": "src/" + path.split("/src/")[1] if "/src/" in path else path,
                        "lines": {"begin": line}
                    }
                })
    except FileNotFoundError:
        pass

with open("gl-code-quality-report.json", "w") as f:
    json.dump(findings, f)
