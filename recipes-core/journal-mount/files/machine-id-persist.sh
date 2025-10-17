#!/bin/sh
DATA_ID="/mnt/data/machine-id"
ETC_ID="/etc/machine-id"

# Exit if /mnt/data does not exist
[ -d /mnt/data ] || exit 0

# Sync machine-id
if [ -f "$DATA_ID" ]; then
    echo "[machine-id-persist] Found existing $DATA_ID, linking to /etc/machine-id"
    ln -sf "$DATA_ID" "$ETC_ID"
else
    echo "[machine-id-persist] No persistent machine-id found, creating..."
    cp "$ETC_ID" "$DATA_ID"
    ln -sf "$DATA_ID" "$ETC_ID"
fi

exit 0
