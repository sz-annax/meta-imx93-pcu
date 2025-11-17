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
    p = argparse.ArgumentParser(description="Generate PCUV upgrade package")
    p.add_argument("--output-dir", "-o", default=None, help="Where to write package.json and final tar.gz")
    p.add_argument("--image-type", default="factory", help="Type of image: factory rootfs or app")
    p.add_argument("--target-name", default=DEFAULT_TARGET_NAME)
    p.add_argument("--target-version", default=DEFAULT_TARGET_VERSION)
    p.add_argument("--target-type", default=DEFAULT_TARGET_TYPE)
    p.add_argument("--uboot", default=None, help="Path to u-boot image file")
    p.add_argument("--kernel-image", default=None, help="Path to kernel image file")
    p.add_argument("--dtb", default=None, help="Path to device tree blob file")
    p.add_argument("--rootfs", default=None, help="Path to rootfs file")
    p.add_argument("--recovery-dtb", default=None, help="Path to recovery device tree blob file")
    p.add_argument("--recovery-image", default=None, help="Path to recovery image file")
    p.add_argument("--recovery-rootfs", default=None, help="Path to recovery rootfs file")
    args = p.parse_args()

    TARGET_NAME = args.target_name
    TARGET_VERSION = args.target_version
    TARGET_TYPE = args.target_type
    UBOOT_IMAGE = None
    KERNEL_IMAGE = None
    DTB_IMAGE = None
    ROOTFS_IMAGE = None
    RECOVERY_DTB = None
    RECOVERY_IMAGE = None
    RECOVERY_ROOTFS = None

    if args.output_dir:
        OUT_DIR = Path(args.output_dir)
        os.makedirs(OUT_DIR, exist_ok=True)
    else:
        raise ValueError("Output directory must be specified with --output-dir")

    if args.image_type:
        IMAGE_TYPE = args.image_type

    if args.uboot:
        UBOOT_IMAGE = Path(args.uboot)
    
    if args.kernel_image:
        KERNEL_IMAGE = Path(args.kernel_image)

    if args.dtb:
        DTB_IMAGE = Path(args.dtb)

    if args.rootfs:
        ROOTFS_IMAGE = Path(args.rootfs)

    if args.recovery_dtb:
        RECOVERY_DTB = Path(args.recovery_dtb)

    if args.recovery_image:
        RECOVERY_IMAGE = Path(args.recovery_image)

    if args.recovery_rootfs:
        RECOVERY_ROOTFS = Path(args.recovery_rootfs)

    JSON_FILE = OUT_DIR / "package.json"
    TARGET_FILE = OUT_DIR / f"{TARGET_NAME}-{IMAGE_TYPE}-{TARGET_VERSION}.tar.gz"

    files = {
        "uboot": UBOOT_IMAGE,
        "image": KERNEL_IMAGE,
        "dtb": DTB_IMAGE,
        "rootfs": ROOTFS_IMAGE,
        "recoverydtb": RECOVERY_DTB,
        "recoveryimage": RECOVERY_IMAGE,
        "recoverydisk": RECOVERY_ROOTFS
    }

    print(f"Generating {TARGET_NAME}({TARGET_TYPE}) upgrade package ...")

    result = {
        "target": TARGET_NAME,
        "version": TARGET_VERSION,
        "date": datetime.now(timezone.utc).strftime("%Y%m%dT%H%M%S"),
        "devicetype": TARGET_TYPE
    }

    temp_files = []
    for key, p in files.items():
        if p is not None and p.exists() and p.is_file():
            temp_files.append(p)
            sha = sha256sum(p)
            result[key] = {
                "name": p.name,
                "sha": sha
            }
        elif p is not None:
            raise ValueError(f"file not exist: {p}")

    # JSON
    with open(JSON_FILE, "w") as f:
        json.dump(result, f, indent=4)

    # Create tar.gz package
    with tarfile.open(TARGET_FILE, "w:gz", dereference=True) as tar:
        tar.add(JSON_FILE, arcname="package.json")
        for item in temp_files:
            tar.add(item, arcname=item.name)

    print(f"{TARGET_FILE} created successfully.")


if __name__ == "__main__":
    main()
