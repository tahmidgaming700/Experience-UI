#!/usr/bin/env python3
"""Validate Experience UI GSI metadata; never flashes or modifies a device."""
import json
from pathlib import Path

manifest = Path(__file__).resolve().parents[1] / "device-targets.json"
data = json.loads(manifest.read_text(encoding="utf-8"))

assert data.get("project") == "Experience UI"
assert data.get("format_version") == 1
assert data.get("base", {}).get("kind") == "LineageOS-derived GSI"
assert isinstance(data.get("targets"), list) and data["targets"]

ids = set()
for target in data["targets"]:
    assert target.get("id") and target["id"] not in ids, "missing or duplicate target id"
    ids.add(target["id"])
    assert target.get("status") in {"research-only", "blocked", "buildable", "tested"}
    # A target may only be marked flashable after explicit evidence is recorded.
    if target.get("flash_allowed"):
        assert target.get("status") == "tested", f"{target['id']} cannot be flash-enabled before testing"
        assert target.get("bootloader_unlock_confirmed") is True
        assert target.get("treble_enabled") is True
        assert target.get("abi"), f"{target['id']} missing ABI"

print(f"Manifest valid: {len(data['targets'])} targets; no device flashing performed.")
