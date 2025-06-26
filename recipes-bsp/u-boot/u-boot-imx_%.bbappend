FILESEXTRAPATHS:prepend := "${THISDIR}/${PN}:"

SRC_URI += " \
    file://0001-Add-support-for-1GB-board.patch \
    file://0002-add-recovery-script.patch \
    file://0004-Press-ENTER-to-stop-autoboot.patch \    
"
