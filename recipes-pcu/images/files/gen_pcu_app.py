#!/usr/bin/env python3
import argparse
import hashlib
import json
import os
import tarfile
from datetime import datetime, timezone
from pathlib import Path

# defaults are kept for standalone run
DEFAULT_TARGET_NAME = "PCU-IP"
DEFAULT_TARGET_VERSION = "0.0.1"
DEFAULT_TARGET_TYPE = "PECU"

def sha256sum(filepath):
    h = hashlib.sha256()
    with open(filepath, "rb") as f:
        for chunk in iter(lambda: f.read(8192), b""):
            h.update(chunk)
    return h.hexdigest()

def main():
    p = argparse.ArgumentParser(description="Generate upgrade package")
    p.add_argument("--output-dir", "-o", default=None, help="Where to write package.json and final tar.gz")
    p.add_argument("--target-name", default=DEFAULT_TARGET_NAME)
    p.add_argument("--target-version", default=DEFAULT_TARGET_VERSION)
    p.add_argument("--target-type", default=DEFAULT_TARGET_TYPE)
    p.add_argument("--deploy-dir-deb", default=None, help="Path to deploy deb directory")
    p.add_argument("--deb-list", type=lambda s: s.split(","), default=[], help="List of deb files to include")
    args = p.parse_args()

    TARGET_NAME = args.target_name
    TARGET_VERSION = args.target_version
    TARGET_TYPE = args.target_type
    IMAGE_TYPE = "app"
    DEPLOY_DIR_DEB = ""
    deb_files = []

    if args.output_dir:
        OUT_DIR = Path(args.output_dir).resolve()
        os.makedirs(OUT_DIR, exist_ok=True)
    else:
        raise ValueError("Output directory must be specified with --output-dir")

    if args.deploy_dir_deb:
        DEPLOY_DIR_DEB = Path(args.deploy_dir_deb).resolve()
    else:
        raise ValueError("Deploy deb directory must be specified with --deploy-dir-deb")

    for deb_prefix in args.deb_list:
        deb_dir = Path(DEPLOY_DIR_DEB)
        matches = sorted(deb_dir.glob(f"{deb_prefix}_*.deb"))
        if matches:
            latest = matches[-1]
            print(f"find deb: {latest}")
            deb_files.append({
                "name": deb_prefix,
                "fullname": latest.name,
                "path": latest.resolve()
            })
        else:
            raise ValueError(f"not find {deb_prefix}")

    if len(deb_files) == 0:
        raise ValueError("Not found any debs with --deb-list")

    JSON_FILE = OUT_DIR / "package.json"
    TARGET_FILE = OUT_DIR / f"{TARGET_NAME}-{IMAGE_TYPE}-{TARGET_VERSION}.tar.gz"

    print(f"Generating {TARGET_NAME}({TARGET_TYPE})-{IMAGE_TYPE} upgrade package ...")

    result = {
        "target": TARGET_NAME,
        "version": TARGET_VERSION,
        "date": datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S"),
        "devicetype": TARGET_TYPE,
        "debs": []
    }

    for deb in deb_files:
        result['debs'].append({
            "name": deb['name'],
            "fullname": deb['fullname'],
            "sha": sha256sum(deb['path'])
        })

    print(f"deb_files: {deb_files}")

    # JSON
    with open(JSON_FILE, "w") as f:
        json.dump(result, f, indent=4)

    with tarfile.open(TARGET_FILE, "w:gz") as tar:
        tar.add(JSON_FILE, arcname="package.json")
        for deb in deb_files:
            print(f"deb: {deb}")
            tar.add(deb['path'], arcname=deb['fullname'])

    print(f"{TARGET_FILE} created successfully.")


if __name__ == "__main__":
    main()
