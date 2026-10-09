SUMMARY = "Chromium Wayland launcher for the IM356 LCD"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

SRC_URI = "file://chromium-lcd.service file://chromium-lcd"
S = "${UNPACKDIR}"

inherit systemd

COMPATIBLE_MACHINE = "^imx6u-lcd$"
RDEPENDS:${PN} = "chromium-ozone-wayland weston-init"
SYSTEMD_SERVICE:${PN} = "chromium-lcd.service"
SYSTEMD_AUTO_ENABLE = "disable"

do_install() {
    install -Dm0644 ${UNPACKDIR}/chromium-lcd.service ${D}${systemd_system_unitdir}/chromium-lcd.service
    install -Dm0644 ${UNPACKDIR}/chromium-lcd ${D}${sysconfdir}/default/chromium-lcd
}

FILES:${PN} += "${systemd_system_unitdir}/chromium-lcd.service"
CONFFILES:${PN} = "${sysconfdir}/default/chromium-lcd"